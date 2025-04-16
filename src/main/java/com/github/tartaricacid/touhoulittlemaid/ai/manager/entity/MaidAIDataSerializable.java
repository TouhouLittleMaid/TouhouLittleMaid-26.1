package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class MaidAIDataSerializable {
    public static final StreamCodec<ByteBuf, MaidAIDataSerializable> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf buffer, MaidAIDataSerializable message) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.chatSiteName);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.chatModel);
            ByteBufCodecs.DOUBLE.encode(buffer, message.chatTemperature);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.ttsSiteName);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.ttsModel);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.ttsLanguage);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.ownerName);
            ByteBufCodecs.STRING_UTF8.encode(buffer, message.customSetting);
        }

        @Override
        public MaidAIDataSerializable decode(ByteBuf buffer) {
            String chatSiteName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String chatModel = ByteBufCodecs.STRING_UTF8.decode(buffer);
            double chatTemperature = ByteBufCodecs.DOUBLE.decode(buffer);
            String ttsSiteName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String ttsModel = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String ttsLanguage = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String ownerName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String customSetting = ByteBufCodecs.STRING_UTF8.decode(buffer);
            return new MaidAIDataSerializable(chatSiteName, chatModel, chatTemperature, ttsSiteName, ttsModel, ttsLanguage, ownerName, customSetting);
        }
    };

    protected String chatSiteName = "";
    protected String chatModel = "";
    protected double chatTemperature = -1;
    protected String ttsSiteName = "";
    protected String ttsModel = "";
    protected String ttsLanguage = "";
    protected String ownerName = "";
    protected String customSetting = "";

    public MaidAIDataSerializable() {
    }

    public MaidAIDataSerializable(String chatSiteName, String chatModel, double chatTemperature, String ttsSiteName,
                                  String ttsModel, String ttsLanguage, String ownerName, String customSetting) {
        this.chatSiteName = chatSiteName;
        this.chatModel = chatModel;
        this.chatTemperature = chatTemperature;
        this.ttsSiteName = ttsSiteName;
        this.ttsModel = ttsModel;
        this.ttsLanguage = ttsLanguage;
        this.ownerName = ownerName;
        this.customSetting = customSetting;
    }

    public void copyFrom(MaidAIDataSerializable data) {
        chatSiteName = data.chatSiteName;
        chatModel = data.chatModel;
        chatTemperature = data.chatTemperature;
        ttsSiteName = data.ttsSiteName;
        ttsModel = data.ttsModel;
        ttsLanguage = data.ttsLanguage;
        ownerName = data.ownerName;
        customSetting = data.customSetting;
    }

    public void readFromTag(CompoundTag tag) {
        if (tag.contains("MaidAIChatData")) {
            CompoundTag data = tag.getCompound("MaidAIChatData");
            chatSiteName = data.getString("ChatSiteName");
            chatModel = data.getString("ChatModel");
            chatTemperature = data.getDouble("ChatTemperature");
            ttsSiteName = data.getString("TtsSiteName");
            ttsModel = data.getString("TtsModel");
            ttsLanguage = data.getString("TtsLanguage");
            ownerName = data.getString("OwnerName");
            customSetting = data.getString("CustomSetting");
        }
    }

    public void writeToTag(CompoundTag tag) {
        CompoundTag data = new CompoundTag();
        {
            data.putString("ChatSiteName", chatSiteName);
            data.putString("ChatModel", chatModel);
            data.putDouble("ChatTemperature", chatTemperature);
            data.putString("TtsSiteName", ttsSiteName);
            data.putString("TtsModel", ttsModel);
            data.putString("TtsLanguage", ttsLanguage);
            data.putString("OwnerName", ownerName);
            data.putString("CustomSetting", customSetting);
        }
        tag.put("MaidAIChatData", data);
    }

    public String getChatSiteName() {
        return chatSiteName;
    }

    public void setChatSiteName(String chatSiteName) {
        this.chatSiteName = chatSiteName;
    }

    public String getChatModel() {
        return chatModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    public double getChatTemperature() {
        return chatTemperature;
    }

    public void setChatTemperature(double chatTemperature) {
        this.chatTemperature = chatTemperature;
    }

    public String getTtsSiteName() {
        return ttsSiteName;
    }

    public void setTtsSiteName(String ttsSiteName) {
        this.ttsSiteName = ttsSiteName;
    }

    public String getTtsModel() {
        return ttsModel;
    }

    public void setTtsModel(String ttsModel) {
        this.ttsModel = ttsModel;
    }

    public String getTtsLanguage() {
        return ttsLanguage;
    }

    public void setTtsLanguage(String ttsLanguage) {
        this.ttsLanguage = ttsLanguage;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCustomSetting() {
        return customSetting;
    }

    public void setCustomSetting(String customSetting) {
        this.customSetting = customSetting;
    }
}
