package com.github.tartaricacid.touhoulittlemaid.client.init;

import com.github.tartaricacid.touhoulittlemaid.client.model.DebugFloorModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.NewEntityFairyModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.*;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.*;
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
import net.minecraft.world.entity.EntityType;
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

        EntityRenderers.register(EntityType.SLIME, EntityYukkuriSlimeRender::new);
        EntityRenderers.register(EntityType.MAGMA_CUBE, EntityMarisaYukkuriSlimeRender::new);
        EntityRenderers.register(EntityType.EXPERIENCE_ORB, ReplaceExperienceOrbRenderer::new);

        BlockEntityRenderers.register(InitBlocks.ALTAR_TE.get(), TileEntityAltarRenderer::new);
        BlockEntityRenderers.register(InitBlocks.STATUE_TE.get(), TileEntityStatueRenderer::new);
        BlockEntityRenderers.register(InitBlocks.GARAGE_KIT_TE.get(), TileEntityGarageKitRenderer::new);
        BlockEntityRenderers.register(InitBlocks.GOMOKU_TE.get(), TileEntityGomokuRenderer::new);
        BlockEntityRenderers.register(InitBlocks.CCHESS_TE.get(), TileEntityCChessRenderer::new);
        BlockEntityRenderers.register(InitBlocks.WCHESS_TE.get(), TileEntityWChessRenderer::new);
        BlockEntityRenderers.register(InitBlocks.KEYBOARD_TE.get(), TileEntityKeyboardRenderer::new);
        BlockEntityRenderers.register(InitBlocks.BOOKSHELF_TE.get(), TileEntityBookshelfRenderer::new);
        BlockEntityRenderers.register(InitBlocks.COMPUTER_TE.get(), TileEntityComputerRenderer::new);
        BlockEntityRenderers.register(InitBlocks.SHRINE_TE.get(), TileEntityShrineRenderer::new);
        BlockEntityRenderers.register(InitBlocks.PICNIC_MAT_TE.get(), PicnicMatRender::new);
        BlockEntityRenderers.register(InitBlocks.MAID_BED_TE.get(), TileEntityMaidBedRenderer::new);
        BlockEntityRenderers.register(InitBlocks.SNACK_CABINET_TE.get(), TileEntitySnackCabinetRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DebugFloorModel.LAYER, DebugFloorModel::createBodyLayer);
        // 为了别的模组兼容，暂时保留
        event.registerLayerDefinition(NewEntityFairyModel.LAYER, NewEntityFairyModel::createBodyLayer);
    }
}
