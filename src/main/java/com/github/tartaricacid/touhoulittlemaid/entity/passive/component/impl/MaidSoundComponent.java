package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPlaySoundEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook.SoundHook;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.BaseTickComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.PlayMaidSoundPackage;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;

@MaidComponentDef("sound")
public class MaidSoundComponent implements MaidComponent, BaseTickComponent, SoundHook {
    private final EntityMaid maid;
    private int playerHurtSoundCount = 120;
    private int pickupSoundCount = 5;

    public MaidSoundComponent(EntityMaid maid) {
        this.maid = maid;
    }

    @Override
    public int priority() {
        return 20;
    }

    public void tryPlayMaidPickupSound() {
        if (mayPlaySound()) {
            pickupSoundCount--;
            if (pickupSoundCount == 0) {
                this.playSound(InitSounds.MAID_ITEM_GET.get(), 1, 1);
                pickupSoundCount = 5;
            }
        }
    }

    public boolean mayPlaySound() {
        return !NeoForge.EVENT_BUS.post(new MaidPlaySoundEvent(maid)).isCanceled();
    }

    @Override
    public void baseTick() {
        if (playerHurtSoundCount > 0) {
            playerHurtSoundCount--;
        }
    }

    @Override
    public boolean playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (soundEvent.location().getPath().startsWith("maid") && !maid.level.isClientSide()) {
            NetworkHandler.sendToNearby(maid, new PlayMaidSoundPackage(soundEvent.location(), maid.components.profile.getSoundPackId(), maid.getId()), 16);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        if (!mayPlaySound()) {
            return null;
        }
        return maid.components.task.getTask().getAmbientSound(maid);
    }

    @Override
    @Nullable
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (!mayPlaySound()) {
            return null;
        }
        if (damageSourceIn.is(DamageTypeTags.IS_FIRE)) {
            return InitSounds.MAID_HURT_FIRE.get();
        } else if (damageSourceIn.getEntity() instanceof Player) {
            if (playerHurtSoundCount == 0) {
                playerHurtSoundCount = 120;
                return InitSounds.MAID_PLAYER.get();
            } else {
                return null;
            }
        } else {
            return InitSounds.MAID_HURT.get();
        }
    }

    @Override
    @Nullable
    public SoundEvent getDeathSound() {
        if (!mayPlaySound()) {
            return null;
        }
        return InitSounds.MAID_DEATH.get();
    }
}
