package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.item.ItemMaidBeacon;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEntityMaidBeacon extends BlockEntity {
    public static final String POTION_INDEX_TAG = "PotionIndex";
    public static final String STORAGE_POWER_TAG = "StoragePower";
    public static final String OVERFLOW_DELETE_TAG = "OverflowDelete";
    private int potionIndex = -1;
    private float storagePower;
    private boolean overflowDelete = false;

    public BlockEntityMaidBeacon(BlockPos blockPos, BlockState blockState) {
        super(InitBlocks.MAID_BEACON_BE.get(), blockPos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockEntityMaidBeacon beacon) {
        if (beacon.level != null && !level.isClientSide() && level.getGameTime() % 80L == 0L) {
            if (beacon.potionIndex != -1 && beacon.storagePower >= beacon.getEffectCost()) {
                beacon.storagePower = beacon.storagePower - beacon.getEffectCost();
                beacon.updateBeaconEffect(level, BeaconEffect.getEffectByIndex(beacon.potionIndex).getEffect());
            }
            beacon.updateAbsorbPower(level);
        }
    }

    private void updateBeaconEffect(Level world, Holder<MobEffect> potion) {
        AABB inflate = new AABB(getBlockPos()).inflate(8, 8, 8);
        List<EntityMaid> list = world.getEntitiesOfClass(EntityMaid.class, inflate, LivingEntity::isAlive);
        for (EntityMaid maid : list) {
            maid.addEffect(new MobEffectInstance(potion, 100, 1, true, true));
        }
    }

    private void updateAbsorbPower(Level world) {
        int range = MiscConfig.SHRINE_LAMP_MAX_RANGE.get();
        AABB inflate = new AABB(getBlockPos()).inflate(range, range, range);
        List<EntityPowerPoint> list = world.getEntitiesOfClass(EntityPowerPoint.class, inflate, Entity::isAlive);
        for (EntityPowerPoint powerPoint : list) {
            float addNum = this.getStoragePower() + powerPoint.value / 100.0f;
            if (addNum <= this.getMaxStorage()) {
                this.setStoragePower(addNum);
                powerPoint.spawnExplosionParticle();
                powerPoint.discard();
            } else {
                if (overflowDelete) {
                    powerPoint.spawnExplosionParticle();
                    powerPoint.discard();
                }
            }
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.putInt(POTION_INDEX_TAG, potionIndex);
        output.store(STORAGE_POWER_TAG, Codec.FLOAT, storagePower);
        output.putBoolean(OVERFLOW_DELETE_TAG, overflowDelete);
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        potionIndex = input.getIntOr(POTION_INDEX_TAG, -1);
        storagePower = input.read(STORAGE_POWER_TAG, Codec.FLOAT).orElse(0.0f);
        overflowDelete = input.getBooleanOr(OVERFLOW_DELETE_TAG, false);
    }

    public void loadData(CompoundTag data) {
        potionIndex = data.getInt(POTION_INDEX_TAG).orElse(-1);
        storagePower = data.getFloat(STORAGE_POWER_TAG).orElse(0.0f);
        overflowDelete = data.getBoolean(OVERFLOW_DELETE_TAG).orElse(false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level instanceof ServerLevel serverLevel) {
            ItemStack itemStack = ItemMaidBeacon.blockEntityToItemStack(serverLevel.registryAccess(), this);
            Block.popResource(serverLevel, pos, itemStack);
        }
    }

    public int getPotionIndex() {
        return potionIndex;
    }

    public void setPotionIndex(int potionIndex) {
        this.potionIndex = potionIndex;
        this.refresh();
    }

    public float getStoragePower() {
        return storagePower;
    }

    public void setStoragePower(float storagePower) {
        this.storagePower = storagePower;
        this.refresh();
    }

    public boolean isOverflowDelete() {
        return overflowDelete;
    }

    public void setOverflowDelete(boolean overflowDelete) {
        this.overflowDelete = overflowDelete;
        this.refresh();
    }

    public float getEffectCost() {
        return (float) (MiscConfig.SHRINE_LAMP_EFFECT_COST.get() / 900);
    }

    public float getMaxStorage() {
        return MiscConfig.SHRINE_LAMP_MAX_STORAGE.get().floatValue();
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public enum BeaconEffect {
        // Effects
        SPEED(MobEffects.SPEED),
        FIRE_RESISTANCE(MobEffects.FIRE_RESISTANCE),
        STRENGTH(MobEffects.STRENGTH),
        RESISTANCE(MobEffects.RESISTANCE),
        REGENERATION(MobEffects.REGENERATION);

        private final Holder<MobEffect> effect;

        BeaconEffect(Holder<MobEffect> effect) {
            this.effect = effect;
        }

        public static BeaconEffect getEffectByIndex(int index) {
            return values()[Mth.clamp(0, index, values().length - 1)];
        }

        public Holder<MobEffect> getEffect() {
            return effect;
        }
    }
}
