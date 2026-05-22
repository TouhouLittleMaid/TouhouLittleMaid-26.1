package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions.*;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.variable.FirstPersonModHideVariable;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.variable.LadderFacingVariable;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.variable.MoveInputVariable;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.variable.TextureNameVariable;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.ContextBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.util.EquipmentUtil;
import com.github.tartaricacid.touhoulittlemaid.util.LazyValue;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Locale;

@SuppressWarnings("resource")
public class YSMBinding extends ContextBinding {
    public static final LazyValue<YSMBinding> INSTANCE = new LazyValue<>(YSMBinding::new);

    @SuppressWarnings("resource")
    private YSMBinding() {
        function("dump_equipped_item", new DumpEquippedItem());
        function("dump_relative_block", new DumpRelativeBlock());
        var("dump_mods", YSMBinding::dumpMods);
        entityVar("dump_effects", YSMBinding::dumpEffects);
        entityVar("dump_biome", YSMBinding::dumpBiome);

        function("mod_version", new ModVersion());
        function("equipped_enchantment_level", new EquippedEnchantmentLevel());
        function("effect_level", new EffectLevel());
        function("relative_block_name", new RelativeBlockName());
        function("relative_block_name_any", new RelativeBlockNameAny());

        function("bone_rot", new BoneRotation());
        function("bone_pos", new BonePosition());
        function("bone_scale", new BoneScale());
        function("bone_pivot_abs", new BoneAbsolutePivot());

        var("head_yaw", ctx -> ctx.data().netHeadYaw);
        var("head_pitch", ctx -> ctx.data().headPitch);
        var("weather", ctx -> getWeather(ctx.level()));
        var("dimension_name", ctx -> ctx.level().dimension().identifier().toString());
        var("fps", ctx -> Minecraft.getInstance().getFps());
        var("time_delta", ctx -> ctx.animatableEntity().getStateTracker().getRenderTickDelta() / 20);

        entityVar("ground_speed2", YSMBinding::getGroundSpeed2);

        entityVar("input_vertical", MoveInputVariable::getVertical);
        entityVar("input_horizontal", MoveInputVariable::getHorizontal);
        entityVar("person_view", _ -> CameraType.THIRD_PERSON_FRONT.ordinal());
        entityVar("rendering_in_paperdoll", ctx -> false);
        entityVar("rendering_in_inventory", ctx -> ctx.animatableEntity().isPreviewEntity() || ctx.animationEvent().getRenderContext().inventory());
        entityVar("block_light", ctx -> ctx.level().getBrightness(LightLayer.BLOCK, ctx.entity().blockPosition()));
        entityVar("sky_light", ctx -> ctx.level().getBrightness(LightLayer.SKY, ctx.entity().blockPosition()));

        entityVar("is_passenger", ctx -> ctx.entity().isPassenger());
        entityVar("is_sleep", ctx -> ctx.entity().getPose() == Pose.SLEEPING);
        entityVar("is_sneak", ctx -> ctx.entity().onGround() && ctx.entity().getPose() == Pose.CROUCHING);
        entityVar("biome_category", ctx -> getBiomeCategory(ctx.entity()));
        entityVar("is_open_air", ctx -> isOpenAir(ctx.entity()));
        entityVar("eye_in_water", ctx -> ctx.entity().isUnderWater());
        entityVar("frozen_ticks", ctx -> ctx.entity().getTicksFrozen());
        entityVar("air_supply", ctx -> ctx.entity().getAirSupply());
        entityVar("delta_movement_length", ctx -> ctx.entity().getDeltaMovement().length());

        livingEntityVar("has_helmet", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.HEAD));
        livingEntityVar("has_chest_plate", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.CHEST));
        livingEntityVar("has_leggings", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.LEGS));
        livingEntityVar("has_boots", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.FEET));
        livingEntityVar("has_mainhand", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.MAINHAND));
        livingEntityVar("has_offhand", ctx -> getSlotValue(ctx.entity(), EquipmentSlot.OFFHAND));
        livingEntityVar("has_elytra", ctx -> !EquipmentUtil.getEquippedElytraItem(ctx.entity()).isEmpty());
        livingEntityVar("is_riptide", ctx -> ctx.entity().isAutoSpinAttack());
        livingEntityVar("armor_value", ctx -> ctx.entity().getArmorValue());
        livingEntityVar("hurt_time", ctx -> ctx.entity().hurtTime);
        livingEntityVar("is_close_eyes", ctx -> getEyeCloseState(ctx.animationEvent(), ctx.entity()));
        livingEntityVar("on_ladder", ctx -> ctx.entity().onClimbable());
        livingEntityVar("ladder_facing", new LadderFacingVariable());
        livingEntityVar("arrow_count", ctx -> ctx.entity().getArrowCount());
        livingEntityVar("stinger_count", ctx -> ctx.entity().getStingerCount());
        livingEntityVar("entity_type", YSMBinding::getEntityType);
        livingEntityVar("is_player", ctx -> "player".equals(getEntityType(ctx)));
        livingEntityVar("is_maid", ctx -> "maid".equals(getEntityType(ctx)));
        // 为了兼容其他模组，只有玩家能返回这个值，其他都是满值（20）
        livingEntityVar("food_level", YSMBinding::getFoodLevel);

        livingEntityVar("xxa", YSMBinding::getXxa);
        livingEntityVar("yya", YSMBinding::getYya);
        livingEntityVar("zza", YSMBinding::getZza);

        livingEntityVar("mainhand_charged_crossbow", ctx -> isChargedCrossbow(ctx, InteractionHand.MAIN_HAND));
        livingEntityVar("offhand_charged_crossbow", ctx -> isChargedCrossbow(ctx, InteractionHand.OFF_HAND));
        maidEntityVar("is_fishing", YSMBinding::isFishing);

        livingEntityVar("swinging", ctx -> ctx.entity().swinging);
        livingEntityVar("swing_time", ctx -> ctx.entity().swingTime);
        livingEntityVar("swinging_arm", ctx -> {
            InteractionHand hand = ctx.entity().swingingArm;
            return hand == InteractionHand.MAIN_HAND ? 0 : 1;
        });
        livingEntityVar("attack_time", ctx -> ctx.entity().getAttackAnim(ctx.animationEvent().getPartialTick()));

        playerVar("texture_name", new TextureNameVariable());
        playerVar("first_person_mod_hide", new FirstPersonModHideVariable());
        playerVar("has_left_shoulder_parrot", ctx -> hasParrot(ctx.entity(), true));
        playerVar("has_right_shoulder_parrot", ctx -> hasParrot(ctx.entity(), false));
        playerVar("left_shoulder_parrot_variant", ctx -> getParrotVariant(ctx.entity(), true));
        playerVar("right_shoulder_parrot_variant", ctx -> getParrotVariant(ctx.entity(), false));

        playerVar("attack_damage", ctx -> ctx.entity().getAttributeValue(Attributes.ATTACK_DAMAGE));
        playerVar("attack_speed", ctx -> ctx.entity().getAttributeValue(Attributes.ATTACK_SPEED));
        playerVar("attack_knockback", ctx -> ctx.entity().getAttributeValue(Attributes.ATTACK_KNOCKBACK));
        playerVar("movement_speed", ctx -> ctx.entity().getAttributeValue(Attributes.MOVEMENT_SPEED));
        playerVar("knockback_resistance", ctx -> ctx.entity().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        playerVar("luck", ctx -> ctx.entity().getAttributeValue(Attributes.LUCK));


        playerVar("block_reach", ctx -> ctx.entity().getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE));
        playerVar("entity_reach", ctx -> ctx.entity().getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
        playerVar("swim_speed", ctx -> ctx.entity().getAttributeValue(NeoForgeMod.SWIM_SPEED));
        playerVar("entity_gravity", ctx -> ctx.entity().getAttributeValue(Attributes.GRAVITY));
        playerVar("step_height_addition", ctx -> ctx.entity().getAttributeValue(Attributes.STEP_HEIGHT) - 0.6);
        playerVar("nametag_distance", ctx -> ctx.entity().getAttributeValue(NeoForgeMod.NAMETAG_DISTANCE));

        clientPlayerVar("elytra_rot_x", ctx -> Math.toDegrees(ctx.entity().elytraAnimationState.getRotX(ctx.animationEvent().getRequestedPartialTick())));
        clientPlayerVar("elytra_rot_y", ctx -> Math.toDegrees(ctx.entity().elytraAnimationState.getRotY(ctx.animationEvent().getRequestedPartialTick())));
        clientPlayerVar("elytra_rot_z", ctx -> Math.toDegrees(ctx.entity().elytraAnimationState.getRotZ(ctx.animationEvent().getRequestedPartialTick())));

        localPlayerVar("hit_target_id", YSMBinding::getHitId);
        localPlayerVar("hit_target_type", YSMBinding::getHitType);

        function("first_order", new FirstOrderFunction());
        function("second_order", new SecondOrderFunction());
        function("particle", new ParticleFunction(false));
        function("abs_particle", new ParticleFunction(true));
        function("perlin_noise", new PerlinNoiseFunction());
        function("play_sound", new SoundFunction.Play());
        function("stop_sound", new SoundFunction.Stop());
        function("stop_all_sounds", new SoundFunction.StopAll());
        function("keyboard", new InputCheck.Keyboard());
        function("mouse", new InputCheck.Mouse());
        function("sync", new Sync());
        function("defer", new Defer());

        projectileVar("projectile_owner", ctx -> ctx.createChild(ctx.entity().getOwner()));

        throwableItemProjectileVar("throwable_item", _ -> null);

        fishingHookVar("hooked_in", _ -> null);
        fishingHookVar("is_biting", _ -> null);

        abstractArrowVar("on_ground_time", _ -> null);
        abstractArrowVar("in_ground", _ -> null);
        abstractArrowVar("is_spectral_arrow", _ -> null);
        abstractArrowVar("shoot_item_id", _ -> null);
    }

    private static String getHitId(IContext<LocalPlayer> context) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult result) {
            if (result.getType() == HitResult.Type.MISS) {
                return "";
            }
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return "";
            }
            BlockState blockState = level.getBlockState(result.getBlockPos());
            Identifier id = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
            return id.toString();
        }

        if (hitResult instanceof EntityHitResult result) {
            Entity entity = result.getEntity();
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            return id.toString();
        }

        return "";
    }

    private static String getHitType(IContext<LocalPlayer> context) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult == null) {
            return StringUtils.EMPTY;
        }
        return switch (hitResult.getType()) {
            case BLOCK -> "blockState";
            case ENTITY -> "entity";
            default -> StringUtils.EMPTY;
        };
    }

    private static float getGroundSpeed2(IContext<Entity> ctx) {
        var stateStacker = ctx.animatableEntity().getStateTracker();
        var posDelta = stateStacker.getPositionDelta();
        return 20 * Mth.sqrt((float) ((posDelta.x * posDelta.x) + (posDelta.z * posDelta.z))) / stateStacker.getRenderTickDelta();
    }

    private static float getXxa(IContext<LivingEntity> ctx) {
        return ctx.entity().xxa;
    }

    private static float getYya(IContext<LivingEntity> ctx) {
        return ctx.entity().yya;
    }

    private static float getZza(IContext<LivingEntity> ctx) {
        return ctx.entity().zza;
    }

    private static boolean inShieldBlockCooldown(IContext<Player> context) {
        // TODO: 女仆会用盾吗？
        return false;
    }

    private static boolean isFishing(IContext<EntityMaid> ctx) {
        return ctx.entity().fishing != null;
    }

    private static boolean isChargedCrossbow(IContext<LivingEntity> ctx, InteractionHand hand) {
        ItemStack itemInHand = ctx.entity().getItemInHand(hand);
        return itemInHand.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemInHand);
    }

    private static String getEntityType(IContext<LivingEntity> ctx) {
        LivingEntity entity = ctx.entity();
        if (entity instanceof Player) {
            return "player";
        }
        Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if ("touhou_little_maid".equals(key.getNamespace()) && "maid".equals(key.getPath())) {
            return "maid";
        }
        return key.toString();
    }

    private static Object getFoodLevel(IContext<LivingEntity> ctx) {
        if (ctx.entity() instanceof Player player) {
            return player.getFoodData().getFoodLevel();
        } else {
            return 20;
        }
    }

    private static boolean getEyeCloseState(AnimationEvent<?> animationEvent, LivingEntity player) {
        float remainder = (animationEvent.getRenderTicks() + Math.abs(player.getUUID().getLeastSignificantBits()) % 10) % 90;
        boolean isBlinkTime = 85 < remainder && remainder < 90;
        return player.isSleeping() || isBlinkTime;
    }

    private static boolean getSlotValue(LivingEntity entity, EquipmentSlot slot) {
        return !EquipmentUtil.getEquippedItem(entity, slot).isEmpty();
    }

    private static int getWeather(ClientLevel world) {
        if (world.isThundering()) {
            return 2;
        } else if (world.isRaining()) {
            return 1;
        }
        return 0;
    }

    @Deprecated
    private String getBiomeCategory(Entity entity) {
        return null;
    }

    private static Object dumpMods(IContext<?> context) {
        if (!context.isDebugEnabled()) {
            return null;
        }

        ModList.get().getMods().stream().sorted(Comparator.comparing(IModInfo::getDisplayName)).forEach(mod -> {
            context.debugPrint(Component.literal("Mod: display ").append(ComponentUtils.copyOnClickText(mod.getDisplayName()))
                    .append(Component.literal("  id ").append(ComponentUtils.copyOnClickText(mod.getModId()))));
        });
        return null;
    }

    private static Object dumpEffects(IContext<Entity> context) {
        if (!context.isDebugEnabled()) {
            return null;
        }

        if (context.entity() instanceof Arrow arrow) {
            for (var instance : arrow.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
                instance.getEffect().unwrapKey().ifPresent(id -> {
                    context.debugPrint(Component.literal("Effect: display ").append(ComponentUtils.copyOnClickText(instance.getEffect().value().getDisplayName().getString(99)))
                            .append(Component.literal("  name ").append(ComponentUtils.copyOnClickText(id.identifier().toString())))
                            .append("  lv=").append(String.valueOf(instance.getAmplifier() + 1)));
                });
            }
        } else if (context.entity() instanceof LivingEntity livingEntity) {
            for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
                instance.getEffect().unwrapKey().ifPresent(id -> {
                    context.debugPrint(Component.literal("Effect: display ").append(ComponentUtils.copyOnClickText(instance.getEffect().value().getDisplayName().getString(99)))
                            .append(Component.literal("  name ").append(ComponentUtils.copyOnClickText(id.identifier().toString())))
                            .append("  lv=").append(String.valueOf(instance.getAmplifier() + 1)));
                });
            }
        }

        return null;
    }

    private static Object dumpBiome(IContext<Entity> context) {
        if (!context.isDebugEnabled()) {
            return null;
        }

        Holder<Biome> biome = context.entity().level().getBiome(context.entity().blockPosition());
        biome.unwrapKey().ifPresent(p -> {
            context.debugPrint(Component.literal("Name ").append(ComponentUtils.copyOnClickText(p.identifier().toString())));
        });
        biome.tags().forEach(tag -> {
            context.debugPrint(Component.literal("Tag ").append(ComponentUtils.copyOnClickText(tag.location().toString())));
        });

        return null;
    }

    private static boolean isOpenAir(Entity entity) {
        BlockPos blockpos = entity.blockPosition();
        if (!entity.level().canSeeSky(blockpos)) {
            return false;
        }
        if (entity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos).getY() > blockpos.getY()) {
            return false;
        }
        return true;
    }

    private static String getParrotVariant(Player player, boolean leftShoulder) {
        return (leftShoulder ? player.getShoulderParrotLeft() : player.getShoulderParrotRight())
                .map(variant -> variant.getSerializedName().toLowerCase(Locale.ENGLISH))
                .orElse("empty");
    }

    private static boolean hasParrot(Player player, boolean leftShoulder) {
        return (leftShoulder ? player.getShoulderParrotLeft() : player.getShoulderParrotRight()).isPresent();
    }
}
