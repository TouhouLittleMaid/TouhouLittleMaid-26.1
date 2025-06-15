package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register;

import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.builder.MaidBaubleBuilder;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.builder.MaidTaskBuilder;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.builder.MaidTipsBuilder;
import dev.latvian.mods.kubejs.typings.Info;

public class MaidRegisterJS {
    @Info("""
            Register text prompts, which will be displayed when the player points at a maid with the corresponding item in hand. <br>
            注册提示文本，当玩家手持对应物品指向女仆时会显示对应的提示文本
            """)
    public static final MaidTipsBuilder TIPS = new MaidTipsBuilder();

    @Info("""
            Register baubles, which can be equipped by maids. <br>
            注册女仆饰品，女仆可以装备的饰品
            """)
    public static final MaidBaubleBuilder BAUBLE = new MaidBaubleBuilder();

    @Info("""
            Register maid's work tasks <br>
            注册女仆工作任务
            """)
    public static final MaidTaskBuilder TASK = new MaidTaskBuilder();
}