package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions.physics.IPhysics;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

public class PhysicsManager {
    private final Int2ReferenceOpenHashMap<IPhysics> physicsValues = new Int2ReferenceOpenHashMap<>(16);
    private float lastRenderTicks = 0;

    public void update(float renderTicks) {
        if (lastRenderTicks > 0) {
            if (renderTicks > lastRenderTicks) {
                float interval = (renderTicks - lastRenderTicks) / 20f;
                lastRenderTicks = renderTicks;
                physicsValues.int2ReferenceEntrySet().fastForEach(entry -> entry.getValue().update(interval));
            }
        } else {
            lastRenderTicks = renderTicks;
        }
    }

    public void put(int key, IPhysics physics) {
        this.physicsValues.put(key, physics);
    }

    @Nullable
    public IPhysics get(int key) {
        return this.physicsValues.get(key);
    }

    public void reset() {
        lastRenderTicks = 0;
        physicsValues.clear();
    }
}
