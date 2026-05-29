package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTamedEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTypeNameEvent;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.curios.CuriosCompat;
import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.TabIndex;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponents;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook.InteractHook;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.hook.SpawnHook;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.BaseTickComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.BaubleContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.config.MaidConfigContainer;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.MAID_NUM;

/**
 * 无法归类的部分功能，全部放入此 Manager 里
 */
@MaidComponentDef("misc")
public class MaidMiscComponent implements MaidComponent, BaseTickComponent, InteractHook, SpawnHook {
    protected MaidComponents host;
    private final EntityMaid maid;

    public MaidMiscComponent(EntityMaid maid) {
        this.maid = maid;
    }

    @Override
    public void init(MaidComponents host) {
        this.host = host;
    }

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public InteractionResult mobInteract(Player playerIn, InteractionHand hand) {
        // 禁止 fake player 交互女仆
        if (playerIn instanceof FakePlayer) {
            return InteractionResult.PASS;
        }
        if (hand == InteractionHand.MAIN_HAND && maid.isOwnedBy(playerIn)) {
            ItemStack stack = playerIn.getMainHandItem();
            InteractMaidEvent event = new InteractMaidEvent(playerIn, maid, stack);
            // 利用短路原理，逐个触发对应的交互事件
            if (NeoForge.EVENT_BUS.post(event).isCanceled()
                || stack.interactLivingEntity(playerIn, maid, hand).consumesAction()
                || maid.components().misc.openMaidGui(playerIn)) {
                return InteractionResult.SUCCESS;
            }
        } else {
            return tameMaid(playerIn.getItemInHand(hand), playerIn);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult tameMaid(ItemStack stack, Player player) {
        MaidNumAttachment cap = player.getData(MAID_NUM);
        if (cap.canAdd() || player.isCreative()) {
            boolean isNormal = !maid.isTame() && stack.is(TagItem.MAID_TAMED_ITEM);
            boolean isNtr = stack.is(InitItems.OWNER_CONVERSION_TOOL.get());
            if (isNormal || isNtr) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    cap.add();
                }
                maid.tame(player);
                // 清掉寻路，清掉敌对记忆
                maid.getRawNavigation().stop();
                maid.setTarget(null);
                maid.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                maid.level.broadcastEntityEvent(maid, EntityEvent.TAMING_SUCCEEDED);
                maid.playSound(InitSounds.MAID_TAMED.get(), 1, 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.TAMED_MAID);
                    if (maid.components().stats.isStructureSpawn()) {
                        InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.TAMED_MAID_FROM_STRUCTURE);
                    }
                }
                // 触发事件
                NeoForge.EVENT_BUS.post(new MaidTamedEvent(maid, player, isNtr));
                return InteractionResult.SUCCESS;
            }
        } else {
            if (player instanceof ServerPlayer) {
                MutableComponent msg = Component.translatable("message.touhou_little_maid.owner_maid_num.can_not_add",
                        cap.get(), cap.getMaxNum());
                player.sendSystemMessage(msg);
            }
        }
        return InteractionResult.PASS;
    }

    public boolean openMaidGui(Player player) {
        return openMaidGui(player, TabIndex.MAIN);
    }

    public boolean openMaidGui(Player player, int tabIndex) {
        if (player instanceof ServerPlayer serverPlayer && !maid.isSleeping()) {
            maid.getRawNavigation().stop();
            final int id = maid.getId();
            MenuProvider guiProvider = getGuiProvider(tabIndex);
            serverPlayer.openMenu(guiProvider, buffer -> buffer.writeInt(id));
        }
        return true;
    }

    private MenuProvider getGuiProvider(int tabIndex) {
        return switch (tabIndex) {
            case TabIndex.TASK_CONFIG -> maid.components().task.getTask().getTaskConfigGuiProvider(maid);
            case TabIndex.MAID_CONFIG -> MaidConfigContainer.create(maid.getId());
            case TabIndex.BAUBLE -> BaubleContainer.create(maid);
            case TabIndex.CURIOS -> CuriosCompat.create(maid);
            default -> maid.components().backpack.getMaidBackpackType().getGuiProvider(maid.getId());
        };
    }

    @Override
    public void baseTick() {
        this.randomRestoreHealth();
        this.onMaidSleep();
    }

    private void randomRestoreHealth() {
        if (maid.getHealth() < maid.getMaxHealth() && maid.getRandom().nextFloat() < 0.0025) {
            maid.heal(1);
            maid.components().particle.spawnRestoreHealthParticle(maid.getRandom().nextInt(3) + 7);
        }
    }

    private void onMaidSleep() {
        if (maid.isSleeping()) {
            maid.getSleepingPos().ifPresent(pos -> maid.setPos(pos.getX() + 0.5, pos.getY() + 0.5625, pos.getZ() + 0.5));
            maid.setDeltaMovement(Vec3.ZERO);
            if (!maid.isSilent()) {
                maid.setSilent(true);
            }
        } else {
            if (maid.isSilent()) {
                maid.setSilent(false);
            }
        }
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        if (!maid.components().stats.isStruckByLightning()) {
            double beforeMaxHealth = maid.getAttributeBaseValue(Attributes.MAX_HEALTH);
            Objects.requireNonNull(maid.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(beforeMaxHealth + 20);
            maid.components().stats.setStruckByLightning(true);
            if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.LIGHTNING_BOLT);
                if (maid.getMaxHealth() >= 100) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.MAID_100_HEALTHY);
                }
            }
        }
    }

    /**
     * 女仆在危险情况（比如附近有苦力怕）下是否应该停止骑乘或待命状态
     */
    public boolean shouldLeaveMountOrSitForDanger() {
        // 如果女仆和玩家骑乘同一个扫帚
        Entity vehicle = maid.getVehicle();
        if (vehicle != null && vehicle.getControllingPassenger() instanceof Player) {
            return false;
        }
        return true;
    }

    @Override
    public Component getTypeName() {
        // 优先事件系统
        MaidTypeNameEvent typeNameEvent = new MaidTypeNameEvent(maid);
        NeoForge.EVENT_BUS.post(typeNameEvent);
        if (typeNameEvent.getTypeName() != null) {
            return typeNameEvent.getTypeName();
        }
        // 然后才是默认模型名
        String modelId = maid.components().profile.getModelId();
        MutableComponent rawTypeName = Component.literal(maid.getType().getDescriptionId());
        Optional<MaidModelInfo> info = ServerCustomPackLoader.SERVER_MAID_MODELS.getInfo(modelId);
        return info.map(maidModelInfo -> ParseI18n.parse(maidModelInfo.getName()))
                .orElse(rawTypeName);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        // 为结构生成的女仆添加特殊标签
        if (reason == EntitySpawnReason.STRUCTURE) {
            host.stats.structureSpawn = true;
        }
        int modelSize = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelSize();
        // 这里居然可能为 0
        if (modelSize > 0) {
            int skipRandom = maid.getRandom().nextInt(modelSize);
            Optional<String> modelId = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelIdSet().stream().skip(skipRandom).findFirst();
            return modelId.map(id -> {
                maid.components().profile.setModelId(id);
                return spawnDataIn;
            }).orElse(spawnDataIn);
        }
        return spawnDataIn;
    }

    @Nullable
    public Vec3 getLegacyLeashOffset(String modelId) {
        var modelOptional = CustomPackLoader.MAID_MODELS.getModel(modelId);
        Optional<MaidModelInfo> infoOptional = CustomPackLoader.MAID_MODELS.getInfo(modelId);
        if (modelOptional.isPresent() && infoOptional.isPresent()) {
            var model = modelOptional.get();
            float renderEntityScale = infoOptional.get().getRenderEntityScale();

            BedrockPart arm = null;
            HumanoidArm armSide = HumanoidArm.RIGHT;
            if (model.hasRightArm()) {
                arm = model.getRightArm();
            } else if (model.hasLeftArm()) {
                arm = model.getLeftArm();
                armSide = HumanoidArm.LEFT;
            }

            if (arm != null) {
                BedrockPart positioningModel = model.getArmPositioningModel(armSide);
                Vector3f positionVec;
                // TODO: getTranslateAndRotateVector3f() 在 BedrockPart 中已被移除
                // 需要使用新的变换获取方式（可能是 x/y/z 字段 + rotationDegreesX/Y/Z 组合）
                if (positioningModel != null) {
                    // positionVec = positioningModel.getTranslateAndRotateVector3f();
                    positionVec = new Vector3f(0, 0.5f, 0);  // 临时默认值
                } else {
                    positionVec = new Vector3f(0, 0.5f, 0);
                }
                // Vector3f armVec = arm.getTranslateAndRotateVector3f();
                Vector3f armVec = new Vector3f(0, 10, 0);  // 临时默认值
                Vector3f pose = armVec.add(positionVec);
                return new Vec3(pose.x() * renderEntityScale, (1.5 - pose.y) * renderEntityScale, pose.z() * renderEntityScale);
            }

            if (model.hasHead()) {
                BedrockPart head = model.getHead();
                return new Vec3(head.x * renderEntityScale, (1.5 - head.y / 16) * renderEntityScale, head.z * renderEntityScale);
            }
        }
        return null;
    }
}
