package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoMaidRenderData;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoUpdateTask;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class EntityMaidRenderState extends ArmedEntityRenderState {
    public ModelType modelType = ModelType.NONE;

    @Nullable
    public Component customName;
    public String modelId;

    public MaidModelInfo mainInfo;
    public EntityMaidModel bedrockModel;
    public List<IAnimation<EntityMaidRenderState>> mainAnimations = Collections.emptyList();

    public boolean playerVehicle;
    public boolean sitting;

    public boolean showBubble;
    public Vec3 bubbleOffset;

    public boolean showBackpack;
    @Nullable
    public IMaidBackpack backpack;  // 可能要换成相关 RenderState
    @Nullable
    public BannerRenderState backBanner;
    @Nullable
    public SkullBlockRenderState headSkull;

    public final BlockModelRenderState headBlock = new BlockModelRenderState();
    public final ItemStackRenderState simpleHat = new ItemStackRenderState();
    public final ItemStackRenderState backItem = new ItemStackRenderState();

    public long gameTime;
    @Nullable
    public ResourceKey<Level> dimension;
    public boolean raining;
    public boolean thundering;
    public long uuidLeastSignificantBits;

    public float attackAnim;
    public int swingTime;
    public boolean sleeping;
    public boolean passenger;
    public boolean usingItem;
    @Nullable
    public InteractionHand usedItemHand;
    @Nullable
    public InteractionHand swingingArm;
    public boolean hasMainHandItem;
    public int armorValue;
    public float health;
    public float maxHealth;

    public boolean begging;
    public boolean swingingArms;
    public boolean maidInSittingPose;
    public boolean hasHelmet;
    public boolean hasChestPlate;
    public boolean hasLeggings;
    public boolean hasBoots;
    public boolean hasBackpack;
    public boolean hurt;
    public boolean hasFishingHook;
    public boolean onClimbable;
    @Nullable
    public String taskId;

    public GeckoUpdateTask<GeckoMaidRenderData> geckoUpdateTask;

    public void clear() {
        modelType = ModelType.NONE;

        customName = null;
        mainInfo = null;
        bedrockModel = null;
        mainAnimations = Collections.emptyList();

        playerVehicle = false;
        sitting = false;

        showBubble = false;
        bubbleOffset = null;

        showBackpack = false;
        backpack = null;
        backBanner = null;
        headSkull = null;

        headBlock.clear();
        simpleHat.clear();
        backItem.clear();

        gameTime = 0;
        dimension = null;
        raining = false;
        thundering = false;
        uuidLeastSignificantBits = 0;

        attackAnim = 0;
        swingTime = 0;
        sleeping = false;
        passenger = false;
        usingItem = false;
        usedItemHand = null;
        swingingArm = null;
        hasMainHandItem = false;
        armorValue = 0;
        health = 0;
        maxHealth = 0;

        begging = false;
        swingingArms = false;
        maidInSittingPose = false;
        hasHelmet = false;
        hasChestPlate = false;
        hasLeggings = false;
        hasBoots = false;
        hasBackpack = false;
        hurt = false;
        hasFishingHook = false;
        onClimbable = false;
        taskId = null;

        geckoUpdateTask = null;
    }
}
