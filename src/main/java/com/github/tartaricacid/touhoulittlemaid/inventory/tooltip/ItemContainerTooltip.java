package com.github.tartaricacid.touhoulittlemaid.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public record ItemContainerTooltip(ResourceHandler<@NotNull ItemResource> handler) implements TooltipComponent {
}
