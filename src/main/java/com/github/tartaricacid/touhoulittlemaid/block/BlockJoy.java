package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityJoy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class BlockJoy extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected BlockJoy(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockJoy(Identifier id) {
        this(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0F, 3.0F)
                .forceSolidOn()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    protected abstract Vec3 sitPosition();

    protected abstract String getTypeName();

    protected abstract int sitYRot();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!(level instanceof ServerLevel serverLevel)) {
            // 客户端必须返回 SUCCESS，否则会有额外的异常提醒
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BlockEntityJoy joy)) {
            return InteractionResult.PASS;
        }
        Entity oldSitEntity = serverLevel.getEntity(joy.getSitId());
        if (oldSitEntity != null && oldSitEntity.isAlive()) {
            return InteractionResult.PASS;
        }

        Vec3 sitPos = this.sitPosition();
        Vec3 corner = Vec3.atLowerCornerWithOffset(pos, sitPos.x, sitPos.y, sitPos.z);

        EntitySit newSitEntity = new EntitySit(level, corner, this.getTypeName(), pos);
        newSitEntity.setYRot(state.getValue(FACING).getOpposite().toYRot() + this.sitYRot());
        level.addFreshEntity(newSitEntity);

        joy.setSitId(newSitEntity.getUUID());
        joy.setChanged();

        player.startRiding(newSitEntity);
        return InteractionResult.SUCCESS_SERVER;
    }

    public void startMaidSit(EntityMaid maid, BlockState state, Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof BlockEntityJoy joy)) {
            return;
        }
        Entity oldSitEntity = serverLevel.getEntity(joy.getSitId());
        if (oldSitEntity != null && oldSitEntity.isAlive()) {
            return;
        }

        Vec3 sitPos = this.sitPosition();
        Vec3 corner = Vec3.atLowerCornerWithOffset(pos, sitPos.x, sitPos.y, sitPos.z);

        EntitySit newSitEntity = new EntitySit(level, corner, this.getTypeName(), pos);
        newSitEntity.setYRot(state.getValue(FACING).getOpposite().toYRot() + this.sitYRot());
        level.addFreshEntity(newSitEntity);

        joy.setSitId(newSitEntity.getUUID());
        joy.setChanged();

        maid.startRiding(newSitEntity);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction opposite = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, opposite);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
