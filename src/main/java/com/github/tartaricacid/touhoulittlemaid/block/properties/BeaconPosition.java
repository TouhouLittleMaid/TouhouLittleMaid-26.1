package com.github.tartaricacid.touhoulittlemaid.block.properties;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum BeaconPosition implements StringRepresentable {
    UP_N_S, UP_W_E, DOWN;

    @NonNull
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.US);
    }

    @Override
    public String toString() {
        return getSerializedName();
    }
}
