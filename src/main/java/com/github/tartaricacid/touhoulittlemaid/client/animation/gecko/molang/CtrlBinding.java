package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.Priority;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions.*;
import com.github.tartaricacid.touhoulittlemaid.compat.immersivemelodies.client.ImmersiveMelodiesCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.ContextBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.util.LazyValue;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class CtrlBinding extends ContextBinding {
    public static final LazyValue<CtrlBinding> INSTANCE = new LazyValue<>(CtrlBinding::new);

    public static final int STATE_CONTINUE = 2;
    public static final int STATE_STOP = 3;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_BYPASS = 5;

    public static final int LOOP = 10;
    public static final int PLAY_ONCE = 11;
    public static final int HOLD_ON_LAST_FRAME = 12;

    private static ReferenceArrayList<Condition>[] DATA;
    private static final float MIN_SPEED = 0.05f;

    private CtrlBinding() {
        // 主动画的
        register("death", Priority.HIGHEST, LivingEntity::isDeadOrDying);
        register("riptide", Priority.HIGHEST, LivingEntity::isAutoSpinAttack);
        register("sleep", Priority.HIGHEST, entity -> entity.getPose() == Pose.SLEEPING);
        register("swim", Priority.HIGHEST, Entity::isSwimming);
        register("climb", Priority.HIGHEST, entity -> entity.getPose() == Pose.SWIMMING && isMoving(entity));
        register("climbing", Priority.HIGHEST, entity -> entity.getPose() == Pose.SWIMMING);

        register("ladder_up", Priority.HIGHEST, entity -> entity.onClimbable() && getVerticalSpeed(entity) > 0);
        register("ladder_stillness", Priority.HIGHEST, entity -> entity.onClimbable() && getVerticalSpeed(entity) == 0);
        register("ladder_down", Priority.HIGHEST, entity -> entity.onClimbable() && getVerticalSpeed(entity) < 0);

        register("gomoku", Priority.HIGH, entity -> sitInJoy(entity, Type.GOMOKU));
        register("bookshelf", Priority.HIGH, entity -> sitInJoy(entity, Type.BOOKSHELF));
        register("computer", Priority.HIGH, entity -> sitInJoy(entity, Type.COMPUTER));
        register("keyboard", Priority.HIGH, entity -> sitInJoy(entity, Type.KEYBOARD));
        register("picnic", Priority.HIGH, entity -> sitInJoy(entity, Type.ON_HOME_MEAL));

        register("fly", Priority.HIGH, CtrlBinding::isFlying);
        register("elytra_fly", Priority.HIGH, entity -> entity.getPose() == Pose.FALL_FLYING && entity.isFallFlying());

        register("swim_stand", Priority.NORMAL, entity -> entity.isInWater() && !entity.onGround());
        register("attacked", Priority.NORMAL, entity -> entity.hurtTime > 0);
        register("jump", Priority.NORMAL, entity -> !entity.onGround() && !entity.isInWater());
        register("sneak", Priority.NORMAL, entity -> entity.onGround() && entity.getPose() == Pose.CROUCHING && isMoving(entity));
        register("sneaking", Priority.NORMAL, entity -> entity.onGround() && entity.getPose() == Pose.CROUCHING);

        register("run", Priority.LOW, entity -> entity.onGround() && entity.isSprinting());
        register("walk", Priority.LOW, entity -> entity.onGround() && isMoving(entity));

        register("idle", Priority.LOWEST, _ -> true);

        // 轮盘动画预测
        var("playing_extra_animation", _ -> null);

        // 条件动画的
        function("hold", HandItemCheck.holdCheck());
        function("swing", HandItemCheck.swingCheck());
        function("use", HandItemCheck.useCheck());
        function("armor", ArmorCheck.armorCheck());
        function("ride", RideCheck.rideCheck());

        // 模组的
        ImmersiveMelodiesCompat.addBinding(this);
        addModPlaceholder();

        // 硬编码预测函数用
        constValue("state_continue", STATE_CONTINUE);
        constValue("state_stop", STATE_STOP);
        constValue("state_pause", STATE_PAUSE);
        constValue("state_bypass", STATE_BYPASS);

        constValue("loop", LOOP);
        constValue("play_once", PLAY_ONCE);
        constValue("hold_on_last_frame", HOLD_ON_LAST_FRAME);

        function("set_animation", new SetAnimation());
        function("set_beginning_transition_length", new SetBeginningTransitionLength());
        function("reset", new ResetController());
        function("indicate_reload", new IndicateReload());
    }

    @SuppressWarnings("unchecked")
    private void register(String name, int priority, Predicate<IContext<LivingEntity>> predicate) {
        if (DATA == null) {
            DATA = new ReferenceArrayList[Priority.LOWEST + 1];
            for (int i = 0; i < DATA.length; i++) {
                DATA[i] = new ReferenceArrayList<>(6);
            }
        }
        Condition condition = new Condition(name, priority, predicate);
        DATA[priority].add(condition);
        livingEntityVar(name, ctx -> testCondition(name, ctx));
    }

    private void register(String name, int priority, LivingEntityPredicate predicate) {
        register(name, priority, (Predicate<IContext<LivingEntity>>) predicate);
    }

    private static boolean testCondition(String name, IContext<LivingEntity> context) {
        LivingEntity entity = context.entity();

        var stateTracker = context.animatableEntity().getStateTracker();
        if (stateTracker.getMainAnimationCache() != null) {
            return name.equals(stateTracker.getMainAnimationCache());
        }

        if (context.animatableEntity().isPreviewEntity()) {
            stateTracker.setMainAnimationCache("");
            return false;
        }

        // 载具
        Entity vehicle = entity.getVehicle();
        if (vehicle != null && vehicle.isAlive()) {
            stateTracker.setMainAnimationCache("");
            return false;
        }

        for (int i = Priority.HIGHEST; i <= Priority.LOWEST; i++) {
            for (Condition condition : DATA[i]) {
                if (condition.predicate().test(context)) {
                    stateTracker.setMainAnimationCache(condition.name());
                    return condition.name().equals(name);
                }
            }
        }
        stateTracker.setMainAnimationCache("");
        return false;
    }

    private static boolean sitInJoy(LivingEntity maid, Type type) {
        return maid.getVehicle() instanceof EntitySit sit && sit.getJoyType().equals(type.getTypeName());
    }

    private static boolean isMoving(LivingEntity entity) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float limbSwingAmount = entity.walkAnimation.speed(partialTick);
        return Math.abs(limbSwingAmount) > MIN_SPEED;
    }

    private static float getVerticalSpeed(LivingEntity entity) {
        return 20 * (float) (entity.position().y - entity.yo);
    }

    private static boolean isFlying(IContext<LivingEntity> ctx) {
        // 女仆应该不会自己飞吧
        return false;
    }

    private record Condition(String name, int priority, Predicate<IContext<LivingEntity>> predicate) {
    }

    private void addModPlaceholder() {
        livingEntityVar("carryon_type", _ -> StringUtils.EMPTY);
        livingEntityVar("carryon_is_princess", _ -> false);
        livingEntityVar("tac_hold_gun", _ -> false);
        livingEntityVar("tac_gun_type", _ -> StringUtils.EMPTY);
        livingEntityVar("tac_gun_id", _ -> StringUtils.EMPTY);
        livingEntityVar("tac_is_fire", _ -> false);
        livingEntityVar("tac_is_aim", _ -> false);
        livingEntityVar("tac_is_reload", _ -> false);
        livingEntityVar("tac_is_melee", _ -> false);
        livingEntityVar("tac_is_draw", _ -> false);
        livingEntityVar("tac_fire_mode", _ -> StringUtils.EMPTY);
        livingEntityVar("swem_is_ride", _ -> false);
        livingEntityVar("swem_state", _ -> StringUtils.EMPTY);
        livingEntityVar("parcool_state", _ -> StringUtils.EMPTY);
        livingEntityVar("slashblade_animation", _ -> StringUtils.EMPTY);
        livingEntityVar("has_sophisticated_backpack", _ -> false);
        playerVar("create_hanging_skyhook", _ -> false);
        clientPlayerVar("bcombat_attack_animation", _ -> StringUtils.EMPTY);
        clientPlayerVar("iss_animation", _ -> "");
    }

    private interface LivingEntityPredicate extends Predicate<IContext<LivingEntity>> {
        boolean testLivingEntity(LivingEntity entity);

        @Override
        default boolean test(IContext<LivingEntity> context) {
            return testLivingEntity(context.entity());
        }
    }
}
