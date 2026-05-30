package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public final class MaidKillRecordManager {
    private static final String KILL_RECORD = "KillRecord";
    private static final String TOTAL_COUNT = "TotalCount";
    private static final String SLIME_COUNT = "Slime";
    private static final String WITHER_COUNT = "Wither";
    private static final String ENDER_DRAGON_COUNT = "EnderDragon";

    private final EntityMaid maid;

    private int totalCount;
    private int slimeCount;
    private int witherCount;
    private int enderDragonCount;

    public MaidKillRecordManager(EntityMaid maid) {
        this.maid = maid;
    }

    void save(ValueOutput output) {
        ValueOutput child = output.child(KILL_RECORD);
        child.store(TOTAL_COUNT, Codec.INT, totalCount);
        child.store(SLIME_COUNT, Codec.INT, slimeCount);
        child.store(WITHER_COUNT, Codec.INT, witherCount);
        child.store(ENDER_DRAGON_COUNT, Codec.INT, enderDragonCount);
    }

    void read(ValueInput input) {
        ValueInput child = input.childOrEmpty(KILL_RECORD);
        child.read(TOTAL_COUNT, Codec.INT).ifPresent(v -> totalCount = v);
        child.read(SLIME_COUNT, Codec.INT).ifPresent(v -> slimeCount = v);
        child.read(WITHER_COUNT, Codec.INT).ifPresent(v -> witherCount = v);
        child.read(ENDER_DRAGON_COUNT, Codec.INT).ifPresent(v -> enderDragonCount = v);
    }

    public void onTargetDeath(EntityMaid maid, LivingEntity target) {
        LivingEntity owner = maid.getOwner();
        this.totalCount++;
        triggerKill(owner, TriggerType.MAID_KILL_MOB);
        if (this.totalCount >= 100) {
            triggerKill(owner, TriggerType.KILL_100);
        }
        if (target instanceof Slime) {
            this.slimeCount++;
            if (slimeCount >= 300) {
                triggerKill(owner, TriggerType.KILL_SLIME_300);
            }
        }
        if (target instanceof WitherBoss) {
            this.witherCount++;
            triggerKill(owner, TriggerType.KILL_WITHER);
        }
        if (target instanceof EnderDragon) {
            this.enderDragonCount++;
            triggerKill(owner, TriggerType.KILL_DRAGON);
        }
    }

    private void triggerKill(@Nullable LivingEntity owner, String eventName) {
        if (owner instanceof ServerPlayer serverPlayer) {
            InitTrigger.MAID_EVENT.get().trigger(serverPlayer, eventName);
        }
    }
}
