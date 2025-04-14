package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.response.ResponseChat;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.Site;
import com.github.tartaricacid.touhoulittlemaid.ai.service.Service;
import com.github.tartaricacid.touhoulittlemaid.ai.service.TTSApiType;
import com.github.tartaricacid.touhoulittlemaid.ai.service.fishaudio.TTSClient;
import com.github.tartaricacid.touhoulittlemaid.ai.service.fishaudio.request.TTSRequest;
import com.github.tartaricacid.touhoulittlemaid.ai.service.openai.ChatClient;
import com.github.tartaricacid.touhoulittlemaid.ai.service.openai.request.ChatCompletion;
import com.github.tartaricacid.touhoulittlemaid.ai.service.openai.response.ChatCompletionResponse;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.AIConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManger;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.TTSAudioToClientPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.TTSSystemAudioToClientPackage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public final class MaidAIChatManager extends MaidAIChatData {
    public MaidAIChatManager(EntityMaid maid) {
        super(maid);
    }

    public void chat(String message, String language) {
        if (AIConfig.CHAT_ENABLED.get()) {
            @Nullable Site site = this.getChatSite();
            if (site == null || StringUtils.isBlank(site.getApiKey())) {
                ChatBubbleManger.addInnerChatText(maid, "ai.touhou_little_maid.chat.api_key.empty");
            } else {
                ChatClient chatClient = Service.getChatClient(site);
                ChatCompletion chatCompletion = Service.getChatCompletion(this, language);
                if (chatCompletion != null) {
                    chatCompletion.userChat(message);
                    chatClient.chat(chatCompletion).handle(this::onShowChatSync, this::onChatFailSync);
                    this.addUserHistory(message);
                } else {
                    ChatBubbleManger.addInnerChatText(maid, "ai.touhou_little_maid.chat.no_setting");
                }
            }
        } else {
            ChatBubbleManger.addInnerChatText(maid, "ai.touhou_little_maid.chat.disable");
        }
    }

    private void tts(Site site, String chatText, String ttsText) {
        // 调用系统 TTS，那么此时就只需要发送给指定的玩家即可
        if (TTSApiType.SYSTEM.getName().equals(site.getApiType())) {
            onPlaySoundLocal(chatText, ttsText);
        } else {
            TTSClient ttsClient = Service.getTtsClient(site);
            TTSRequest ttsRequest = Service.getTtsRequest(this.getTtsModel(), ttsText);
            ttsClient.request(ttsRequest).handle(data -> onPlaySoundSync(chatText, data), throwable -> onTtsFailSync(chatText, throwable));
        }
    }

    private void onShowChatSync(ChatCompletionResponse result) {
        String rawMessage = result.getFirstChoiceMessage();
        try {
            ResponseChat responseChat = Service.GSON.fromJson(rawMessage, ResponseChat.class);
            if (responseChat == null) {
                TouhouLittleMaid.LOGGER.error("Error in Response Chat: {}", rawMessage);
                onChatFailSync(Component.translatable("ai.touhou_little_maid.chat.format.json_format_error", rawMessage));
                return;
            }
            String chatText = responseChat.getChatText();
            String ttsText = responseChat.getTtsText();
            if (StringUtils.isBlank(chatText) || StringUtils.isBlank(ttsText)) {
                TouhouLittleMaid.LOGGER.error("Error in Response Chat: {}", rawMessage);
                onChatFailSync(Component.translatable("ai.touhou_little_maid.chat.format.text_is_empty", rawMessage));
                return;
            }
            this.addAssistantHistory(rawMessage);
            Site site = this.getTtsSite();
            if (AIConfig.TTS_ENABLED.get() && site != null && StringUtils.isNotBlank(site.getApiKey())) {
                this.tts(site, chatText, ttsText);
            } else {
                ChatBubbleManger.addAiChatTextSync(maid, chatText);
            }
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.error(e.getMessage());
        }
    }

    private void onChatFailSync(Throwable throwable) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            if (maid.getOwner() instanceof ServerPlayer player) {
                String cause = throwable.getLocalizedMessage();
                player.sendSystemMessage(Component.translatable("ai.touhou_little_maid.chat.connect.fail")
                        .append(cause).withStyle(ChatFormatting.RED));
            }
        });
    }

    private void onChatFailSync(Component message) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            if (maid.getOwner() instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.translatable("ai.touhou_little_maid.chat.connect.fail")
                        .append(message).withStyle(ChatFormatting.RED));
            }
        });
    }

    private void onPlaySoundLocal(String chatText, String ttsText) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            if (maid.getOwner() instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new TTSSystemAudioToClientPackage(ttsText));
            }
            ChatBubbleManger.addAiChatText(maid, chatText);
        });
    }

    private void onPlaySoundSync(String chatText, byte[] data) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            NetworkHandler.sendToNearby(maid, new TTSAudioToClientPackage(this.maid.getId(), data));
            ChatBubbleManger.addAiChatText(maid, chatText);
        });
    }

    private void onTtsFailSync(String chatText, Throwable throwable) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            ChatBubbleManger.addAiChatText(maid, chatText);
            if (maid.getOwner() instanceof ServerPlayer player) {
                String cause = throwable.getLocalizedMessage();
                player.sendSystemMessage(Component.translatable("ai.touhou_little_maid.tts.connect.fail")
                        .append(cause).withStyle(ChatFormatting.RED));
            }
        });
    }
}
