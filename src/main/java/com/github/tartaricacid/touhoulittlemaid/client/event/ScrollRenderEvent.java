package com.github.tartaricacid.touhoulittlemaid.client.event;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFoxScroll;
import com.github.tartaricacid.touhoulittlemaid.item.ItemServantBell;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Optional;
import java.util.OptionalDouble;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public class ScrollRenderEvent {
    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }
        Optional<ItemFoxScroll.TrackInfo> trackInfo = getInfo(player, player.getMainHandItem());
        if (trackInfo.isEmpty()) {
            return;
        }

        ItemFoxScroll.TrackInfo info = trackInfo.get();
        String dimension = info.dimension();
        Vec3 trackVec = Vec3.upFromBottomCenterOf(info.position(), 1);
        if (!dimension.equals(player.level.dimension().identifier().toString())) {
            return;
        }
        Vec3 playerVec = player.position();
        double actualDistance = playerVec.distanceTo(trackVec);
        if (actualDistance < 5) {
            return;
        }

        double viewDistance = actualDistance;
        double maxRenderDistance = mc.options.renderDistance().get() * 16;
        if (actualDistance > maxRenderDistance) {
            Vec3 delta = trackVec.subtract(playerVec).normalize();
            trackVec = playerVec.add(delta.x * maxRenderDistance, delta.y * maxRenderDistance, delta.z * maxRenderDistance);
            viewDistance = maxRenderDistance;
        }
        float scale = 0.2f * (((float) viewDistance + 4.0f) / 3.0f);

        Gizmos.billboardText(
                Math.round(actualDistance) + " m", trackVec.add(0, 0.75 * scale, 0),
                new TextGizmo.Style(0xffff8800, scale, OptionalDouble.empty())
        ).setAlwaysOnTop();

        Gizmos.billboardText(
                "▼", trackVec,
                new TextGizmo.Style(0xffff0000, scale * 1.2f, OptionalDouble.empty())
        ).setAlwaysOnTop();
    }

    private static Optional<ItemFoxScroll.TrackInfo> getInfo(Player player, ItemStack stack) {
        if (stack.getItem() instanceof ItemFoxScroll) {
            var trackInfo = ItemFoxScroll.getTrackInfo(player.getMainHandItem());
            return Optional.ofNullable(trackInfo);
        }
        if (stack.is(InitItems.SERVANT_BELL.get())) {
            var maidShow = ItemServantBell.getMaidShow(stack);
            return Optional.ofNullable(maidShow);
        }
        return Optional.empty();
    }
}
