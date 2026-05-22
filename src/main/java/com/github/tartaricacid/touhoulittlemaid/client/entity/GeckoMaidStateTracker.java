package com.github.tartaricacid.touhoulittlemaid.client.entity;

import com.github.tartaricacid.touhoulittlemaid.compat.immersivemelodies.client.ImmersiveMelodiesCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.EntityStateTracker;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GeckoMaidStateTracker<T extends LivingEntity> extends EntityStateTracker<T> {
    private ImmersiveMelodiesCompat.ImmersiveMelodiesData imData = new ImmersiveMelodiesCompat.ImmersiveMelodiesData();

    private ItemStack mainhandItemStack = ItemStack.EMPTY;
    private ItemStack offhandItemStack = ItemStack.EMPTY;

    public GeckoMaidStateTracker(T entity) {
        super(entity);
    }

    @Override
    public void reset() {
        mainhandItemStack = ItemStack.EMPTY;
        offhandItemStack = ItemStack.EMPTY;
        super.reset();
    }

    @Override
    protected void updateRenderTickData(float currentRenderTick, float lastRenderTick, float partialTicks) {
        super.updateRenderTickData(currentRenderTick, lastRenderTick, partialTicks);
        ImmersiveMelodiesCompat.updateMelodyProgress(this.entity, imData, partialTicks);
    }

    public ItemStack getHandItem(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return mainhandItemStack;
        } else {
            return offhandItemStack;
        }
    }

    public void setHandItem(ItemStack stack, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            this.mainhandItemStack = stack;
        } else {
            this.offhandItemStack = stack;
        }
    }

    public ImmersiveMelodiesCompat.ImmersiveMelodiesData getImmersiveMelodiesData() {
        return imData;
    }
}
