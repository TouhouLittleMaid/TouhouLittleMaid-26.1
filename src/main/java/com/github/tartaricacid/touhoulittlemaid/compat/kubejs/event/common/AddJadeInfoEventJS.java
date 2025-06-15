package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.AddJadeInfoEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.latvian.mods.kubejs.event.KubeEvent;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class AddJadeInfoEventJS implements KubeEvent {
    private final EntityMaid maid;
    private final ITooltip tooltip;
    private final IPluginConfig pluginConfig;

    public AddJadeInfoEventJS(AddJadeInfoEvent event) {
        this.maid = event.getMaid();
        this.tooltip = event.getTooltip();
        this.pluginConfig = event.getPluginConfig();
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public ITooltip getTooltip() {
        return tooltip;
    }

    public IPluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
