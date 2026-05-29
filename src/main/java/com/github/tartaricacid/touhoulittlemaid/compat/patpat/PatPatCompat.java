package com.github.tartaricacid.touhoulittlemaid.compat.patpat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;

public class PatPatCompat {
    private static final String PATPAT_ID = "patpat";
    private static boolean isLoaded = false;

    public static void init() {
        isLoaded = ModList.get().isLoaded(PATPAT_ID);
    }

    public static void renderPat(LivingEntity livingEntity, PoseStack matrixStack, float tickDelta) {
    }
}
