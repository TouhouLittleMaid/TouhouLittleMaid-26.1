package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.boat.Boat;

import java.util.function.BiPredicate;

public class AnimationRegister {
    private static final double MIN_SPEED = 0.05;

    public static void registerAnimationState() {
        register("death", LoopType.PLAY_ONCE, Priority.HIGHEST, (maid, event) -> maid.isDeadOrDying());
        register("sleep", Priority.HIGHEST, (maid, event) -> maid.getPose() == Pose.SLEEPING);
        register("swim", Priority.HIGHEST, (maid, event) -> maid.isVisuallySwimming());

        register("ladder_up", Priority.HIGHEST, (maid, event) -> maid.onClimbable() && getVerticalSpeed(maid) > 0);
        register("ladder_stillness", Priority.HIGHEST, (maid, event) -> maid.onClimbable() && getVerticalSpeed(maid) == 0);
        register("ladder_down", Priority.HIGHEST, (maid, event) -> maid.onClimbable() && getVerticalSpeed(maid) < 0);

        register("gomoku", Priority.HIGH, (maid, event) -> sitInJoy(maid, Type.GOMOKU));
        register("bookshelf", Priority.HIGH, (maid, event) -> sitInJoy(maid, Type.BOOKSHELF));
        register("computer", Priority.HIGH, (maid, event) -> sitInJoy(maid, Type.COMPUTER));
        register("keyboard", Priority.HIGH, (maid, event) -> sitInJoy(maid, Type.KEYBOARD));
        register("picnic", Priority.HIGH, (maid, event) -> sitInJoy(maid, Type.ON_HOME_MEAL));

        register("boat", Priority.HIGH, (maid, event) -> maid.getVehicle() instanceof Boat);
        register("chair", Priority.HIGH, (maid, event) -> maid.isPassenger());
        register("sit", Priority.HIGH, (maid, event) -> maid.isMaidInSittingPose());

        register("swim_stand", Priority.NORMAL, (maid, event) -> maid.isInWater());
        register("attacked", LoopType.PLAY_ONCE, Priority.NORMAL, (maid, event) -> maid.hurtTime > 0);
        register("jump", Priority.NORMAL, (maid, event) -> !maid.onGround() && !maid.isInWater());

        register("run", Priority.LOW, (maid, event) -> maid.onGround() && maid.isSprinting());
        register("walk", Priority.LOW, (maid, event) -> maid.onGround() && event.getLimbSwingAmount() > MIN_SPEED);

        register("idle", Priority.LOWEST, (maid, event) -> true);
    }

    private static boolean sitInJoy(EntityMaid maid, Type type) {
        return maid.getVehicle() instanceof EntitySit sit && sit.getJoyType().equals(type.getTypeName());
    }

    private static void register(String animationName, LoopType loopType, int priority, BiPredicate<EntityMaid, AnimationEvent<?>> predicate) {
        AnimationManager.register(new AnimationState(animationName, loopType, priority, predicate));
    }

    private static void register(String animationName, int priority, BiPredicate<EntityMaid, AnimationEvent<?>> predicate) {
        register(animationName, LoopType.LOOP, priority, predicate);
    }

    private static float getVerticalSpeed(EntityMaid maid) {
        Mob entity = maid;
        return 20 * (float) (entity.position().y - entity.yo);
    }
}
