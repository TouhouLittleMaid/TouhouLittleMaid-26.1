package com.github.tartaricacid.touhoulittlemaid.client.init;

import com.github.tartaricacid.touhoulittlemaid.client.model.DebugFloorModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.*;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.*;
import com.github.tartaricacid.touhoulittlemaid.entity.item.*;
import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityThrowPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class InitEntitiesRender {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        EntityRenderers.register(EntityMaid.TYPE, EntityMaidRenderer::new);
        EntityRenderers.register(EntityChair.TYPE, EntityChairRenderer::new);
        EntityRenderers.register(EntityFairy.TYPE, EntityFairyRenderer::new);
        EntityRenderers.register(EntityDanmaku.TYPE, EntityDanmakuRenderer::new);
        EntityRenderers.register(EntityPowerPoint.TYPE, EntityPowerPointRenderer::new);
        EntityRenderers.register(EntityExtinguishingAgent.TYPE, EntityExtinguishingAgentRenderer::new);
        EntityRenderers.register(EntityBox.TYPE, EntityBoxRender::new);
        EntityRenderers.register(EntityThrowPowerPoint.TYPE, ThrownItemRenderer::new);
        EntityRenderers.register(EntityTombstone.TYPE, EntityTombstoneRenderer::new);
        EntityRenderers.register(EntitySit.TYPE, EntitySitRenderer::new);
        EntityRenderers.register(EntityBroom.TYPE, EntityBroomRender::new);
        EntityRenderers.register(MaidFishingHook.TYPE, MaidFishingHookRenderer::new);

        BlockEntityRenderers.register(InitBlocks.ALTAR_BE.get(), BlockEntityAltarRenderer::new);
        BlockEntityRenderers.register(InitBlocks.STATUE_BE.get(), BlockEntityStatueRenderer::new);
        BlockEntityRenderers.register(InitBlocks.GARAGE_KIT_BE.get(), BlockEntityGarageKitRenderer::new);
        BlockEntityRenderers.register(InitBlocks.GOMOKU_BE.get(), BlockEntityGomokuRenderer::new);
        BlockEntityRenderers.register(InitBlocks.CCHESS_BE.get(), BlockEntityCChessRenderer::new);
        BlockEntityRenderers.register(InitBlocks.WCHESS_BE.get(), BlockEntityWChessRenderer::new);
        BlockEntityRenderers.register(InitBlocks.KEYBOARD_BE.get(), BlockEntityKeyboardRenderer::new);
        BlockEntityRenderers.register(InitBlocks.BOOKSHELF_BE.get(), BlockEntityBookshelfRenderer::new);
        BlockEntityRenderers.register(InitBlocks.COMPUTER_BE.get(), BlockEntityComputerRenderer::new);
        BlockEntityRenderers.register(InitBlocks.SHRINE_BE.get(), BlockEntityShrineRenderer::new);
        BlockEntityRenderers.register(InitBlocks.PICNIC_MAT_BE.get(), PicnicMatRender::new);
        BlockEntityRenderers.register(InitBlocks.MAID_BED_BE.get(), BlockEntityMaidBedRenderer::new);
        BlockEntityRenderers.register(InitBlocks.SNACK_CABINET_BE.get(), BlockEntitySnackCabinetRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DebugFloorModel.LAYER, DebugFloorModel::createBodyLayer);
    }
}
