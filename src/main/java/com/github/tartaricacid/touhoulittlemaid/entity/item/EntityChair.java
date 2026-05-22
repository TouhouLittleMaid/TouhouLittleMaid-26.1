package com.github.tartaricacid.touhoulittlemaid.entity.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoChairEntity;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.ChairConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenChairGuiPackage;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.*;

public class EntityChair extends AbstractEntityFromItem {
    public static final EntityType<EntityChair> TYPE = EntityType.Builder.<EntityChair>of(EntityChair::new, MobCategory.MISC)
            .sized(0.875f, 0.5f)
            .clientTrackingRange(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "chair")));

    private static final EntityDataAccessor<String> MODEL_ID = SynchedEntityData.defineId(EntityChair.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> MOUNTED_HEIGHT = SynchedEntityData.defineId(EntityChair.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> TAMEABLE_CAN_RIDE = SynchedEntityData.defineId(EntityChair.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER = SynchedEntityData.defineId(EntityBroom.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);


    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:cushion";

    private GeckoChairEntity animatable;

    protected EntityChair(EntityType<EntityChair> type, Level worldIn) {
        super(type, worldIn);
        if (worldIn.isClientSide()) {
            this.animatable = new GeckoChairEntity(this);
        }
    }

    public EntityChair(Level worldIn) {
        this(TYPE, worldIn);
    }

    public EntityChair(Level worldIn, double x, double y, double z, float yaw) {
        this(TYPE, worldIn);
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
            List<TamableAnimal> list = level.getEntitiesOfClass(TamableAnimal.class,
                    getBoundingBox().expandTowards(0, 0.5, 0),
                    e -> !e.isInSittingPose() && !e.isPassenger() && e.getPassengers().isEmpty());
            list.stream().findFirst().ifPresent(entity -> entity.startRiding(this));
        }
    }

    /**
     * 此参数会影响钓鱼钩和客户端的渲染交互。
     * 所以将其设计为仅修改服务端，避免影响客户端渲染交互，同时不会在服务端被钓鱼钩影响
     */
    @Override
    public boolean isPickable() {
        return !EffectiveSide.get().isServer();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player.isShiftKeyDown()) {
            if (player.getItemInHand(hand).interactLivingEntity(player, this, hand).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new OpenChairGuiPackage(getId()));
            }
        } else {
            if (!level.isClientSide() && getPassengers().isEmpty() && !player.isPassenger()) {
                player.startRiding(this);
            }
        }
        return InteractionResult.SUCCESS;
    }

    //FIXME 实体CULLING控制从这个方法出了
//    @Nonnull
//    @Override
//    public AABB getBoundingBoxForCulling() {
//        BedrockModel<EntityChair> model = CustomPackLoader.CHAIR_MODELS.getModel(getModelId()).orElse(null);
//        if (model == null) {
//            return super.getBoundingBoxForCulling();
//        }
//        return model.getRenderBoundingBox().move(position());
//    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return new Vec3(0, getMountedHeight() + 0.125, 0);
    }

    @Override
    protected boolean canKillEntity(Player player) {
        if (ChairConfig.CHAIR_CAN_DESTROYED_BY_ANYONE.get()) {
            return true;
        }
        return this.getOwnerUUID().map(uuid -> player.getUUID().equals(uuid)).orElse(true);
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
        input.read(OWNER_UUID_TAG_NAME, UUIDUtil.CODEC).ifPresent(this::setOwnerUUID);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store(MODEL_ID_TAG_NAME, Codec.STRING, getModelId());
        output.store(MOUNTED_HEIGHT_TAG_NAME, Codec.FLOAT, getMountedHeight());
        output.store(TAMEABLE_CAN_RIDE_TAG_NAME, Codec.BOOL, isTameableCanRide());
        output.storeNullable(OWNER_UUID_TAG_NAME, UUIDUtil.CODEC, getOwnerUUID().orElse(null));
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

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(OWNER).map(EntityReference::getUUID);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER, Optional.ofNullable(uuid).map(EntityReference::of));
    }

    public void setOwner(@Nullable Player player) {
        if (player != null) {
            this.setOwnerUUID(player.getUUID());
        }
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

    public float getYaw() {
        return getYRot();
    }

    public float getPassengerPitch() {
        if (!getPassengers().isEmpty()) {
            return getPassengers().getFirst().getXRot();
        }
        return 0;
    }

    @Deprecated
    public int getDim() {
        ResourceKey<Level> dim = this.level.dimension();
        if (dim.equals(Level.OVERWORLD)) {
            return 0;
        }
        if (dim.equals(Level.NETHER)) {
            return -1;
        }
        if (dim.equals(Level.END)) {
            return 1;
        }
        return 0;
    }

    @Override
    protected ItemStack getKilledStack() {
        return ItemChair.setData(InitItems.CHAIR.get().getDefaultInstance(),
                new ItemChair.Data(getModelId(), getMountedHeight(), isTameableCanRide(), isNoGravity()));
    }

    public GeckoChairEntity getAnimatableEntity() {
        return animatable;
    }
}
