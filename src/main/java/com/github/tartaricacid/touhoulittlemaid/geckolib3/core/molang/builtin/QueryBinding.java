package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.builtin;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions.Rot2Camera;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.ContextBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.builtin.query.*;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.AnimationContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.ControllerContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.MathUtil;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.util.EquipmentUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class QueryBinding extends ContextBinding {
    public static final QueryBinding INSTANCE = new QueryBinding();

    @SuppressWarnings("resource")
    private QueryBinding() {
        function("debug_output", new DebugOutput());

        function("biome_has_all_tags", new BiomeHasAllTags());
        function("biome_has_any_tag", new BiomeHasAnyTag());
        function("relative_block_has_all_tags", new RelativeBlockHasAllTags());
        function("relative_block_has_any_tag", new RelativeBlockHasAnyTag());
        function("is_item_name_any", new ItemNameAny());
        function("equipped_item_all_tags", new EquippedItemAllTags());
        function("equipped_item_any_tag", new EquippedItemAnyTags());
        function("position", new Position());
        function("position_delta", new PositionDelta());
        function("rotation_to_camera", new Rot2Camera());

        function("max_durability", new ItemMaxDurability());
        function("remaining_durability", new ItemRemainingDurability());

        var("actor_count", ctx -> ctx.level().getEntityCount());
        var("anim_time", ctx -> getAnimationContext(ctx).map(AnimationContext::animTime).orElse(0f));
        // 目前控制器只能同时播放单一动画，所以两个 molang 都是一样的结果
        var("all_animations_finished", ctx -> getControllerContext(ctx).map(ControllerContext::isAllAnimationsFinished).orElse(false));
        var("any_animation_finished", ctx -> getControllerContext(ctx).map(ControllerContext::isAnyAnimationFinished).orElse(false));
        var("life_time", ctx -> ctx.animatableEntity().getSeekTime() / 20.0);
        var("head_x_rotation", ctx -> ctx.data().netHeadYaw);
        var("head_y_rotation", ctx -> ctx.data().headPitch);
        var("moon_phase", ctx ->  ctx.mc().gameRenderer.getMainCamera().attributeProbe().getValue(EnvironmentAttributes.MOON_PHASE, ctx.animationEvent().getPartialTick()));
        var("time_of_day", ctx -> ctx.level().dimensionType().defaultClock().map(clock ->
                MolangUtils.normalizeTime(ctx.level().clockManager().getTotalTicks(clock))).orElseGet(() -> 0f));
        var("time_stamp", ctx -> ctx.level().getGameTime());
        var("delta_time", ctx -> ctx.animatableEntity().getStateTracker().getRenderTickDelta() / 20);

        entityVar("yaw_speed", QueryBinding::getYawSpeed);
        entityVar("cardinal_facing_2d", ctx -> ctx.entity().getDirection().get3DDataValue());
        entityVar("distance_from_camera", ctx -> ctx.mc().gameRenderer.getMainCamera().position().distanceTo(ctx.entity().position()));
        entityVar("eye_target_x_rotation", ctx -> ctx.entity().getViewXRot(ctx.animationEvent().getRequestedPartialTick()));
        entityVar("eye_target_y_rotation", ctx -> ctx.entity().getViewYRot(ctx.animationEvent().getRequestedPartialTick()));
        entityVar("ground_speed", ctx -> getGroundSpeed(ctx.entity()));
        avatarVar("modified_distance_moved", ctx -> ctx.entity().avatarState().getInterpolatedWalkDistance(ctx.animationEvent().getPartialTick()));
        entityVar("vertical_speed", QueryBinding::getVerticalSpeed);
        entityVar("walk_distance", ctx -> ctx.entity().moveDist);
        entityVar("has_rider", ctx -> ctx.entity().isVehicle());
        entityVar("is_first_person", _ -> CameraType.THIRD_PERSON_BACK.ordinal());
        entityVar("is_in_water", ctx -> ctx.entity().isInWater());
        entityVar("is_in_water_or_rain", ctx -> ctx.entity().isInWaterOrRain() || ctx.entity().getInBlockState().is(Blocks.BUBBLE_COLUMN));
        entityVar("is_on_fire", ctx -> ctx.entity().isOnFire());
        entityVar("is_on_ground", ctx -> ctx.entity().onGround());
        entityVar("is_riding", ctx -> ctx.entity().isPassenger());
        entityVar("is_sneaking", ctx -> ctx.entity().onGround() && ctx.entity().getPose() == Pose.CROUCHING);
        entityVar("is_spectator", ctx -> ctx.entity().isSpectator());
        entityVar("is_sprinting", ctx -> ctx.entity().isSprinting());
        entityVar("is_swimming", ctx -> ctx.entity().isSwimming());

        livingEntityVar("body_x_rotation", ctx -> Mth.lerp(ctx.animationEvent().getRequestedPartialTick(), ctx.entity().xRotO, ctx.entity().getXRot()));
        livingEntityVar("body_y_rotation", ctx -> Mth.wrapDegrees(Mth.lerp(ctx.animationEvent().getRequestedPartialTick(), ctx.entity().yBodyRotO, ctx.entity().yBodyRot)));
        livingEntityVar("health", QueryBinding::getHealth);
        livingEntityVar("max_health", QueryBinding::getMaxHealth);
        livingEntityVar("hurt_time", ctx -> ctx.entity().hurtTime);
        livingEntityVar("is_eating", ctx -> ctx.entity().getUseItem().getUseAnimation() == ItemUseAnimation.EAT);
        livingEntityVar("is_playing_dead", ctx -> ctx.entity().isDeadOrDying());
        livingEntityVar("is_sleeping", ctx -> ctx.entity().isSleeping());
        livingEntityVar("is_using_item", ctx -> ctx.entity().isUsingItem());
        livingEntityVar("item_in_use_duration", ctx -> ctx.entity().getTicksUsingItem() / 20.0);
        livingEntityVar("item_max_use_duration", ctx -> getMaxUseDuration(ctx.entity()) / 20.0);
        livingEntityVar("item_remaining_use_duration", ctx -> ctx.entity().getUseItemRemainingTicks() / 20.0);
        livingEntityVar("equipment_count", ctx -> getEquipmentCount(ctx.entity()));

        avatarVar("cape_flap_amount", QueryBinding::getCapeFlapAmount);
        maidEntityVar("player_level", QueryBinding::getExpLevel);
        maidEntityVar("is_jumping", ctx -> !isFlying(ctx) && !ctx.entity().isPassenger() && !ctx.entity().onGround() && !ctx.entity().isInWater());

        clientPlayerVar("has_cape", ctx -> hasCape(ctx.entity()));
    }

    private static Optional<AnimationContext> getAnimationContext(IContext<?> ctx) {
        return Optional.ofNullable(ctx.animationContext());
    }

    private static Optional<ControllerContext> getControllerContext(IContext<?> ctx) {
        return Optional.ofNullable(ctx.controllerContext());
    }

    private static boolean isFlying(IContext<EntityMaid> ctx) {
        return false;   // 女仆会自己飞吗？
    }

    private static int getExpLevel(IContext<EntityMaid> ctx) {
        return ctx.entity().components().stats.getExperience();
    }

    private static Object getHealth(IContext<LivingEntity> ctx) {
        return ctx.entity().getHealth();
    }

    private static Object getMaxHealth(IContext<LivingEntity> ctx) {
        return ctx.entity().getMaxHealth();
    }

    private static boolean hasCape(AbstractClientPlayer player) {
        return player.getSkin().cape() != null && !player.isInvisible() && player.isModelPartShown(PlayerModelPart.CAPE);
    }

    private static int getEquipmentCount(LivingEntity entity) {
        int count = 0;
        for (var slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            var stack = EquipmentUtil.getEquippedItem(entity, slot);
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static int getMaxUseDuration(LivingEntity player) {
        ItemStack useItem = player.getUseItem();
        if (useItem.isEmpty()) {
            return 0;
        } else {
            return useItem.getUseDuration(player);
        }
    }

    private static float getYawSpeed(IContext<Entity> ctx) {
        return 20 * (ctx.entity().getYRot() - ctx.entity().yRotO);
    }

    private static float getGroundSpeed(Entity player) {
        Vec3 velocity = player.getDeltaMovement();
        return 20 * Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
    }

    private static float getVerticalSpeed(IContext<Entity> ctx) {
        var stateStacker = ctx.animatableEntity().getStateTracker();
        var posDelta = stateStacker.getPositionDelta();
        return 20 * (float) posDelta.y / stateStacker.getRenderTickDelta();
    }

    private static float getCapeFlapAmount(IContext<ClientAvatarEntity> ctx) {
        float partialTicks = ctx.animationEvent().getPartialTick();
        var avatarState = ctx.entity().avatarState();
        var renderState = ctx.animationEvent().getRenderState();
        var entity = (LivingEntity) ctx.entity();

        float d0 = (float) (avatarState.getInterpolatedCloakX(partialTicks) - renderState.x);
        float d1 = (float) (avatarState.getInterpolatedCloakY(partialTicks) - renderState.y);
        float d2 = (float) (avatarState.getInterpolatedCloakZ(partialTicks) - renderState.z);
        float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float d3 = Mth.sin(f * (MathUtil.PI / 180F));
        float d4 = (-Mth.cos(f * (MathUtil.PI / 180F)));
        float f1 = d1 * 10.0F;
        f1 = Mth.clamp(f1, -6.0F, 32.0F);
        float f2 = (d0 * d3 + d2 * d4) * 100.0F;
        f2 = Mth.clamp(f2, 0.0F, 150.0F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        float f4 = avatarState.getInterpolatedBob(partialTicks);
        f1 = f1 + Mth.sin(avatarState.getInterpolatedWalkDistance(partialTicks) * 6.0F) * 32.0F * f4;
        if (renderState instanceof HumanoidRenderState humanoidRenderState && humanoidRenderState.isCrouching) {
            f1 += 25.0F;
        }

        return Mth.clamp((6.0F + f2 / 2.0F + f1) / 108, 0, 1);
    }
}
