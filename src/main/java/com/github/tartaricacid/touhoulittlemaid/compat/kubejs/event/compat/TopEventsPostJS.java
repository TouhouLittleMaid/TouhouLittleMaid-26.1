package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.compat;

import com.github.tartaricacid.touhoulittlemaid.api.event.AddTopInfoEvent;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.MaidEventsJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common.AddTopInfoEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TopEventsPostJS {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void addTopInfo(AddTopInfoEvent event) {
        if (MaidEventsJS.ADD_TOP_INFO.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            MaidEventsJS.ADD_TOP_INFO.post(scriptType, new AddTopInfoEventJS(event));
        }
    }
}
