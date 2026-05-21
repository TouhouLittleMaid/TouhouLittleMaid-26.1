package com.github.tartaricacid.touhoulittlemaid.compat.ysm;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.YsmMaidInfo;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

public class YsmCompat {
    private static final String MOD_ID = "yes_steve_model";
    private static final VersionRange VERSION_RANGE;
    private static boolean INSTALLED = false;

    static {
        try {
            VERSION_RANGE = VersionRange.createFromVersionSpec("[2.3.3,)");
        } catch (InvalidVersionSpecificationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        ModList.get().getModContainerById(MOD_ID).ifPresent(modContainer -> {
            ArtifactVersion version = modContainer.getModInfo().getVersion();
            if (VERSION_RANGE.containsVersion(version)) {
                INSTALLED = true;
            } else {
                // 开发环境下，version 是空的，所以需要额外判断
                INSTALLED = !FMLEnvironment.isProduction();
            }
        });
    }

    public static boolean isInstalled() {
        return INSTALLED;
    }

    public static YsmMaidInfo getYsmMaidInfo(CompoundTag maidData) {
        if (isInstalled()) {
            boolean isYsmModel = maidData.getBooleanOr(EntityMaid.IS_YSM_MODEL_TAG, false);
            String ysmModelId = maidData.getStringOr(EntityMaid.YSM_MODEL_ID_TAG, StringUtils.EMPTY);
            String ysmTextureId = maidData.getStringOr(EntityMaid.YSM_MODEL_TEXTURE_TAG, StringUtils.EMPTY);
            String ysmName = maidData.getStringOr(EntityMaid.YSM_MODEL_NAME_TAG, StringUtils.EMPTY);
            return new YsmMaidInfo(isYsmModel, ysmModelId, ysmTextureId, ysmName);
        }
        return YsmMaidInfo.EMPTY;
    }
}
