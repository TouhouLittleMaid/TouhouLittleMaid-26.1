package com.github.tartaricacid.touhoulittlemaid.geckolib3.core;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntityStateTracker<T extends Entity> {
    protected T entity;

    private final IntOpenHashSet entityTickStates;
    private int lastEntityTickCount;

    private Vec3 lastPosition;
    private Vec3 positionDelta = Vec3.ZERO;
    private String mainAnimationCache;
    protected float lastRenderTick;
    protected float renderTickDelta;

    public EntityStateTracker(T entity) {
        this.entity = entity;
        this.entityTickStates = new IntOpenHashSet();
    }

    public void reset() {
        entityTickStates.clear();
        lastEntityTickCount = 0;
        lastPosition = null;
        positionDelta = Vec3.ZERO;
        mainAnimationCache = null;
        lastRenderTick = 0;
        renderTickDelta = 0;
    }

    final void update(int entityTickCount, float renderTick, float partialTicks) {
        if (lastEntityTickCount < entityTickCount) {
            updateEntityTickData(entityTickCount, lastEntityTickCount);
            lastEntityTickCount = entityTickCount;
        }

        if (lastRenderTick < renderTick) {
            updateRenderTickData(renderTick, lastRenderTick, partialTicks);
            lastRenderTick = renderTick;
        }
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    /**
     * 每帧的第一次更新时调用，后续更新则不会（如纸娃娃渲染）；
     * 第一次更新可能是在 iris 渲染阴影时，不过问题不大；
     * 不要用 entity.tickCount，否则会受暂停影响。
     */
    protected void updateRenderTickData(float currentRenderTick, float lastRenderTick, float partialTicks) {
        renderTickDelta = currentRenderTick - lastRenderTick;
        updatePositionDelta(partialTicks);
        mainAnimationCache = null;
    }

    protected void updateEntityTickData(int currentTickCount, int lastTickCount) {
        entityTickStates.clear();
    }

    private void updatePositionDelta(float partialTicks) {
        var cur = new Vec3(Mth.lerp(partialTicks, entity.xo, entity.getX()),
                Mth.lerp(partialTicks, entity.yo, entity.getY()),
                Mth.lerp(partialTicks, entity.zo, entity.getZ()));
        if (lastPosition != null) {
            positionDelta = cur.subtract(lastPosition);
        }
        lastPosition = cur;
    }

    /**
     * 在每个 entityTick 开始时都会清空的状态，
     * 在同一个 entityTick 内的所有 renderTick 可共享这些状态
     */
    public boolean setEntityTickState(int name) {
        return entityTickStates.add(name);
    }

    public boolean hasEntityTickState(int name) {
        return entityTickStates.contains(name);
    }

    /**
     * 获取两帧之间实体的坐标变化
     */
    public Vec3 getPositionDelta() {
        return positionDelta;
    }

    @Nullable
    public String getMainAnimationCache() {
        return mainAnimationCache;
    }

    public void setMainAnimationCache(String mainAnimation) {
        this.mainAnimationCache = mainAnimation;
    }

    public float getRenderTickDelta() {
        return renderTickDelta;
    }
}
