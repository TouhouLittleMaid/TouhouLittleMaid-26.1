package com.github.tartaricacid.touhoulittlemaid.blockentity;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public abstract class BlockEntityJoy extends BlockEntityBase {
    private static final String SIT_ID = "SitId";
    private UUID sitId = Util.NIL_UUID;

    public BlockEntityJoy(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(SIT_ID, UUIDUtil.CODEC, sitId);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.sitId = input.read(SIT_ID, UUIDUtil.CODEC).orElse(Util.NIL_UUID);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.getSitId());
            if (entity instanceof EntitySit) {
                entity.discard();
            }
        }
    }

    public UUID getSitId() {
        return this.sitId;
    }

    public void setSitId(UUID sitId) {
        this.sitId = sitId;
    }
}
