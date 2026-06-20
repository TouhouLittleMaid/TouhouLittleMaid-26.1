package com.github.tartaricacid.touhoulittlemaid.api.backpack;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public abstract class MaidBackpackRenderData {
    /**
     * 背包模型
     */
    @Nullable
    public abstract EntityModel<EntityMaidRenderState> getBackpackModel();

    /**
     * 背包材质
     */
    @Nullable
    public abstract Identifier getBackpackTexture();

    /**
     * 当女仆的装饰槽位装备物品时，应该对这个物品应用的矩阵，避免物品和背包穿模
     */
    public abstract void offsetBackpackItem(PoseStack poseStack);

    /**
     * 空实现，没有穿戴任何背包时返回此值
     */
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
}
