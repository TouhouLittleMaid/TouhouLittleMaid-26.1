package com.github.tartaricacid.touhoulittlemaid.network.client.ai;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.AIChatScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.editor.LLMSiteEditorScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.editor.TTSSiteEditorScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsHubScreen;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsLLMSiteScreen;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.SyncAISitesPacket;
import com.github.tartaricacid.touhoulittlemaid.util.ScreenUtil;
import net.minecraft.client.Minecraft;

public final class SyncAISitesPacketProxy {
    public static void handle(SyncAISitesPacket message) {
        Minecraft mc = Minecraft.getInstance();
        if (ScreenUtil.getScreen() instanceof LLMSiteEditorScreen editor) {
            editor.getParentHub().reopenSelf(message.llmSites(), message.ttsSites());
        } else if (ScreenUtil.getScreen() instanceof TTSSiteEditorScreen editor) {
            editor.getParentHub().reopenSelf(message.llmSites(), message.ttsSites());
        } else if (ScreenUtil.getScreen() instanceof AIChatSettingsHubScreen hubScreen) {
            hubScreen.reopenSelf(message.llmSites(), message.ttsSites());
        } else if (ScreenUtil.getScreen() instanceof AIChatScreen screen) {
            ScreenUtil.setScreen(new AIChatSettingsLLMSiteScreen(screen, message.llmSites(), message.ttsSites(), message.insufficientPermissions()));
        } else {
            ScreenUtil.setScreen(AIChatSettingsHubScreen.openDefault(null, message.llmSites(), message.ttsSites(), message.insufficientPermissions()));
        }
    }
}
