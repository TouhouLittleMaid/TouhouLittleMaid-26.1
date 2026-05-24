package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.AnimationUpdateManager;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.ChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer.LayerMaidBackItem;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer.LayerMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer.LayerMaidBipedHead;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.layer.LayerMaidHeldItem;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.ModelType;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.SpecialMaidModelResolver;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.GunClientUtil;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.SWarfareCompat;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("rawtypes,unchecked")
public class EntityMaidRenderer extends MobRenderer<Mob, EntityMaidRenderState, EntityMaidModel> {
    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/empty.png");
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    /**
     * YSM 到时候会把渲染器加入其中
     */
    public static @Nullable Function<EntityRendererProvider.Context, IGeoEntityRenderer<Mob>> YSM_ENTITY_MAID_RENDERER;
    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;
    /**
     * 女仆模组自带的 GeckoLib 模型渲染
     */
    private final GeckoEntityMaidRenderer geckoEntityMaidRenderer;
    /**
     * YSM 借用的渲染类型，和上述互斥
     */
    // private @Nullable IGeoEntityRenderer<Mob> ysmMaidRenderer;
    private ChatBubbleRenderer chatBubbleRenderer2;
    private CameraRenderState cameraRenderState;

    public EntityMaidRenderer(EntityRendererProvider.Context manager) {
        super(manager, new EntityMaidModel(), 0.5f);
        this.itemModelResolver = manager.getItemModelResolver();
        this.blockModelResolver = manager.getBlockModelResolver();
        this.addLayer(new LayerMaidHeldItem(this));
        this.addLayer(new LayerMaidBipedHead(this, Minecraft.getInstance().getBlockEntityRenderDispatcher()));
        this.addLayer(new LayerMaidBackpack(this, manager.getModelSet()));
        this.addLayer(new LayerMaidBackItem(this));
        // this.addLayer(new LayerMaidBanner(this));
        this.addAdditionMaidLayer(manager);
        this.geckoEntityMaidRenderer = new GeckoEntityMaidRenderer<>(manager);
        this.initYsmModelRenderer(manager);
        this.chatBubbleRenderer2 = new ChatBubbleRenderer(this);
    }

    @Override
    public EntityMaidRenderState createRenderState() {
        return new EntityMaidRenderState();
    }

    @Override
    public void extractRenderState(Mob entity, EntityMaidRenderState state, float partialTicks) {
        state.clear();
        super.extractRenderState(entity, state, partialTicks);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, itemModelResolver, partialTicks);

        state.customName = entity.getCustomName();
        state.entity = entity;  // TODO
        state.gameTime = entity.level().getGameTime();
        state.dimension = entity.level().dimension();
        state.raining = entity.level().isRaining();
        state.thundering = entity.level().isThundering();
        state.uuidLeastSignificantBits = entity.getUUID().getLeastSignificantBits();
        state.attackAnim = entity.attackAnim;
        state.swingTime = entity.swingTime;
        state.sleeping = entity.isSleeping();
        state.passenger = entity.isPassenger();
        state.usingItem = entity.isUsingItem();
        state.usedItemHand = entity.getUsedItemHand();
        state.swingingArm = entity.swingingArm;
        state.hasMainHandItem = !entity.getMainHandItem().isEmpty();
        state.armorValue = entity.getArmorValue();
        state.health = entity.getHealth();
        state.maxHealth = entity.getMaxHealth();

        IMaid maid = IMaid.convert(entity);
        if (maid == null) {
            return;
        }

        state.begging = maid.isBegging();
        state.swingingArms = maid.isSwingingArms();
        state.maidInSittingPose = maid.isMaidInSittingPose();
        state.hasHelmet = maid.hasHelmet();
        state.hasChestPlate = maid.hasChestPlate();
        state.hasLeggings = maid.hasLeggings();
        state.hasBoots = maid.hasBoots();
        state.hasBackpack = maid.hasBackpack();
        state.hurt = maid.onHurt();
        state.taskId = maid.getTask().getUid().getPath();
        state.modelId = maid.getModelId();

        // 卓越前线实体隐藏
        if (SWarfareCompat.shouldHideLivingRender(entity)) {
            return;
        }

        // 读取默认模型，用于清除不存在模型的缓存残留
        CustomPackLoader.MAID_MODELS.getModel(DEFAULT_MODEL_ID).ifPresent(model -> state.bedrockModel = model);
        CustomPackLoader.MAID_MODELS.getInfo(DEFAULT_MODEL_ID).ifPresent(mainInfo -> state.mainInfo = mainInfo);
        CustomPackLoader.MAID_MODELS.getAnimation(DEFAULT_MODEL_ID).ifPresent(animations -> state.mainAnimations = animations);

        // 直接特殊模型解析（替代 RenderMaidEvent 事件流）
        if (!SpecialMaidModelResolver.resolveSpecialModel(state, CustomPackLoader.MAID_MODELS)) {
            // 通过模型 id 获取对应数据
            String modelId = maid.getModelId();
            CustomPackLoader.MAID_MODELS.getModel(modelId).ifPresent(model -> state.bedrockModel = model);
            CustomPackLoader.MAID_MODELS.getInfo(modelId).ifPresent(mainInfo -> state.mainInfo = mainInfo);
            CustomPackLoader.MAID_MODELS.getAnimation(modelId).ifPresent(animations -> state.mainAnimations = animations);
        }

        // 头部物品
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof AbstractSkullBlock) {
                // TODO: 复刻 SkullBlockRenderer 的逻辑，extract 至 state.headSkull
            } else {
                blockModelResolver.update(state.headBlock, blockItem.getBlock().defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
            }
        } else if (SimpleHatsCompat.isHatItem(headItem)) {
            SimpleHatsCompat.extract(state.simpleHat, headItem);
        }

        var maidEntity = IMaid.convertToMaid(entity);

        // 暂定只能女仆显示
        if (maidEntity != null && MaidConfig.GLOBAL_MAID_SHOW_CHAT_BUBBLE.get() && maidEntity.getConfigManager().isChatBubbleShow()) {
            Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTicks));
            if (vec3 != null) {
                state.showBubble = true;
                state.bubbleOffset = vec3;
            }
        }

        // 背部物品
        if (maidEntity != null) {
            state.showBackpack = maidEntity.getConfigManager().isShowBackpack();
            if (!entity.isSleeping() && !entity.isInvisible()) {
                state.backpack = state.showBackpack ? maidEntity.getMaidBackpackType() : BackpackManager.getEmptyBackpack();
                ItemStack backpackShowingItem = maidEntity.getBackpackShowItem();
                if (GunClientUtil.isGun(backpackShowingItem)) {
                    // 目前 26.1 没有枪械模组
                } else if (backpackShowingItem.getItem() instanceof BannerItem item) {
                    // TODO: 复刻 BannerRenderer 的逻辑，extract 至 state.backBanner
                } else {
                    itemModelResolver.updateForLiving(state.backItem, backpackShowingItem, ItemDisplayContext.FIXED, entity);
                }
            }
        }

        state.playerVehicle = entity.getVehicle() instanceof Player;
        if (maidEntity != null) {
            state.sitting = maidEntity.isMaidInSittingPose();
        }

        // 准备各模型类型的状态
        if (maidEntity != null && maidEntity.isYsmModel()) {
            // 还没想好需要什么
            state.modelType = ModelType.YSM;
        }

        // Gecko 动画更新要放在最后
        if (state.mainInfo != null && state.mainInfo.isGeckoModel()) {
            state.modelType = ModelType.GECKO;
            var geckoEntity = getGeckoEntity(entity);
            if (geckoEntity != null) {
                AnimationUpdateManager.createTask(geckoEntity, state);
            }
        }

        if (state.modelType == ModelType.NONE) {
            state.modelType = ModelType.SIMPLE_BEDROCK;
        }
    }

    @Nullable
    public GeckoMaidEntity<? extends Mob> getGeckoEntity(Mob entity) {
        return entity.getData(GeckoMaidEntity.TYPE);
    }

    /**
     * 不能使用事件来初始化 YSM 渲染器
     * <p>
     * 使用事件的话，会受到先后顺序的影响
     */
    private void initYsmModelRenderer(EntityRendererProvider.Context manager) {
        if (!YsmCompat.isInstalled() || YSM_ENTITY_MAID_RENDERER == null) {
            return;
        }
/*
        IGeoEntityRenderer<Mob> geoEntityRenderer = YSM_ENTITY_MAID_RENDERER.apply(manager);
        if (geoEntityRenderer != null) {
            this.ysmMaidRenderer = geoEntityRenderer;
            // 将女仆模组自带的 GeckoLib 模型的 Layer 渲染复制到 YSM 的 Layer 里去
            List<GeoLayerRenderer> layerRenderers = this.geckoEntityMaidRenderer.getLayerRenderers();
            for (GeoLayerRenderer layerRenderer : layerRenderers) {
                this.ysmMaidRenderer.addGeoLayerRenderer(layerRenderer.copy(this.ysmMaidRenderer));
            }
        }
*/
    }

    @Override
    public void submit(EntityMaidRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.modelType == ModelType.NONE) {
            return;
        }

        this.cameraRenderState = camera;

        // 暂定只能女仆显示
        if (state.showBubble) {
            poseStack.pushPose();
            double offsetY = state.bubbleOffset.y() + 0.5f;
            if (state.sitting) {
                offsetY -= 0.25f;
            }
            poseStack.translate(state.bubbleOffset.x, offsetY, state.bubbleOffset.z);
            poseStack.mulPose(camera.orientation);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            // TODO
            // EntityGraphics graphics = new EntityGraphics(poseStack, maidEntity, state.lightCoords, state.partialTick);
            // this.chatBubbleRenderer2.submit(submitNodeCollector, graphics);
            poseStack.popPose();
        }

        // YSM 接管渲染
/*
        if (state.modelType == ModelType.YSM && this.ysmMaidRenderer != null) {
            IGeoEntity geoEntity = this.ysmMaidRenderer.getGeoEntity(entity);
            geoEntity.setYsmModel(maid.getYsmModelId(), maid.getYsmModelTexture());
            if (maidEntity != null) {
                geoEntity.updateRoamingVars(maidEntity.roamingVars);
            }
            PatPatCompat.renderPat(entity, poseStack, partialTicks);
            this.ysmMaidRenderer.geoRender(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        }
*/

        // GeckoLib 接管渲染
        if (state.modelType == ModelType.GECKO) {
            // PatPatCompat.renderPat(state, poseStack, state.partialTick);
            this.geckoEntityMaidRenderer.submit(state, poseStack, submitNodeCollector, camera);
        }

        if (state.modelType == ModelType.SIMPLE_BEDROCK) {
            assert state.bedrockModel != null;
            this.model = state.bedrockModel;
            // 模型动画设置
            this.model.setAnimations(state.mainAnimations);
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    protected void setupRotations(EntityMaidRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);

        // 抱起女仆时的旋转
        if (state.playerVehicle && state.modelType != ModelType.GECKO) {
            poseStack.translate(-0.375, 0.8325, 0.375);
            poseStack.mulPose(Axis.ZN.rotationDegrees(65));
            poseStack.mulPose(Axis.YN.rotationDegrees(-80));
        }

        // 其他时候的旋转
        // HardcodedAnimationManger.setupRotations(mob, poseStack, state.ageInTicks, state.yRot, state.partialTick, state.mainInfo.isGeckoModel());
    }

    @Override
    protected void scale(EntityMaidRenderState state, PoseStack poseStack) {
        var scale = state.mainInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(EntityMaidRenderState state) {
        if (state.mainInfo == null) {
            return DEFAULT_TEXTURE;
        }
        return state.mainInfo.getTexture();
    }

    @Override
    public float getWhiteOverlayProgress(EntityMaidRenderState state) {
        return super.getWhiteOverlayProgress(state);
    }

    public EntityRenderDispatcher getDispatcher() {
        return this.entityRenderDispatcher;
    }

    public CameraRenderState getCameraRenderState() {
        return this.cameraRenderState;
    }

    private void addAdditionMaidLayer(EntityRendererProvider.Context renderManager) {
        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.addAdditionMaidLayer(this, renderManager);
        }
    }
}
