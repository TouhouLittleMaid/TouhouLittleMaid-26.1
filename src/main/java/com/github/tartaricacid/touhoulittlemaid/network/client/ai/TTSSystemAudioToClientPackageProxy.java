package com.github.tartaricacid.touhoulittlemaid.network.client.ai;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.site.AvailableSites;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSSite;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.TTSSystemAudioToClientPackage;

public final class TTSSystemAudioToClientPackageProxy {
    public static void handle(TTSSystemAudioToClientPackage message) {
        TTSSite ttsSite = AvailableSites.getTTSSite(message.siteName());
        if (ttsSite == null || !ttsSite.enabled()) {
            return;
        }
        ttsSite.client().play(message.chatText(), message.config(), null);
    }
}
