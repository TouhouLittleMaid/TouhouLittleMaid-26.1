package com.github.tartaricacid.touhoulittlemaid.compat.cloth;

import com.github.tartaricacid.touhoulittlemaid.util.migrate.ScreenUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;

public final class ClothConfigCompat {
    public static void openConfigScreen() {
        ConfigBuilder configBuilder = MenuIntegration.getConfigBuilder();
        configBuilder.setGlobalizedExpanded(true);
        ScreenUtil.setScreen(configBuilder.build());
    }
}
