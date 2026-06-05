package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity.state.JoyRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityBookshelf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class BlockEntityBookshelfRenderer extends BlockEntityJoyRenderer<BlockEntityBookshelf> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/bookshelf.png");
    private final SimpleBedrockModel<Unit> model;

    public BlockEntityBookshelfRenderer(BlockEntityRendererProvider.Context context) {
        model = InternalBedrockModelRegistry.getModel(InternalBedrockModelRegistry.BOOKSHELF);
    }

    @Override
    public void submit(JoyRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facing.get2DDataValue() * 90));
        submitNodeCollector.submitCustomGeometry(
                poseStack, RenderTypes.entityCutout(TEXTURE),
                (pose, buffer) -> {
                    poseStack.pushPose();
                    poseStack.last().set(pose);
                    model.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
        );
        poseStack.popPose();
    }
}
