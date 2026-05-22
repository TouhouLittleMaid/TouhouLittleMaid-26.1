package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public final class WirelessIORenderEvent {
    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent.AfterOpaqueBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        ItemStack mainStack = mc.player.getMainHandItem();
        if (mainStack.getItem() != InitItems.WIRELESS_IO.get()) {
            return;
        }
        BlockPos pos = ItemWirelessIO.getBindingPos(mainStack);
        if (pos == null) {
            return;
        }
        Vec3 position = event.getLevelRenderState().cameraRenderState.pos.reverse();
        AABB aabb = new AABB(pos).move(position);
        Gizmos.cuboid(aabb, GizmoStyle.fill(ARGB.colorFromFloat(1.0F, 1.0F, 0, 0)));
    }
}
