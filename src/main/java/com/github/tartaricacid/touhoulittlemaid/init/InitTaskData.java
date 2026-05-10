package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.data.inner.AttackListData;
import net.minecraft.resources.Identifier;

public final class InitTaskData {
    public static TaskDataKey<AttackListData> ATTACK_LIST;

    public static void registerAll(TaskDataRegister register) {
        ATTACK_LIST = register.register(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "maid_attack_list"), AttackListData.CODEC);
    }
}