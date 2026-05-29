package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MaidAIChatSerializable {
    public static final String NO_TTS_SITE = "__none__";

    public String llmSite = "";
    public String llmModel = "";

    public String ttsSite = "";
    public String ttsModel = "";
    public String ttsLanguage = "";
    public String chatLanguage = "";

    public String ownerName = "";
    public String customSetting = "";

    /**
     * 哨兵值，如果为此值，说明此时对当前女仆禁用 TTS 功能
     */
    public static boolean isNoTTSSite(String siteId) {
        return NO_TTS_SITE.equals(siteId);
    }

    public void decode(FriendlyByteBuf buf) {
        llmSite = buf.readUtf();
        llmModel = buf.readUtf();
        ttsSite = buf.readUtf();
        ttsModel = buf.readUtf();
        ttsLanguage = buf.readUtf();
        chatLanguage = buf.readUtf();
        ownerName = buf.readUtf();
        customSetting = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(llmSite);
        buf.writeUtf(llmModel);
        buf.writeUtf(ttsSite);
        buf.writeUtf(ttsModel);
        buf.writeUtf(ttsLanguage);
        buf.writeUtf(chatLanguage);
        buf.writeUtf(ownerName);
        buf.writeUtf(customSetting);
    }

    public void copyFrom(MaidAIChatSerializable data) {
        llmSite = data.llmSite;
        llmModel = data.llmModel;
        ttsSite = data.ttsSite;
        ttsModel = data.ttsModel;
        ttsLanguage = data.ttsLanguage;
        chatLanguage = data.chatLanguage;
        ownerName = data.ownerName;
        customSetting = data.customSetting;
    }

    public void read(ValueInput input) {
        ValueInput inputChild = input.childOrEmpty("MaidAIChat");
        llmSite = inputChild.getStringOr("LLMSite", "");
        llmModel = inputChild.getStringOr("LLMModel", "");
        ttsSite = inputChild.getStringOr("TTSSiteName", "");
        ttsModel = inputChild.getStringOr("TTSModel", "");
        ttsLanguage = inputChild.getStringOr("TTSLanguage", "");
        chatLanguage = inputChild.getStringOr("ChatLanguage", "");
        ownerName = inputChild.getStringOr("OwnerName", "");
        customSetting = inputChild.getStringOr("CustomSetting", "");
    }

    public void save(ValueOutput output) {
        ValueOutput outputChild = output.child("MaidAIChat");
        outputChild.putString("LLMSite", llmSite);
        outputChild.putString("LLMModel", llmModel);
        outputChild.putString("TTSSiteName", ttsSite);
        outputChild.putString("TTSModel", ttsModel);
        outputChild.putString("TTSLanguage", ttsLanguage);
        outputChild.putString("ChatLanguage", chatLanguage);
        outputChild.putString("OwnerName", ownerName);
        outputChild.putString("CustomSetting", customSetting);
    }
}
