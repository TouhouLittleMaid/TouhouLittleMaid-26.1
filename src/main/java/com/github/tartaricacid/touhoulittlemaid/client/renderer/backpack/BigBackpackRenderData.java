package com.github.tartaricacid.touhoulittlemaid.client.renderer.backpack;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.MaidBackpackRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.MOD_ID;
import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.BIG_BACKPACK;

public class BigBackpackRenderData extends MaidBackpackRenderData {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/bedrock/entity/backpack/big_backpack.png");

    @Override
    public @Nullable EntityModel<EntityMaidRenderState> getBackpackModel() {
        return InternalBedrockModelRegistry.getEntityModel(BIG_BACKPACK);
    }

    @Override
    public @Nullable Identifier getBackpackTexture() {
        return TEXTURE;
    }

    @Override
    public void offsetBackpackItem(PoseStack poseStack) {
        poseStack.translate(0, 0.5, -0.4375);
    }
}
