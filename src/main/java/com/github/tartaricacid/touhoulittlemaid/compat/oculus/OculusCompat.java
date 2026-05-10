package com.github.tartaricacid.touhoulittlemaid.compat.oculus;

public final class OculusCompat {
    public static final String OCULUS = "oculus";
    public static boolean IS_OCULUS_INSTALLED = false;

    public static void init() {
        IS_OCULUS_INSTALLED = net.neoforged.fml.ModList.get().getModContainerById(OCULUS).isPresent();
    }

    public static boolean isOculusInstalled() {
        return IS_OCULUS_INSTALLED;
    }
}
