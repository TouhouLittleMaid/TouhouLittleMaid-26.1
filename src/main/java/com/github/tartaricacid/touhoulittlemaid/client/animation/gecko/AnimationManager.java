package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition.*;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.IAnimationPredicate;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.GunClientUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidGameManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.PlayState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidAnimationPackage;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class AnimationManager {
    public static final IAnimationPredicate<?> EMPTY = e -> PlayState.STOP;
    @SuppressWarnings("unchecked")
    private static final ReferenceArrayList<AnimationState>[] data = new ReferenceArrayList[Priority.LOWEST + 1];

    static {
        for (int i = 0; i < data.length; i++) {
            data[i] = new ReferenceArrayList<>(6);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends AnimatableEntity<?>> IAnimationPredicate<T> empty() {
        return (IAnimationPredicate<T>) EMPTY;
    }

    @Nonnull
    public static PlayState playLoopAnimation(AnimationEvent<?> event, String animationName) {
        return playAnimation(event, animationName, LoopType.LOOP);
    }

    @Nonnull
    private static PlayState playAnimation(AnimationEvent<?> event, String animationName, LoopType loopType) {
        event.getCodedController().setAnimation(animationName, loopType);
        return PlayState.CONTINUE;
    }

    @Nonnull
    private static PlayState playAnimation(AnimationEvent<?> event, String animationName) {
        event.getCodedController().setAnimation(animationName);
        return PlayState.CONTINUE;
    }

    public static void register(AnimationState state) {
        data[state.getPriority()].add(state);
    }

    public static PlayState predicateParallel(AnimationEvent<?> event, String animationName) {
        if (Minecraft.getInstance().isPaused()) {
            return PlayState.STOP;
        }
        return playLoopAnimation(event, animationName);
    }

    @NotNull
    public static PlayState predicateMain(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        for (int i = Priority.HIGHEST; i <= Priority.LOWEST; i++) {
            // 载具动画单独检查
            if (i == Priority.HIGH) {
                PlayState vehicleAnimation = getVehicleAnimation(event);
                if (vehicleAnimation != null) {
                    return vehicleAnimation;
                }
            }
            for (AnimationState state : data[i]) {
                if (state.getPredicate().test(maid, event)) {
                    String animationName = state.getAnimationName();
                    LoopType loopType = state.getLoopType();
                    PlayState gunMainAnimation = GunClientUtil.playGunMainAnimation(maid, event, animationName, loopType);
                    return Objects.requireNonNullElseGet(gunMainAnimation, () -> playAnimation(event, animationName, loopType));
                }
            }
        }
        return PlayState.STOP;
    }

    public static PlayState predicateOffhandHold(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        Mob entity = maid;
        if (!entity.swinging && !entity.isUsingItem()) {
            ItemStack offhandItem = entity.getItemInHand(InteractionHand.OFF_HAND);
            if (offhandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(offhandItem)) {
                return playAnimation(event, "hold_offhand:charged_crossbow", LoopType.LOOP);
            }
        }
        if (checkSwingAndUse(maid, InteractionHand.OFF_HAND)) {
            ItemStack offhandItem = entity.getItemInHand(InteractionHand.OFF_HAND);
            if (!isSameItem(maid, offhandItem, InteractionHand.OFF_HAND)) {
                maid.getHandItemsForAnimation()[InteractionHand.OFF_HAND.ordinal()] = offhandItem;
                playAnimation(event, "empty", LoopType.LOOP);
            }

            ConditionalHold conditionalHold = event.getAnimatableEntity().getGeckoContainer().conditionManager().holdOffhand;
            String name = conditionalHold.doTest(maid);
            if (StringUtils.isNoneBlank(name)) {
                return playAnimation(event, name, LoopType.LOOP);
            }
        }
        return PlayState.STOP;
    }

    public static PlayState predicateMainhandHold(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        if (!maid.swinging && !maid.isUsingItem()) {
            ItemStack mainHandItem = maid.getItemInHand(InteractionHand.MAIN_HAND);
            PlayState gunHoldAnimation = GunClientUtil.playGunHoldAnimation(mainHandItem, event);
            if (gunHoldAnimation != null) {
                return gunHoldAnimation;
            }
            if (mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
                return playAnimation(event, "hold_mainhand:charged_crossbow", LoopType.LOOP);
            }
            if (maid.hasFishingHook()) {
                return playAnimation(event, "hold_mainhand:fishing", LoopType.LOOP);
            }
        }

        if (checkSwingAndUse(maid, InteractionHand.MAIN_HAND)) {
            ItemStack mainHandItem = maid.getItemInHand(InteractionHand.MAIN_HAND);
            if (!isSameItem(maid, mainHandItem, InteractionHand.MAIN_HAND)) {
                maid.getHandItemsForAnimation()[InteractionHand.MAIN_HAND.ordinal()] = mainHandItem;
                playAnimation(event, "empty", LoopType.LOOP);
            }

            ConditionalHold conditionalHold = event.getAnimatableEntity().getGeckoContainer().conditionManager().holdMainhand;
            String name = conditionalHold.doTest(maid);
            if (StringUtils.isNoneBlank(name)) {
                return playAnimation(event, name, LoopType.LOOP);
            }
        }
        return PlayState.STOP;
    }

    private static boolean isSameItem(EntityMaid maid, ItemStack maidItem, InteractionHand hand) {
        ItemStack preItem = maid.getHandItemsForAnimation()[hand.ordinal()];
        if (preItem.isDamaged()) {
            return ItemStack.isSameItem(maidItem, preItem);
        }
        return ItemStack.matches(maidItem, preItem);
    }

    public static PlayState predicateSwing(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        if (maid.swinging && !maid.isSleeping()) {
            if (maid.swingTime == 0 && event.getAnimatableEntity().getStateTracker().setEntityTickState(EntityTickStates.SWING)) {
                event.getCodedController().indicateReload();
            }
            var manager = event.getAnimatableEntity().getGeckoContainer().conditionManager();
            ConditionalSwing conditionalSwing = (maid.swingingArm == InteractionHand.MAIN_HAND) ? manager.swing : manager.swingOffhand;
            String name = conditionalSwing.doTest(maid);
            if (StringUtils.isNoneBlank(name)) {
                return playAnimation(event, name, LoopType.PLAY_ONCE);
            }
            String defaultSwing = (maid.swingingArm == InteractionHand.MAIN_HAND) ? "swing_hand" : "swing_offhand";
            return playAnimation(event, defaultSwing, LoopType.PLAY_ONCE);
        }
        return PlayState.CONTINUE;
    }

    public static PlayState predicateUse(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        if (maid.isUsingItem() && !maid.isSleeping()) {
            if (maid.getTicksUsingItem() == 1 && event.getAnimatableEntity().getStateTracker().setEntityTickState(EntityTickStates.USING_ITEM)) {
                event.getCodedController().indicateReload();
            }
            var manager = event.getAnimatableEntity().getGeckoContainer().conditionManager();
            if (maid.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                ConditionalUse conditionalUse = manager.useMainhand;
                String name = conditionalUse.doTest(maid);
                if (StringUtils.isNoneBlank(name)) {
                    return playAnimation(event, name, LoopType.LOOP);
                }
                return playAnimation(event, "use_mainhand", LoopType.LOOP);
            } else {
                ConditionalUse conditionalUse = manager.useOffhand;
                String name = conditionalUse.doTest(maid);
                if (StringUtils.isNoneBlank(name)) {
                    return playAnimation(event, name, LoopType.LOOP);
                }
                return playAnimation(event, "use_offhand", LoopType.LOOP);
            }
        }
        return PlayState.STOP;
    }

    public static PlayState predicateMisc(AnimationEvent<GeckoMaidEntity<?>> event) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        // 赢棋输棋优先
        if (maid.getVehicle() instanceof EntitySit) {
            MaidGameManager manager = maid.getGameManager();
            if (manager.isWin()) {
                return playAnimation(event, "game_win", LoopType.LOOP);
            }
            if (manager.isLost()) {
                return playAnimation(event, "game_lost", LoopType.LOOP);
            }
        }
        // 祈求动画
        if (maid.isBegging()) {
            return playAnimation(event, "beg", LoopType.LOOP);
        }
        // 其他杂项动画，目前仅捡雪球
        if (maid.getAnimationManager().animationId == MaidAnimationPackage.PICK_UP_SNOWBALL) {
            // 捡雪球动画默认 1750 毫秒
            if (System.currentTimeMillis() - maid.getAnimationManager().animationRecordTime > 1750) {
                maid.getAnimationManager().animationId = MaidAnimationPackage.NONE;
                maid.getAnimationManager().animationRecordTime = -1L;
                // 利用空动画重置 PLAY_ONCE 动画
                return playAnimation(event, "empty", LoopType.PLAY_ONCE);
            }
            return playAnimation(event, "pick_up_snowball", LoopType.PLAY_ONCE);
        }
        return PlayState.STOP;
    }

    public static <T extends EntityMaid> PlayState predicateArmor(AnimationEvent<GeckoMaidEntity<T>> event, EquipmentSlot slot) {
        EntityMaid maid = event.getAnimatableEntity().getMaid();
        ItemStack itemBySlot = maid.getItemBySlot(slot);
        if (itemBySlot.isEmpty()) {
            return PlayState.STOP;
        }

        ConditionArmor conditionArmor = event.getAnimatableEntity().getGeckoContainer().conditionManager().armor;
        String name = conditionArmor.doTest(maid, slot);
        if (StringUtils.isNoneBlank(name)) {
            return playAnimation(event, name, LoopType.LOOP);
        }

        String defaultName = slot.getName() + ":default";
        if (event.getAnimatableEntity().getAnimation(defaultName) != null) {
            return playAnimation(event, defaultName, LoopType.LOOP);
        }
        return PlayState.STOP;
    }

    @Nullable
    public static PlayState getVehicleAnimation(AnimationEvent<GeckoMaidEntity<?>> event) {
        Mob mob = event.getAnimatableEntity().getMaid();
        Entity vehicle = mob.getVehicle();
        if (vehicle == null || !vehicle.isAlive()) {
            return null;
        }

        var manager = event.getAnimatableEntity().getGeckoContainer().conditionManager();

        // 如果是坐垫
        if (vehicle instanceof EntityChair) {
            ConditionalChair conditionalChair = manager.chair;
            String name = conditionalChair.doTest(mob);
            if (StringUtils.isNoneBlank(name)) {
                return playAnimation(event, name, LoopType.LOOP);
            }
        }

        // 其他情况
        ConditionalVehicle vehicleCondition = manager.vehicle;
        String name = vehicleCondition.doTest(mob);
        if (StringUtils.isNoneBlank(name)) {
            return playAnimation(event, name, LoopType.LOOP);
        }
        return null;
    }

    public static PlayState predicatePassengerAnimation(AnimationEvent<GeckoMaidEntity<?>> event) {
        Mob mob = event.getAnimatableEntity().getMaid();
        Entity passenger = mob.getFirstPassenger();
        if (passenger == null || !passenger.isAlive()) {
            return PlayState.STOP;
        }

        ConditionalPassenger conditionalPassenger = event.getAnimatableEntity().getGeckoContainer().conditionManager().passenger;
        String name = conditionalPassenger.doTest(mob);
        if (StringUtils.isNoneBlank(name)) {
            return playAnimation(event, name, LoopType.LOOP);
        }
        return PlayState.STOP;
    }

    private static boolean checkSwingAndUse(EntityMaid maid, InteractionHand hand) {
        if (maid.swinging && maid.swingingArm == hand) {
            return false;
        }
        return !maid.isUsingItem() || maid.getUsedItemHand() != hand;
    }
}
