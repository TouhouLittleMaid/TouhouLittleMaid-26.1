package com.github.tartaricacid.touhoulittlemaid.inventory.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public record ItemMaidTooltip(String modelId, @Nullable Component customName) implements TooltipComponent {
}
