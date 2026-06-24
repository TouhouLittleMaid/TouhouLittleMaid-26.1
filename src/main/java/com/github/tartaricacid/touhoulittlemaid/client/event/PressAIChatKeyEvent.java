package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.AIConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.OpenMaidAIChatPacket;
import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class PressAIChatKeyEvent {
    @SubscribeEvent
    public static void onOpenConfig(InputEvent.Key event) {
        if (isInGame() && AIConfig.LLM_ENABLED.get() && keyIsMatch(event)) {
            EntityMaid maid = maidCheck();
            if (maid == null) {
                return;
            }
            Minecraft.getInstance().options.keyChat.consumeClick();
            // 先通过服务端鉴权，然后发送同步信息后再打开客户端界面
            ClientPacketDistributor.sendToServer(new OpenMaidAIChatPacket(maid));
        }
    }

    @SuppressWarnings("removal")
    private static boolean keyIsMatch(InputEvent.Key event) {
        KeyMapping keyChat = Minecraft.getInstance().options.keyChat;
        return event.getAction() == GLFW.GLFW_PRESS
               && keyChat.matches(event.getKeyEvent());
    }

    @Nullable
    private static EntityMaid maidCheck() {
        // 玩家不为空或者观察者模式
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return null;
        }
        // 当前鼠标指向了特定的女仆
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return null;
        }
        if (!(entityHitResult.getEntity() instanceof EntityMaid maid)) {
            return null;
        }
        if (!maid.isOwnedBy(player)) {
            return null;
        }
        return maid;
    }

    private static boolean isInGame() {
        Minecraft mc = Minecraft.getInstance();
        // 不能是加载界面
        if (ScreenUtil.hasOverlay()) {
            return false;
        }
        // 不能打开任何 GUI
        if (ScreenUtil.getScreen() != null) {
            return false;
        }
        // 当前窗口捕获鼠标操作
        if (!mc.mouseHandler.isMouseGrabbed()) {
            return false;
        }
        // 选择了当前窗口
        return mc.isWindowActive();
    }
}
