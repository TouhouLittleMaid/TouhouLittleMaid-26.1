package com.github.tartaricacid.touhoulittlemaid.compat.oculus;

import net.neoforged.fml.ModList;

public final class OculusCompat {
    public static final String OCULUS = "oculus";
    public static boolean IS_OCULUS_INSTALLED = false;

    public static void init() {
        IS_OCULUS_INSTALLED = ModList.get().getModContainerById(OCULUS).isPresent();
    }

    public static boolean isOculusInstalled() {
        return IS_OCULUS_INSTALLED;
    }
}
