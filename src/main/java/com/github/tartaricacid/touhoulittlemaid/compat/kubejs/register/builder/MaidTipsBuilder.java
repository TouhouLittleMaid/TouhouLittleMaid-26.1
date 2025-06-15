package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.builder;

import com.github.tartaricacid.touhoulittlemaid.client.overlay.MaidTipsOverlay;
import com.google.common.collect.Lists;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.List;
import java.util.function.Consumer;

public class MaidTipsBuilder {
    private final List<Consumer<MaidTipsOverlay>> tips = Lists.newArrayList();

    @Info(value = """
            Register text prompts, which will be displayed when the player points at a maid with the corresponding item in hand. <br>
            注册提示文本，当玩家手持对应物品指向女仆时会显示对应的提示文本
            """)
    public MaidTipsBuilder tips(Consumer<MaidTipsOverlay> consumer) {
        tips.add(consumer);
        return this;
    }

    @HideFromJS
    public void register(MaidTipsOverlay overlay) {
        this.tips.forEach(consumer -> consumer.accept(overlay));
        this.tips.clear();
    }
}
