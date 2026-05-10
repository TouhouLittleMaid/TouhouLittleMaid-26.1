package com.github.tartaricacid.touhoulittlemaid.api.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import net.neoforged.bus.api.Event;

/**
 * 方便其他附属模将自己的事件注册到 KubeJS 的 MaidEvents 名下
 */
public class RegisterKubeJSEvent extends Event {
    private final EventGroup group;

    public RegisterKubeJSEvent(EventGroup group) {
        this.group = group;
    }

    public EventGroup getGroup() {
        return group;
    }
}
