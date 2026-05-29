package com.github.tartaricacid.touhoulittlemaid.network.message.ai;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.site.ClientAvailableSitesSync;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.AIChatScreen;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.AIConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;

public record SyncMaidAIDataPacket(int entityId, CompoundTag configData, int currentTokens,
                                   int maxTokens) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncMaidAIDataPacket> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("sync_maid_ai_data"));
    public static final StreamCodec<ByteBuf, SyncMaidAIDataPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncMaidAIDataPacket decode(ByteBuf byteBuf) {
            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
            int entityId = buf.readVarInt();
            CompoundTag configData = Objects.requireNonNullElse(buf.readNbt(), new CompoundTag());
            ClientAvailableSitesSync.readFromNetwork(buf);
            int currentTokens = buf.readVarInt();
            int maxTokens = buf.readVarInt();
            return new SyncMaidAIDataPacket(entityId, configData, currentTokens, maxTokens);
        }

        @Override
        public void encode(ByteBuf byteBuf, SyncMaidAIDataPacket message) {
            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
            buf.writeVarInt(message.entityId);
            buf.writeNbt(message.configData);
            ClientAvailableSitesSync.writeToNetwork(buf);
            buf.writeVarInt(message.currentTokens);
            buf.writeVarInt(message.maxTokens);
        }
    };

    public SyncMaidAIDataPacket(EntityMaid maid, ServerPlayer player) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.level.registryAccess());
        maid.getAiChatManager().save(output);
        this(maid.getId(), output.buildResult(),
                player.getData(InitDataAttachment.CHAT_TOKENS).get(),
                AIConfig.MAX_TOKENS_PER_PLAYER.get()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncMaidAIDataPacket message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handle(message));
        }
    }

    private static void handle(SyncMaidAIDataPacket message) {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }
        Entity entity = level.getEntity(message.entityId);
        if (entity instanceof EntityMaid maid) {
            var input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), message.configData);
            maid.getAiChatManager().read(input);
            AIChatScreen chatScreen = new AIChatScreen(maid);
            chatScreen.updateTokens(message.currentTokens, message.maxTokens);
            Minecraft.getInstance().setScreen(chatScreen);
        } else {
            Minecraft.getInstance().setScreen(null);
        }
    }
}
