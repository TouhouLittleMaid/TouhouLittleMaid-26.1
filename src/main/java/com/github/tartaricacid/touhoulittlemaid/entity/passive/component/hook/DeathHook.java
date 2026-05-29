package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public interface DeathHook extends MaidComponent {
    void die(DamageSource cause, Consumer<DamageSource> superDie);

    void dropEquipment(ServerLevel level);

    void remove(Entity.RemovalReason reason);
}
