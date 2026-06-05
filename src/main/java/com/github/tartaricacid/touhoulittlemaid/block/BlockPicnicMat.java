package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.block.properties.PicnicMatPart;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemPicnicBasket;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityPicnicMat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockPicnicMat extends Block implements EntityBlock {
    public static final EnumProperty<PicnicMatPart> PART = EnumProperty.create("part", PicnicMatPart.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape AABB = Block.box(0, 0, 0, 16, 1, 16);

    public BlockPicnicMat(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0F, 3.0F)
                .forceSolidOn()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, PicnicMatPart.CENTER));
    }

    public BlockPicnicMat(Properties properties) {
        super(properties);
    }

    public void startMaidSit(EntityMaid maid, BlockState state, Level worldIn, BlockPos pos) {
        if (!(worldIn instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(worldIn.getBlockEntity(pos) instanceof BlockEntityPicnicMat picnicMat)) {
            return;
        }
        // 只能选中中心方块
        if (!state.getValue(PART).isCenter()) {
            return;
        }

        // 遍历，寻找是否有空位
        boolean hasEmptySit = false;
        int sitIndex = -1;
        for (UUID uuid : picnicMat.getSitIds()) {
            sitIndex++;
            if (uuid.equals(Util.NIL_UUID)) {
                hasEmptySit = true;
                break;
            }
            Entity oldSitEntity = serverLevel.getEntity(uuid);
            if (oldSitEntity == null || !oldSitEntity.isAlive()) {
                hasEmptySit = true;
                break;
            }
        }

        if (hasEmptySit) {
            Vec3 sitPos = this.sitPosition(sitIndex);
            Vec3 corner = Vec3.atLowerCornerWithOffset(pos, sitPos.x, sitPos.y + 0.0625, sitPos.z);
            EntitySit newSitEntity = new EntitySit(worldIn, corner, Type.ON_HOME_MEAL.getTypeName(), pos);

            double y = sitPos.z < 0 ? -1 : 1;
            double x = sitPos.x < 0 ? -1 : 1;
            float rotOffset = (float) Math.toDegrees(Math.atan2(y, x));

            newSitEntity.setYRot(rotOffset + 90);
            worldIn.addFreshEntity(newSitEntity);
            picnicMat.setSitId(sitIndex, newSitEntity.getUUID());
            maid.startRiding(newSitEntity);
        }
    }

    private Vec3 sitPosition(int sitIndex) {
        return switch (sitIndex) {
            case 0 -> new Vec3(2, 0, 2);
            case 1 -> new Vec3(-1, 0, 2);
            case 2 -> new Vec3(-1, 0, -1);
            default -> new Vec3(2, 0, -1);
        };
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos,
                                       Player playerIn, InteractionHand hand, BlockHitResult hit) {
        if (worldIn.isClientSide()) {
            return InteractionResult.PASS;
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (!(worldIn.getBlockEntity(pos) instanceof BlockEntityPicnicMat picnicMat)) {
            return InteractionResult.FAIL;
        }
        BlockPos centerPos = picnicMat.getCenterPos();
        if (!(worldIn.getBlockEntity(centerPos) instanceof BlockEntityPicnicMat picnicMatCenter)) {
            return InteractionResult.FAIL;
        }
        if (itemStack.get(DataComponents.FOOD) != null) {
            return placeFood(itemStack, picnicMatCenter);
        }
        if (itemStack.isEmpty() && playerIn.isDiscrete()) {
            return takeFood(playerIn, picnicMatCenter);
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult placeFood(ItemStack food, BlockEntityPicnicMat picnicMatCenter) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemStacksResourceHandler handler = picnicMatCenter.getHandler();
            int count = food.getCount();
            int shrinkCount = handler.insert(ItemResource.of(food), count, tx);
            if (shrinkCount <= 0) {
                return InteractionResult.FAIL;
            }

            food.shrink(shrinkCount);

            tx.commit();
            picnicMatCenter.refresh();
            return InteractionResult.SUCCESS_SERVER;
        }
    }

    private static InteractionResult takeFood(Player playerIn, BlockEntityPicnicMat picnicMatCenter) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemStacksResourceHandler handler = picnicMatCenter.getHandler();
            int size = handler.size() - 1;
            for (int i = size; i >= 0; i--) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty()) {
                    int extractCount = handler.extract(i, resource, handler.getAmountAsInt(i), tx);
                    if (extractCount <= 0) {
                        return InteractionResult.FAIL;
                    }

                    ItemStack extract = resource.toStack(extractCount);
                    playerIn.getInventory().placeItemBackInInventory(extract);

                    tx.commit();
                    picnicMatCenter.refresh();
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
            return InteractionResult.FAIL;
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        handlePicnicMatRemove(world, pos, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion) {
        handlePicnicMatRemove(world, pos, null);
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos centerPos = context.getClickedPos();
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos searchPos = centerPos.offset(i, 0, j);
                if (!context.getLevel().getBlockState(searchPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }
        Direction opposite = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, opposite);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide()) {
            return;
        }

        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                // 正中心的不用放置
                if (!searchPos.equals(pos)) {
                    worldIn.setBlock(searchPos, state.setValue(PART, PicnicMatPart.SIDE), Block.UPDATE_ALL);
                }
                BlockEntity blockEntity = worldIn.getBlockEntity(searchPos);
                if (blockEntity instanceof BlockEntityPicnicMat picnicMat) {
                    picnicMat.setCenterPos(pos);
                }
            }
        }

        // 给中心方块存入物品
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof BlockEntityPicnicMat picnicMat && stack.is(InitItems.PICNIC_BASKET.get())) {
            picnicMat.setHandler(ItemPicnicBasket.getContainer(stack));
        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos below = blockPos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityPicnicMat(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private static void handlePicnicMatRemove(Level world, BlockPos pos, @Nullable Player player) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof BlockEntityPicnicMat picnicMat)) {
            return;
        }

        BlockPos centerPos = picnicMat.getCenterPos();
        if (world.getBlockEntity(centerPos) instanceof BlockEntityPicnicMat picnicMatCenter) {
            ItemStack stack = InitItems.PICNIC_BASKET.get().getDefaultInstance();
            ItemPicnicBasket.setContainer(stack, picnicMatCenter.getHandler());

            if (player == null || !player.isCreative()) {
                popResource(world, centerPos, stack);
            }

            for (UUID uuid : picnicMatCenter.getSitIds()) {
                if (uuid.equals(Util.NIL_UUID)) {
                    continue;
                }
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof EntitySit) {
                    entity.discard();
                }
            }
        }

        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos offset = centerPos.offset(i, 0, j);
                if (world.getBlockState(offset).is(InitBlocks.PICNIC_MAT.get())) {
                    world.setBlockAndUpdate(offset, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
