package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.ItemModelSwitcher;
import com.github.tartaricacid.touhoulittlemaid.network.message.OpenSwitcherGuiPackage;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityModelSwitcher;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockModelSwitcher extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final MapCodec<BlockModelSwitcher> CODEC = simpleCodec(BlockModelSwitcher::new);

    public BlockModelSwitcher(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.STONE)
                .strength(50.0F, 1200.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    public BlockModelSwitcher(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        Direction value = state.getValue(FACING);
        if (direction != null) {
            return direction == value.getClockWise() || direction == value.getCounterClockWise();
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileEntityModelSwitcher(pPos, pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        if (level.isClientSide()) {
            return;
        }
        Direction direction = state.getValue(FACING);
        Direction counterClockWise = direction.getCounterClockWise();
        Direction clockWise = direction.getClockWise();
        boolean leftSignal = level.getSignal(pos.offset(counterClockWise.getUnitVec3i()), counterClockWise) > 0;
        boolean rightSignal = level.getSignal(pos.offset(clockWise.getUnitVec3i()), clockWise) > 0;
        boolean hasSignal = leftSignal || rightSignal;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityModelSwitcher switcher && level instanceof ServerLevel serverLevel) {
            if (switcher.isPowered() != hasSignal) {
                switcher.setPowered(!switcher.isPowered());
                if (!switcher.isPowered()) {
                    return;
                }
                UUID uuid = switcher.getUuid();
                if (uuid == null) {
                    return;
                }
                int index = calculateIndex(leftSignal, switcher.getInfoList().size(), switcher.getIndex());
                switcher.setIndex(index);
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof EntityMaid && entity.isAlive()) {
                    this.setMaidData(switcher, (EntityMaid) entity);
                }
            }
        }
    }

    private void setMaidData(TileEntityModelSwitcher switcher, EntityMaid maid) {
        TileEntityModelSwitcher.ModeInfo modelInfo = switcher.getModelInfo();
        if (modelInfo != null) {
            maid.components().profile.setModelId(modelInfo.getModelId().toString());
            if (StringUtils.isNotBlank(modelInfo.getText())) {
                maid.setCustomName(Component.literal(modelInfo.getText()));
                maid.setCustomNameVisible(true);
            } else {
                maid.setCustomName(null);
                maid.setCustomNameVisible(false);
            }
            BlockPos blockPos = maid.blockPosition();
            maid.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            maid.setYRot(modelInfo.getDirection().toYRot());
            maid.setYHeadRot(modelInfo.getDirection().toYRot());
            maid.setYBodyRot(modelInfo.getDirection().toYRot());
        }
    }

    private int calculateIndex(boolean leftSignal, int size, int index) {
        if (leftSignal) {
            if (index < size - 1) {
                index++;
            } else {
                index = 0;
            }
        } else {
            if (index > 0) {
                index--;
            } else {
                index = size - 1;
            }
        }
        return index;
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos,
                                       Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof TileEntityModelSwitcher) {
            if (!worldIn.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new OpenSwitcherGuiPackage(pos));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        BlockEntity te = pLevel.getBlockEntity(pPos);
        if (te instanceof TileEntityModelSwitcher tileEntityModelSwitcher) {
            ItemModelSwitcher.itemStackToTileEntity(pLevel.registryAccess(), pStack, tileEntityModelSwitcher);
            tileEntityModelSwitcher.refresh();
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
}
