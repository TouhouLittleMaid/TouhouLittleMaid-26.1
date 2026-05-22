package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityMaidRenderState extends ArmedEntityRenderState {
    public ModelType modelType = ModelType.NONE;

    public MaidModelInfo mainInfo;
    @Nullable
    public BedrockModel<EntityMaidRenderState> bedrockModel;
    public List<Object> mainAnimations = ObjectLists.emptyList();

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

    public Mob entity;  // TODO

    public void clear() {
        modelType = ModelType.NONE;

        mainInfo = null;
        bedrockModel = null;
        mainAnimations = ObjectLists.emptyList();

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

        entity = null;
    }
}
