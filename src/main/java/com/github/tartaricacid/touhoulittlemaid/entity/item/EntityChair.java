package com.github.tartaricacid.touhoulittlemaid.entity.item;

import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoChairEntity;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.ChairConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenChairGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.*;
import static net.minecraft.network.syncher.EntityDataSerializers.*;

public class EntityChair extends AbstractEntityFromItem implements OwnableEntity {
    public static final Identifier ENTITY_ID = IdentifierUtil.modLoc("chair");
    public static final ResourceKey<EntityType<?>> ENTITY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ENTITY_ID);
    public static final EntityType<EntityChair> TYPE = EntityType
            .Builder.<EntityChair>of(EntityChair::new, MobCategory.MISC)
            .sized(0.875f, 0.5f)
            .clientTrackingRange(10)
            .build(ENTITY_KEY);

    private static final EntityDataAccessor<String> MODEL_ID = SynchedEntityData.defineId(EntityChair.class, STRING);
    private static final EntityDataAccessor<Float> MOUNTED_HEIGHT = SynchedEntityData.defineId(EntityChair.class, FLOAT);
    private static final EntityDataAccessor<Boolean> TAMEABLE_CAN_RIDE = SynchedEntityData.defineId(EntityChair.class, BOOLEAN);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER = SynchedEntityData.defineId(
            EntityChair.class, OPTIONAL_LIVING_ENTITY_REFERENCE
    );

    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:cushion";
    private static final String OWNER_TAG = "Owner";

    // 仅用于 geckolib 格式的自定义坐垫模型渲染
    private @Nullable GeckoChairEntity animatable = null;

    protected EntityChair(EntityType<EntityChair> type, Level level) {
        super(type, level);
        if (level.isClientSide()) {
            this.animatable = new GeckoChairEntity(this);
        }
    }

    public EntityChair(Level level) {
        this(TYPE, level);
    }

    public EntityChair(Level level, double x, double y, double z, float yaw) {
        this(TYPE, level);
        this.setPos(x, y, z);
        this.setRot(yaw, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODEL_ID, DEFAULT_MODEL_ID);
        builder.define(MOUNTED_HEIGHT, 0f);
        builder.define(TAMEABLE_CAN_RIDE, true);
        builder.define(OWNER, Optional.empty());
    }

    @Override
    protected void pushEntities() {
        if (!isTameableCanRide()) {
            return;
        }
        if (!level.isClientSide()) {
            AABB aabb = getBoundingBox().expandTowards(0, 0.5, 0);
            level.getEntitiesOfClass(TamableAnimal.class, aabb, this::canRide)
                    .stream().findFirst()
                    .ifPresent(e -> e.startRiding(this));
        }
    }

    private boolean canRide(TamableAnimal e) {
        return !e.isInSittingPose()
               && !e.isPassenger()
               && e.getPassengers().isEmpty();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player.isShiftKeyDown()) {
            if (player.getItemInHand(hand).interactLivingEntity(player, this, hand).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new OpenChairGuiPackage(this));
            }
        } else {
            if (!level.isClientSide() && getPassengers().isEmpty() && !player.isPassenger()) {
                player.startRiding(this);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return new Vec3(0, this.getMountedHeight() + 0.125, 0);
    }

    @Override
    protected boolean canKillEntity(Player player) {
        if (ChairConfig.CHAIR_CAN_DESTROYED_BY_ANYONE.get()) {
            return true;
        }
        var reference = this.getOwnerReference();
        if (reference == null) {
            return true;
        }
        return reference.matches(player);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.WOOL_BREAK;
    }

    @Override
    protected Item getWithItem() {
        return InitItems.CHAIR.get();
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read(MODEL_ID_TAG_NAME, Codec.STRING).ifPresent(this::setModelId);
        input.read(MOUNTED_HEIGHT_TAG_NAME, Codec.FLOAT).ifPresent(this::setMountedHeight);
        input.read(TAMEABLE_CAN_RIDE_TAG_NAME, Codec.BOOL).ifPresent(this::setTameableCanRide);
        this.setOwnerReference(EntityReference.read(input, OWNER_TAG));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store(MODEL_ID_TAG_NAME, Codec.STRING, getModelId());
        output.store(MOUNTED_HEIGHT_TAG_NAME, Codec.FLOAT, getMountedHeight());
        output.store(TAMEABLE_CAN_RIDE_TAG_NAME, Codec.BOOL, isTameableCanRide());
        EntityReference.store(this.getOwnerReference(), output, OWNER_TAG);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity firstPassenger = getFirstPassenger();
        if (firstPassenger instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return super.getControllingPassenger();
    }

    public String getModelId() {
        return this.entityData.get(MODEL_ID);
    }

    public void setModelId(String modelId) {
        this.entityData.set(MODEL_ID, modelId);
    }

    public float getMountedHeight() {
        return this.entityData.get(MOUNTED_HEIGHT);
    }

    public void setMountedHeight(float height) {
        height = Mth.clamp(height, -0.5f, 2.5f);
        this.entityData.set(MOUNTED_HEIGHT, height);
    }

    public boolean isTameableCanRide() {
        return this.entityData.get(TAMEABLE_CAN_RIDE);
    }

    public void setTameableCanRide(boolean canRide) {
        this.entityData.set(TAMEABLE_CAN_RIDE, canRide);
    }

    @Override
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(OWNER).orElse(null);
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> owner) {
        this.entityData.set(OWNER, Optional.ofNullable(owner));
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.setOwnerReference(EntityReference.of(owner));
    }

    public boolean hasPassenger() {
        return !getPassengers().isEmpty();
    }

    public float getPassengerYaw() {
        if (!getPassengers().isEmpty()) {
            return getPassengers().getFirst().getYRot();
        }
        return 0;
    }

    public float getPassengerPitch() {
        if (!getPassengers().isEmpty()) {
            return getPassengers().getFirst().getXRot();
        }
        return 0;
    }

    @Override
    protected ItemStack getKilledStack() {
        ItemStack instance = InitItems.CHAIR.get().getDefaultInstance();
        ItemChair.Data data = new ItemChair.Data(
                getModelId(), getMountedHeight(),
                isTameableCanRide(), isNoGravity()
        );
        return ItemChair.setData(instance, data);
    }

    @Nullable
    public GeckoChairEntity getAnimatableEntity() {
        return animatable;
    }
}
