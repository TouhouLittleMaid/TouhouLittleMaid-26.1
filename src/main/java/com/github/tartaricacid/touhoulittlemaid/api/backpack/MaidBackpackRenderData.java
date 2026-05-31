package com.github.tartaricacid.touhoulittlemaid.api.backpack;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public abstract class MaidBackpackRenderData {
    public static final MaidBackpackRenderData EMPTY = new MaidBackpackRenderData() {
        @Override
        public @Nullable EntityModel<EntityMaidRenderState> getBackpackModel() {
            return null;
        }

        @Override
        public @Nullable Identifier getBackpackTexture() {
            return null;
        }

        @Override
        public void offsetBackpackItem(PoseStack poseStack) {
            poseStack.translate(0, 0.625, 0.2);
        }
    };

    @Nullable
    public abstract EntityModel<EntityMaidRenderState> getBackpackModel();

    @Nullable
    public abstract Identifier getBackpackTexture();

    public abstract void offsetBackpackItem(PoseStack poseStack);
}
