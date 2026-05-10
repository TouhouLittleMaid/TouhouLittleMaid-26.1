package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTaskEnableEvent;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.event.KubeEvent;

import java.util.List;
import java.util.function.Predicate;

public class MaidTaskEnableEventJS implements KubeEvent {
    private final IMaidTask targetTask;
    private final EntityMaid entityMaid;
    private final List<Pair<String, Predicate<EntityMaid>>> enableConditionDesc;

    public MaidTaskEnableEventJS(MaidTaskEnableEvent event) {
        this.targetTask = event.getTargetTask();
        this.entityMaid = event.getEntityMaid();
        this.enableConditionDesc = event.getEnableConditionDesc();
    }

    public IMaidTask getTargetTask() {
        return targetTask;
    }

    public EntityMaid getEntityMaid() {
        return entityMaid;
    }

    public List<Pair<String, Predicate<EntityMaid>>> getEnableConditionDesc() {
        return enableConditionDesc;
    }

    public void addEnableConditionDesc(String desc, Predicate<EntityMaid> predicate) {
        enableConditionDesc.add(Pair.of(desc, predicate));
    }
}
