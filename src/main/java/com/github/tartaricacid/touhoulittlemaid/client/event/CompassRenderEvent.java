package com.github.tartaricacid.touhoulittlemaid.client.event;


import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemKappaCompass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.OptionalDouble;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public class CompassRenderEvent {
    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        ItemStack stack = mc.player.getMainHandItem();
        if (stack.getItem() != InitItems.KAPPA_COMPASS.get()) {
            stack = mc.player.getOffhandItem();
            if (stack.getItem() != InitItems.KAPPA_COMPASS.get()) {
                return;
            }
        }
        if (!ItemKappaCompass.hasKappaCompassData(stack)) {
            return;
        }
        Identifier dimension = ItemKappaCompass.getDimension(stack);
        if (dimension != null && !mc.player.level.dimension().identifier().equals(dimension)) {
            return;
        }
        BlockPos workPos = ItemKappaCompass.getPoint(Activity.WORK, stack);
        if (workPos != null) {
            double radius = MaidConfig.MAID_WORK_RANGE.get() + 0.1;
            renderArea(workPos, radius, 0xffff0000);
            Vec3 textPos = new Vec3(workPos.getX() + 0.5, workPos.getY() + 2, workPos.getZ() + 0.5);
            String text = I18n.get("message.touhou_little_maid.kappa_compass.work_area");
            renderText(text, textPos.add(0, -0.75, 0), 0xffff1111);
            renderText("▼", textPos.add(0, 0.75, 0), 0xffff1111);
        }

        BlockPos idlePos = ItemKappaCompass.getPoint(Activity.IDLE, stack);
        if (idlePos != null) {
            double radius = MaidConfig.MAID_IDLE_RANGE.get();
            renderArea(idlePos, radius, 0xff00ff00);
            Vec3 textPos = new Vec3(idlePos.getX() + 0.5, idlePos.getY() + 2, idlePos.getZ() + 0.5);
            if (idlePos.equals(workPos)) {
                textPos = textPos.add(0, 1, 0);
            } else if (workPos != null) {
                Gizmos.line(centerPos(idlePos), centerPos(workPos), 0xffffffff);
            }
            String text = I18n.get("message.touhou_little_maid.kappa_compass.idle_area");
            renderText(text, textPos.add(0, -0.75, 0), 0xff11ff11);
            renderText("▼", textPos.add(0, 0.75, 0), 0xff11ff11);
        }

        BlockPos resetPos = ItemKappaCompass.getPoint(Activity.REST, stack);
        if (resetPos != null) {
            double radius = MaidConfig.MAID_SLEEP_RANGE.get() - 0.1;
            renderArea(resetPos, radius, 0xff0000ff);
            Vec3 textPos = new Vec3(resetPos.getX() + 0.5, resetPos.getY() + 2, resetPos.getZ() + 0.5);
            if (resetPos.equals(idlePos)) {
                textPos = textPos.add(0, 2, 0);
            } else if (idlePos != null && workPos != null) {
                Gizmos.line(centerPos(resetPos), centerPos(idlePos), 0xffffffff);
                Gizmos.line(centerPos(resetPos), centerPos(workPos), 0xffffffff);
            }
            String text = I18n.get("message.touhou_little_maid.kappa_compass.sleep_area");
            renderText(text, textPos.add(0, -0.75, 0), 0xff1111ff);
            renderText("▼", textPos.add(0, 0.75, 0), 0xff1111ff);
        }
    }

    private static Vec3 centerPos(BlockPos pos) {
        return Vec3.atCenterOf(pos).add(0, 1, 0);
    }

    private static void renderArea(BlockPos pos, double radius, int color) {
        Gizmos.circle(centerPos(pos), (float) radius, GizmoStyle.stroke(color));
    }

    private static void renderText(String text, Vec3 pos, int color) {
        Gizmos.billboardText(text, pos, new TextGizmo.Style(color, 1.5f, OptionalDouble.empty())).setAlwaysOnTop();
    }
}
