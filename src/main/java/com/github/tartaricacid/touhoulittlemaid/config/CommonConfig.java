package com.github.tartaricacid.touhoulittlemaid.config;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.*;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfig {
    public static ModConfigSpec CONFIG;

    public static ModConfigSpec init() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MaidConfig.init(builder);
        ChairConfig.init(builder);
        MiscConfig.init(builder);
        RenderConfig.init(builder);
        AIConfig.init(builder);
        CONFIG = builder.build();
        return CONFIG;
    }
}
