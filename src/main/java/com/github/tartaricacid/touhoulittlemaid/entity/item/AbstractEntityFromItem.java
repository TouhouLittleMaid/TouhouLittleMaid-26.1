package com.github.tartaricacid.touhoulittlemaid.entity.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public abstract class AbstractEntityFromItem extends LivingEntity {
    public AbstractEntityFromItem(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    /**
     * 判定此玩家能否击落实体
     *
     * @param player 使用击打的玩家
     * @return 能够击落
     */
    protected abstract boolean canKillEntity(Player player);

    /**
     * 击打时的音效
     *
     * @return 音效
     */
    protected abstract SoundEvent getHitSound();

    /**
     * 该实体对应的物品
     *
     * @return 物品
     */
    protected abstract Item getWithItem();

    /**
     * 该实体死亡时掉落的对应物品堆
     *
     * @return 物品堆
     */
    protected abstract ItemStack getKilledStack();

    /**
     * 如果有额外掉落的物品
     */
    protected void dropExtraItems() {
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (!this.level.isClientSide() && this.isAlive()) {
            // 如果实体是无敌的
            if (this.isInvulnerableTo(level, source)) {
                return false;
            }
            // 应用打掉的逻辑
            if (source.getDirectEntity() instanceof Player) {
                return applyHitEntityLogic((Player) source.getDirectEntity());
            }
        }
        return false;
    }

    private boolean applyHitEntityLogic(Player player) {
        if (player.isShiftKeyDown()) {
            this.ejectPassengers();
            this.playSound(getHitSound(), 1.0f, 1.0f);
            if (player.isCreative() || canKillEntity(player)) {
                this.killEntity();
            }
        }
        return true;
    }

    private void killEntity() {
        if (this.level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
            ItemStack itemstack = getKilledStack();
            if (this.hasCustomName()) {
                itemstack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
            }
            this.spawnAtLocation(serverLevel, itemstack, 0.0F);
            this.dropExtraItems();
        }
        this.discard();
    }

    /**
     * 不允许被挤走，所以此处留空
     */
    @Override
    public void push(Entity entityIn) {
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void kill(ServerLevel level) {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof Player && !this.level.mayInteract(entity, this.blockPosition());
    }

    @Override
    public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        // 不允许被击退效果影响
    }

    @Override
    @Nullable
    public ItemStack getPickResult() {
        return getKilledStack();
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }
}
