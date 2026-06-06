package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityJoy;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.JoyRenderState;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class JoyRenderer<T extends BlockEntityJoy> implements BlockEntityRenderer<T, JoyRenderState> {
    @Override
    public JoyRenderState createRenderState() {
        return new JoyRenderState();
    }

    @Override
    public void extractRenderState(T entity, JoyRenderState state, float partialTick, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.facing = entity.getBlockState().getValue(BlockGomoku.FACING);
    }

    @Override
    public AABB getRenderBoundingBox(T te) {
        return RenderHelper.getAABB(
                te.getBlockPos().offset(-2, 0, -2),
                te.getBlockPos().offset(2, 1, 2)
        );
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
