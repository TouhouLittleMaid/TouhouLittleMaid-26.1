package com.github.tartaricacid.touhoulittlemaid.network.client.ai;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.AIChatScreen;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.SyncMaidAIDataPacket;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;

public final class SyncMaidAIDataPacketProxy {
    public static void handle(SyncMaidAIDataPacket message) {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            ScreenUtil.setScreen(null);
            return;
        }
        Entity entity = level.getEntity(message.entityId());
        if (entity instanceof EntityMaid maid) {
            var input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), message.configData());
            maid.getAiChatManager().read(input);
            AIChatScreen chatScreen = new AIChatScreen(maid);
            chatScreen.updateTokens(message.currentTokens(), message.maxTokens());
            ScreenUtil.setScreen(chatScreen);
        } else {
            ScreenUtil.setScreen(null);
        }
    }
}
