package com.github.tartaricacid.touhoulittlemaid.network.client.ai;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.AIChatScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.editor.LLMSiteEditorScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.editor.TTSSiteEditorScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsHubScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsLLMSiteScreen;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.SyncAISitesPacket;
import net.minecraft.client.Minecraft;

public final class SyncAISitesPacketProxy {
    public static void handle(SyncAISitesPacket message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof LLMSiteEditorScreen editor) {
            editor.getParentHub().reopenSelf(message.llmSites(), message.ttsSites());
        } else if (mc.screen instanceof TTSSiteEditorScreen editor) {
            editor.getParentHub().reopenSelf(message.llmSites(), message.ttsSites());
        } else if (mc.screen instanceof AIChatSettingsHubScreen hubScreen) {
            hubScreen.reopenSelf(message.llmSites(), message.ttsSites());
        } else if (mc.screen instanceof AIChatScreen screen) {
            mc.setScreen(new AIChatSettingsLLMSiteScreen(screen, message.llmSites(), message.ttsSites(), message.insufficientPermissions()));
        } else {
            mc.setScreen(AIChatSettingsHubScreen.openDefault(null, message.llmSites(), message.ttsSites(), message.insufficientPermissions()));
        }
    }
}
