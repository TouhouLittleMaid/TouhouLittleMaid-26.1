package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.CharacterSetting;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.SettingReader;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.site.AvailableSites;
import com.github.tartaricacid.touhoulittlemaid.ai.service.SupportModelSelect;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.DefaultLLMSite;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.LLMMessage;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.LLMSite;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.openai.response.ToolCall;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSSite;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.system.TTSSystemSite;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.AIConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.CappedQueue;
import com.google.common.collect.Lists;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@SuppressWarnings("all")
public abstract class MaidAIChatData extends MaidAIChatSerializable {
    protected static final String MAID_HISTORY_CHAT_TAG = "MaidHistoryChat";
    protected static final String MAID_HISTORY_SUMMARY_TAG = "MaidHistorySummary";
    protected static final String MAID_LAST_CHAT_TOKEN_USAGE_TAG = "MaidLastChatTokenUsage";

    protected final EntityMaid maid;
    protected final CappedQueue<LLMMessage> history;

    protected String compressedSummary = StringUtils.EMPTY;
    protected volatile int lastChatTokenUsage = 0;
    public volatile boolean historySummaryRunning = false;

    public MaidAIChatData(EntityMaid maid) {
        this.maid = maid;
        this.history = new CappedQueue<>(512);
    }

    @Override
    public void read(ValueInput input) {
        super.read(input);
        input.read(MAID_HISTORY_CHAT_TAG, LLMMessage.CODEC.listOf()).ifPresent(list -> {
            this.history.getDeque().clear();
            ListIterator<LLMMessage> iterator = list.listIterator(list.size());
            while (iterator.hasPrevious()) {
                history.add(iterator.previous());
            }
        });

        this.compressedSummary = input.getStringOr(MAID_HISTORY_SUMMARY_TAG, StringUtils.EMPTY);
        this.lastChatTokenUsage = input.getIntOr(MAID_LAST_CHAT_TOKEN_USAGE_TAG, 0);
    }

    @Override
    public void save(ValueOutput output) {
        super.save(output);
        if (this.history.size() > 0) {
            ArrayList<LLMMessage> llmMessages = Lists.newArrayList(this.history.getDeque());
            output.store(MAID_HISTORY_CHAT_TAG, LLMMessage.CODEC.listOf(), llmMessages);
        }
        output.putString(MAID_HISTORY_SUMMARY_TAG, this.compressedSummary);
        output.putInt(MAID_LAST_CHAT_TOKEN_USAGE_TAG, this.lastChatTokenUsage);
    }

    @Nullable
    public LLMSite getLLMSite() {
        LLMSite site;
        if (StringUtils.isBlank(llmSite)) {
            site = getDefaultLLMSite();
        } else {
            site = AvailableSites.getLLMSite(llmSite);
            if (site == null || !site.enabled()) {
                site = getDefaultLLMSite();
            }
        }
        return site;
    }

    private LLMSite getDefaultLLMSite() {
        LLMSite site = AvailableSites.getLLMSite(DefaultLLMSite.DEEPSEEK.id());
        return site == null ? DefaultLLMSite.DEEPSEEK : site;
    }

    @Nullable
    public TTSSite getTTSSite() {
        if (isNoTTSSite(ttsSite)) {
            return null;
        }

        TTSSite site;
        if (StringUtils.isBlank(ttsSite)) {
            site = AvailableSites.getTTSSite(TTSSystemSite.API_TYPE);
        } else {
            site = AvailableSites.getTTSSite(ttsSite);
            if (site == null || !site.enabled()) {
                site = AvailableSites.getTTSSite(TTSSystemSite.API_TYPE);
            }
        }
        return site;
    }

    public String getLLMModel() {
        LLMSite site = getLLMSite();
        String model = StringUtils.EMPTY;
        if (site instanceof SupportModelSelect select) {
            if (StringUtils.isBlank(llmModel)) {
                model = select.getDefaultModel();
            } else {
                model = select.getModel(llmModel);
            }
        }
        return model;
    }

    public String getTTSModel() {
        TTSSite site = getTTSSite();
        String model = StringUtils.EMPTY;
        if (site instanceof SupportModelSelect select) {
            if (StringUtils.isBlank(ttsModel)) {
                model = select.getDefaultModel();
            } else {
                model = select.getModel(ttsModel);
            }
        }
        return model;
    }

    public String getTTSLanguage() {
        if (StringUtils.isNotBlank(ttsLanguage)) {
            return ttsLanguage;
        }
        return AIConfig.TTS_LANGUAGE.get();
    }

    public String getChatLanguage() {
        if (StringUtils.isNotBlank(chatLanguage)) {
            return chatLanguage;
        }
        return "en_us";
    }

    public CappedQueue<LLMMessage> getHistory() {
        return history;
    }

    public String getCompressedSummary() {
        return compressedSummary;
    }

    public void setCompressedSummary(String newSummary) {
        this.compressedSummary = newSummary;
    }

    public boolean hasCompressedSummary() {
        return StringUtils.isNotBlank(compressedSummary);
    }

    public int getLastChatTokenUsage() {
        return lastChatTokenUsage;
    }

    public void setLastChatTokenUsage(int lastChatTokenUsage) {
        this.lastChatTokenUsage = Math.max(0, lastChatTokenUsage);
    }

    public void clearAllChatMemory() {
        this.history.getDeque().clear();
        this.compressedSummary = StringUtils.EMPTY;
        this.lastChatTokenUsage = 0;
        this.historySummaryRunning = false;
    }

    public void addUserHistory(String message) {
        this.history.add(LLMMessage.userChat(maid, message));
        this.onHistoryUpdated();
    }

    public void addAssistantHistory(String message) {
        this.history.add(LLMMessage.assistantChat(maid, message));
        this.onHistoryUpdated();
    }

    public void addAssistantHistory(String message, List<ToolCall> toolCalls) {
        this.history.add(LLMMessage.assistantChat(maid, message, toolCalls));
        this.onHistoryUpdated();
    }

    public void addToolHistory(String message, String toolCallId) {
        this.history.add(LLMMessage.toolChat(maid, message, toolCallId));
        this.onHistoryUpdated();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public Optional<CharacterSetting> getSetting() {
        String modelId = this.maid.getModelId();
        return SettingReader.getSetting(modelId);
    }

    protected void onHistoryUpdated() {
    }
}
