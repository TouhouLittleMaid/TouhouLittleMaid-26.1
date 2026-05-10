package com.github.tartaricacid.touhoulittlemaid.inventory.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

public record YsmMaidInfo(boolean isYsmModel, String modelId, String textureId, String name) {
    public static YsmMaidInfo EMPTY = new YsmMaidInfo(false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
}
