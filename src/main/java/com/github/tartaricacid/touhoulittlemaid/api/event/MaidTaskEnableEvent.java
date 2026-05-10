package com.github.tartaricacid.touhoulittlemaid.api.event;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.List;
import java.util.function.Predicate;

/**
 * 用来修改默认女仆 Task 的启用条件，该事件优先级高于 IMaidTask#isEnable 方法
 * <p>
 * 这个事件会在客户端和服务端触发，客户端用于 GUI 显示
 * <p>
 * 当事件 cancel 后，则表示该 Task 不可用
 */
public class MaidTaskEnableEvent extends Event implements ICancellableEvent {
    private final IMaidTask targetTask;
    private final EntityMaid entityMaid;
    private final List<Pair<String, Predicate<EntityMaid>>> enableConditionDesc;

    public MaidTaskEnableEvent(IMaidTask targetTask, EntityMaid entityMaid, List<Pair<String, Predicate<EntityMaid>>> enableConditionDesc) {
        this.targetTask = targetTask;
        this.entityMaid = entityMaid;
        this.enableConditionDesc = enableConditionDesc;
    }

    public MaidTaskEnableEvent(IMaidTask targetTask, EntityMaid entityMaid) {
        this.targetTask = targetTask;
        this.entityMaid = entityMaid;
        this.enableConditionDesc = Lists.newArrayList();
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
