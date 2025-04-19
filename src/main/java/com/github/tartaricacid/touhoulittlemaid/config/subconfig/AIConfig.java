package com.github.tartaricacid.touhoulittlemaid.config.subconfig;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.AvailableSites;
import net.neoforged.neoforge.common.ModConfigSpec;

public class AIConfig {
    private static final String TRANSLATE_KEY = "config.touhou_little_maid.global_ai";
    public static ModConfigSpec.BooleanValue CHAT_ENABLED;
    public static ModConfigSpec.DoubleValue CHAT_TEMPERATURE;
    public static ModConfigSpec.ConfigValue<String> CHAT_PROXY_ADDRESS;
    public static ModConfigSpec.BooleanValue TTS_ENABLED;
    public static ModConfigSpec.ConfigValue<String> TTS_LANGUAGE;
    public static ModConfigSpec.ConfigValue<String> TTS_PROXY_ADDRESS;
    public static ModConfigSpec.ConfigValue<String> STT_URL;
    public static ModConfigSpec.ConfigValue<String> STT_PROXY_ADDRESS;
    public static ModConfigSpec.IntValue MAID_MAX_HISTORY_CHAT_SIZE;

    public static void init(ModConfigSpec.Builder builder) {
        // 读取网站列表
        AvailableSites.readSites();

        builder.translation(TRANSLATE_KEY).push("ai");

        builder.comment("Whether or not to enable the AI Chat feature").translation(translateKey("chat_enable"));
        CHAT_ENABLED = builder.define("ChatEnabled", true);

        builder.comment("Chat temperature, the higher this value, the more random the output will be").translation(translateKey("chat_temperature"));
        CHAT_TEMPERATURE = builder.defineInRange("ChatTemperature", 0.5, 0, 2);

        builder.comment("Chat AI Proxy Address, such as 127.0.0.1:1080, empty is no proxy, SOCKS proxies are not supported");
        CHAT_PROXY_ADDRESS = builder.define("ChatProxyAddress", "");

        builder.comment("Whether or not to enable the TTS feature").translation(translateKey("tts_enable"));
        TTS_ENABLED = builder.define("TTSEnabled", true);

        builder.comment("The TTS language you intend to use").translation(translateKey("tts_language"));
        TTS_LANGUAGE = builder.define("TTSLanguage", "en_us");

        builder.comment("TTS Proxy Address, such as 127.0.0.1:1080, empty is no proxy, SOCKS proxies are not supported");
        TTS_PROXY_ADDRESS = builder.define("TTSProxyAddress", "");

        builder.comment("STT Url address, currently only the player2 app is supported");
        STT_URL = builder.define("STTUrl", "http://127.0.0.1:4315/v1/stt");

        builder.comment("STT Proxy Address, such as 127.0.0.1:1080, empty is no proxy, SOCKS proxies are not supported");
        STT_PROXY_ADDRESS = builder.define("STTProxyAddress", "");

        builder.comment("The maximum historical conversation length cached by the maid").translation(translateKey("maid_max_history_chat_size"));
        MAID_MAX_HISTORY_CHAT_SIZE = builder.defineInRange("MaidMaxHistoryChatSize", 16, 1, 128);

        builder.pop();
    }

    private static String translateKey(String key) {
        return TRANSLATE_KEY + "." + key;
    }
}
