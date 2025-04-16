package com.github.tartaricacid.touhoulittlemaid.ai.service;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.entity.HistoryChat;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.entity.MaidAIChatManager;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.Site;
import com.github.tartaricacid.touhoulittlemaid.ai.service.chat.openai.ChatClient;
import com.github.tartaricacid.touhoulittlemaid.ai.service.chat.openai.request.ChatCompletion;
import com.github.tartaricacid.touhoulittlemaid.ai.service.chat.openai.request.ResponseFormat;
import com.github.tartaricacid.touhoulittlemaid.ai.service.chat.openai.request.Role;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSClient;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSFactory;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSRequest;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.AIConfig;
import com.github.tartaricacid.touhoulittlemaid.util.CappedQueue;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.time.Duration;

public final class Service {
    public static final Gson GSON = new Gson();
    private static final HttpClient CHAT_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .proxy(new ConfigProxySelector(AIConfig.CHAT_PROXY_ADDRESS))
            .build();
    private static final HttpClient TTS_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .proxy(new ConfigProxySelector(AIConfig.TTS_PROXY_ADDRESS))
            .build();

    public static ChatClient getChatClient(Site site) {
        String chatApiKey = site.getApiKey();
        String chatBaseUrl = site.getUrl();
        return ChatClient.create(CHAT_HTTP_CLIENT)
                .apiKey(chatApiKey)
                .baseUrl(chatBaseUrl);
    }

    @Nullable
    public static ChatCompletion getChatCompletion(MaidAIChatManager chatManager, String language) {
        // 获取设定文件
        return chatManager.getSetting().map(s -> {
            String setting = s.getSetting(chatManager.getMaid(), language);
            String model = chatManager.getChatModel();
            double chatTemperature = chatManager.getChatTemperature();
            CappedQueue<HistoryChat> history = chatManager.getHistory();

            // 构建对话
            ChatCompletion chatCompletion = ChatCompletion.create()
                    .model(model)
                    .temperature(chatTemperature)
                    .setResponseFormat(ResponseFormat.json())
                    .systemChat(setting);

            // 倒序遍历，将历史对话加载进去
            history.getDeque().descendingIterator().forEachRemaining(historyChat -> {
                Role role = historyChat.role();
                String message = historyChat.message();
                if (role.equals(Role.USER)) {
                    chatCompletion.userChat(message);
                } else if (role.equals(Role.ASSISTANT)) {
                    chatCompletion.assistantChat(message);
                }
            });

            return chatCompletion;
        }).orElse(null);
    }

    @Nullable
    public static TTSClient<?> getTtsClient(Site site) {
        return TTSFactory.getTtsClient(TTS_HTTP_CLIENT, site);
    }

    @Nullable
    public static TTSRequest getTtsRequest(Site site, String ttsText, String ttsLang, String model) {
        return TTSFactory.getTtsRequest(site, ttsText, ttsLang, model);
    }
}
