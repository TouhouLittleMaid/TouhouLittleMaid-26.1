package com.github.tartaricacid.touhoulittlemaid.client.entity;

import com.github.tartaricacid.touhoulittlemaid.compat.immersivemelodies.client.ImmersiveMelodiesCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.EntityStateTracker;
import net.minecraft.world.entity.LivingEntity;

public class GeckoMaidStateTracker<T extends LivingEntity> extends EntityStateTracker<T> {
    private ImmersiveMelodiesCompat.ImmersiveMelodiesData imData = new ImmersiveMelodiesCompat.ImmersiveMelodiesData();

    public GeckoMaidStateTracker(T entity) {
        super(entity);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void updateRenderTickData(float currentRenderTick, float lastRenderTick, float partialTicks) {
        super.updateRenderTickData(currentRenderTick, lastRenderTick, partialTicks);
        ImmersiveMelodiesCompat.updateMelodyProgress(this.entity, imData, partialTicks);
    }

    public ImmersiveMelodiesCompat.ImmersiveMelodiesData getImmersiveMelodiesData() {
        return imData;
    }
}
