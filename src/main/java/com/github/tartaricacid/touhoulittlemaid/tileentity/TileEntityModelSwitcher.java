package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.item.ItemModelSwitcher;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TileEntityModelSwitcher extends BlockEntity {
    public static final String INFO_LIST = "info_list";
    public static final String ENTITY_UUID = "entity_uuid";
    public static final String LIST_INDEX = "list_index";

    private List<ModeInfo> infoList = Lists.newArrayList();
    private boolean isPowered;
    private @Nullable UUID uuid;
    private int index;

    public TileEntityModelSwitcher(BlockPos pWorldPosition, BlockState pBlockState) {
        super(InitBlocks.MODEL_SWITCHER_TE.get(), pWorldPosition, pBlockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.store(INFO_LIST, ModeInfo.CODEC.listOf(), infoList);
        if (this.uuid != null) {
            output.store(ENTITY_UUID, UUIDUtil.CODEC, this.uuid);
        }
        output.putInt(LIST_INDEX, this.index);
        super.saveAdditional(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read(INFO_LIST, ModeInfo.CODEC.listOf()).ifPresent(c -> {
            infoList.clear();
            infoList.addAll(c);
        });
        this.uuid = input.read(ENTITY_UUID, UUIDUtil.CODEC).orElse(null);
        this.index = input.getIntOr(LIST_INDEX, 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level instanceof ServerLevel serverLevel) {
            ItemStack itemStack = ItemModelSwitcher.tileEntityToItemStack(serverLevel.registryAccess(), this);
            Block.popResource(serverLevel, pos, itemStack);
        }
    }

    @Nullable
    public UUID getUuid() {
        return uuid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        this.refresh();
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }

    @Nullable
    public ModeInfo getModelInfo() {
        if (0 <= index && index < infoList.size()) {
            return infoList.get(this.index);
        }
        return null;
    }

    public List<ModeInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<ModeInfo> infoList) {
        this.infoList = infoList;
        this.refresh();
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public static final class ModeInfo {
        public static final Codec<ModeInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(Identifier::parse, Identifier::toString).fieldOf("model_id").forGetter(ModeInfo::getModelId),
                Codec.STRING.fieldOf("text").forGetter(ModeInfo::getText),
                Direction.CODEC.fieldOf("direction").forGetter(ModeInfo::getDirection)
        ).apply(instance, ModeInfo::new));

        public static final StreamCodec<ByteBuf, ModeInfo> MODE_INFO_STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC,
                ModeInfo::getModelId,
                ByteBufCodecs.STRING_UTF8,
                ModeInfo::getText,
                Direction.STREAM_CODEC,
                ModeInfo::getDirection,
                ModeInfo::new
        );

        private Identifier modelId;
        private String text;
        private Direction direction;

        public ModeInfo(Identifier modelId, String text, Direction direction) {
            this.modelId = modelId;
            this.text = text;
            this.direction = direction;
        }

        public Identifier getModelId() {
            return modelId;
        }

        public void setModelId(Identifier modelId) {
            this.modelId = modelId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }
}
