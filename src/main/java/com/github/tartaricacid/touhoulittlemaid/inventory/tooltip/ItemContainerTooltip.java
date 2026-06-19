package com.github.tartaricacid.touhoulittlemaid.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemContainerContents;

public record ItemContainerTooltip(ItemContainerContents contents) implements TooltipComponent {
}
