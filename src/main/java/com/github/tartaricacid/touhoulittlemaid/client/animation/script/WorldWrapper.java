package com.github.tartaricacid.touhoulittlemaid.client.animation.script;

import com.github.tartaricacid.touhoulittlemaid.api.animation.IWorldData;
import net.minecraft.world.level.Level;

public final class WorldWrapper implements IWorldData {
    private Level world;

    public void setData(Level world) {
        this.world = world;
    }

    public void clearData() {
        this.world = null;
    }

    @Override
    public long getWorldTime() {
        return world.getGameTime() % 24000;
    }

    @Override
    public boolean isDay() {
        return getWorldTime() < 13000;
    }

    @Override
    public boolean isNight() {
        return getWorldTime() >= 13000;
    }

    @Override
    public boolean isRaining() {
        return world.isRaining();
    }

    @Override
    public boolean isThundering() {
        return world.isThundering();
    }
}
