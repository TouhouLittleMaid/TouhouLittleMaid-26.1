package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.SpecialMaidModelResolver;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.simplehats.SimpleHatsCompat;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoUpdateTask;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader.MAID_MODELS;

public class EntityMaidRenderState extends HumanoidRenderState {
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    /**
     * 渲染类型，决定是 Simple Bedrock Model 模型还是 GeckoLib 模型
     */
    public ModelType modelType = ModelType.NONE;
    /**
     * 自定义女仆模型，模型 ID
     */
    public String modelId;
    /**
     * 自定义女仆模型，额外模型相关信息
     */
    public @Nullable MaidModelInfo modelInfo;
    /**
     * Simple Bedrock Model 模型，仅在 SIMPLE_BEDROCK 模式下渲染
     */
    public EntityMaidModel bedrockModel;
    /**
     * 女仆的自定义名称，用于一些命名彩蛋模型的渲染判断
     */
    public @Nullable Component customName;
    /**
     * 自定义女仆模型关联的动画
     */
    public List<IAnimation<EntityMaidRenderState>> animations = Collections.emptyList();
    /**
     * GeckoLib 模型更新数据
     */
    public GeckoUpdateTask<GeckoMaidRenderData> geckoUpdateTask;
    /**
     * 玩家的摄像机数据
     */
    public CameraRenderState camera;
    /**
     * 是否显示聊天气泡
     */
    public boolean showBubble;
    /**
     * 聊天气泡起始位置
     */
    public @Nullable Vec3 bubbleOffset;
    /**
     * 聊天气泡数据
     */
    public @Nullable ChatBubbleDataCollection chatBubble;
    /**
     * 当前是否被玩家抱起（即骑乘玩家）
     */
    public boolean playerVehicle;
    /**
     * 女仆是否处于待命状态
     */
    public boolean sitting;
    /**
     * 当前女仆是否处于睡觉状态
     */
    public boolean sleeping;
    /**
     * 女仆挥动手臂的计数器
     */
    public int swingTime;
    /**
     * 女仆是否正在抬起手臂
     */
    public boolean swingingArms;
    /**
     * 女仆是否处于祈求状态
     */
    public boolean begging;
    /**
     * 女仆当前是否正处于受伤状态
     */
    public boolean hurt;
    /**
     * 女仆是否处于钓鱼状态
     */
    public boolean hasFishingHook;
    /**
     * 女仆是否处于游泳状态
     */
    public boolean isSwimming;
    /**
     * 女仆当前工作模式 ID
     */
    public String taskId;
    /**
     * 护甲值，可能用于一些根据护甲值变化的动画或渲染效果
     */
    public int armorValue;
    /**
     * 当前血量
     */
    public float health;
    /**
     * 最大血量
     */
    public float maxHealth;
    /**
     * 实体随机值，用于一些需要随机效果的动画
     * <p>
     * 默认会取该实体 UUID 的低 64 位，确保同一实体在不同帧的渲染过程中保持一致的随机值
     */
    public long randomNumber;
    /**
     * 女仆配置控制，是否渲染背包
     */
    public boolean showBackpack;
    /**
     * 女仆当前是否穿戴背包
     */
    public boolean hasBackpack;
    /**
     * 背包数据
     * TODO: 可能要换成相关 RenderState
     */
    public @Nullable IMaidBackpack backpack;
    /**
     * 旗帜渲染
     */
    public @Nullable BannerRenderState backBanner;
    /**
     * 装饰栏的方块物品，会渲染在头部，故叫做 headBlock
     */
    public final BlockModelRenderState headBlock = new BlockModelRenderState();
    /**
     * 装饰栏，Simple Hats 兼容
     */
    public final ItemStackRenderState simpleHat = new ItemStackRenderState();
    /**
     * 装饰栏，物品
     */
    public final ItemStackRenderState backItem = new ItemStackRenderState();
    /**
     * 游戏时间，用于一些仅根据时间变化的动画或渲染效果
     */
    public long gameTime;
    /**
     * 维度信息，用于一些根据维度变化的动画或渲染效果
     */
    public @Nullable ResourceKey<Level> dimension;
    /**
     * 当前所处环境是否下雨，用于一些根据天气变化的动画或渲染效果
     */
    public boolean raining;
    /**
     * 当前所处环境是否处于雷暴天气，用于一些根据天气变化的动画或渲染效果
     */
    public boolean thundering;

    public void clear() {
        modelType = ModelType.NONE;

        modelId = null;
        modelInfo = null;
        bedrockModel = null;
        customName = null;
        animations = Collections.emptyList();
        geckoUpdateTask = null;
        camera = null;

        showBubble = false;
        bubbleOffset = null;
        chatBubble = null;

        playerVehicle = false;
        sitting = false;
        sleeping = false;
        swingTime = 0;
        swingingArms = false;
        begging = false;
        hurt = false;
        hasFishingHook = false;
        isSwimming = false;
        taskId = null;
        armorValue = 0;
        health = 0;
        maxHealth = 0;
        randomNumber = 0;

        showBackpack = false;
        hasBackpack = false;
        backpack = null;
        backBanner = null;

        headBlock.clear();
        simpleHat.clear();
        backItem.clear();

        gameTime = 0;
        dimension = null;
        raining = false;
        thundering = false;
    }

    public static void extractRenderState(
            EntityMaid maid,
            EntityMaidRenderState state,
            float partialTicks,
            BlockModelResolver blockModelResolver,
            ItemModelResolver itemModelResolver,
            @Nullable GeckoMaidEntity<? extends EntityMaid> geckoEntity
    ) {
        extractEnvironmentState(maid, state);
        extractAttributeState(maid, state);
        extractBehaviorState(maid, state);
        extractModelState(maid, state);
        extractBackDecorationState(maid, state, blockModelResolver);
        extractChatBubbleState(maid, state, partialTicks);
        extractBackpackState(maid, state, itemModelResolver);

        // Gecko 动画更新要放在最后
        extractGeckoState(state, geckoEntity);
    }

    @SuppressWarnings("all")
    private static void extractEnvironmentState(EntityMaid maid, EntityMaidRenderState state) {
        state.gameTime = maid.level().getGameTime();
        state.dimension = maid.level().dimension();
        state.raining = maid.level().isRaining();
        state.thundering = maid.level().isThundering();
    }

    private static void extractAttributeState(EntityMaid maid, EntityMaidRenderState state) {
        state.customName = maid.getCustomName();
        state.armorValue = maid.getArmorValue();
        state.health = maid.getHealth();
        state.maxHealth = maid.getMaxHealth();
        state.randomNumber = maid.getUUID().getLeastSignificantBits();
    }

    private static void extractBehaviorState(EntityMaid maid, EntityMaidRenderState state) {
        state.playerVehicle = maid.getVehicle() instanceof Player;
        state.sitting = maid.isMaidInSittingPose();
        state.swingTime = maid.swingTime;
        state.sleeping = maid.isSleeping();
        state.begging = maid.isBegging();
        state.swingingArms = maid.isSwingingArms();
        state.hasBackpack = maid.hasBackpack();
        state.hurt = maid.hurtTime > 0;
        state.hasFishingHook = maid.hasFishingHook();
        state.isSwimming = maid.isInWater() && maid.getFluidHeight(FluidTags.WATER) > maid.getFluidJumpThreshold();
        state.taskId = maid.getTask().getUid().getPath();
    }

    private static void extractModelState(EntityMaid maid, EntityMaidRenderState state) {
        state.modelId = maid.getModelId();

        // 直接特殊模型解析（替代 RenderMaidEvent 事件流）
        if (!SpecialMaidModelResolver.resolveSpecialModel(state, MAID_MODELS)) {
            MAID_MODELS.getModel(state.modelId).ifPresent(model -> state.bedrockModel = model);
            MAID_MODELS.getInfo(state.modelId).ifPresent(info -> state.modelInfo = info);
            MAID_MODELS.getAnimation(state.modelId).ifPresent(animations -> state.animations = animations);
        }

        // 默认模型兜底
        if (state.bedrockModel == null) {
            MAID_MODELS.getModel(DEFAULT_MODEL_ID).ifPresent(model -> state.bedrockModel = model);
        }
        if (state.modelInfo == null) {
            MAID_MODELS.getInfo(DEFAULT_MODEL_ID).ifPresent(info -> state.modelInfo = info);
        }
        if (state.animations.isEmpty()) {
            MAID_MODELS.getAnimation(DEFAULT_MODEL_ID).ifPresent(animations -> state.animations = animations);
        }
    }

    private static void extractBackDecorationState(EntityMaid maid, EntityMaidRenderState state, BlockModelResolver blockModelResolver) {
        ItemStack showItem = maid.getBackpackShowItem();

        // 旗帜特殊效果，最优先
        if (showItem.getItem() instanceof BannerItem bannerItem) {
            BannerPatternLayers layers = showItem.get(DataComponents.BANNER_PATTERNS);
            if (layers == null) {
                return;
            }
            BannerRenderState backBanner = new BannerRenderState();
            backBanner.baseColor = bannerItem.getColor();
            backBanner.patterns = layers;
            state.backBanner = backBanner;
            return;
        }

        // 如果装饰栏是方块物品，那么就渲染在头上
        if (showItem.getItem() instanceof BlockItem blockItem) {
            BlockState blockState = blockItem.getBlock().defaultBlockState();

            // TODO 提供一个事件或者接口，方便第三方模组进行方块属性的设置或者替换

            // 如果是作物，那就随机给点 age
            if (blockState.hasProperty(CropBlock.AGE)) {
                int age = (int) (Math.abs(state.randomNumber) % (CropBlock.MAX_AGE + 1));
                blockState = blockState.setValue(CropBlock.AGE, age);
            }

            blockModelResolver.update(state.headBlock, blockState, BLOCK_DISPLAY_CONTEXT);
            return;
        }

        // 如果是装饰栏是 Simple Hats 的兼容物品，渲染在头上
        if (SimpleHatsCompat.isHatItem(showItem)) {
            SimpleHatsCompat.extract(state.simpleHat, showItem);
        }
    }

    private static void extractChatBubbleState(EntityMaid maid, EntityMaidRenderState state, float partialTicks) {
        // 暂定只能女仆显示
        if (state.modelInfo == null || !MaidConfig.GLOBAL_MAID_SHOW_CHAT_BUBBLE.get() || !maid.getConfigManager().isChatBubbleShow()) {
            return;
        }

        Vec3 bubbleOffset = maid.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, maid.getViewYRot(partialTicks));
        if (bubbleOffset == null || !ClientHooks.isNameplateInRenderDistance(maid, state.distanceToCameraSq)) {
            return;
        }

        // 依据模型的缩放大小进行 Y 位移
        float scale = state.modelInfo.getRenderEntityScale();
        if (state.modelInfo.isGeckoModel()) {
            // GeckoLib 模型一般偏高
            bubbleOffset = bubbleOffset.multiply(1, scale + 0.3, 1);
        } else {
            bubbleOffset = bubbleOffset.multiply(1, scale, 1);
        }

        var chatBubble = maid.getChatBubbleManager().getChatBubbleDataCollection();
        if (chatBubble == null || chatBubble.isEmpty()) {
            return;
        }

        state.showBubble = true;
        state.bubbleOffset = bubbleOffset;
        state.chatBubble = chatBubble;
    }

    private static void extractBackpackState(EntityMaid maid, EntityMaidRenderState state, ItemModelResolver itemModelResolver) {
        if (maid.isSleeping() || maid.isInvisible()) {
            return;
        }

        state.showBackpack = maid.getConfigManager().isShowBackpack();
        state.backpack = state.showBackpack ? maid.getMaidBackpackType() : BackpackManager.getEmptyBackpack();

        ItemStack showItem = maid.getBackpackShowItem();
        // 只有工具类物品才会显示在背部
        if (showItem.has(DataComponents.TOOL)) {
            itemModelResolver.updateForLiving(state.backItem, showItem, ItemDisplayContext.FIXED, maid);
        }
    }

    @SuppressWarnings("unchecked")
    private static void extractGeckoState(EntityMaidRenderState state, @Nullable GeckoMaidEntity<? extends EntityMaid> geckoEntity) {
        if (geckoEntity != null) {
            if (state.modelInfo != null && state.modelInfo.isGeckoModel()) {
                state.modelType = ModelType.GECKO;
                geckoEntity.setMaidInfo(state.modelInfo);
                state.geckoUpdateTask = (GeckoUpdateTask<GeckoMaidRenderData>) geckoEntity.createUpdateTask(state);
            } else {
                geckoEntity.reset();
            }
        }

        if (state.modelType == ModelType.NONE) {
            state.modelType = ModelType.SIMPLE_BEDROCK;
        }
    }
}
