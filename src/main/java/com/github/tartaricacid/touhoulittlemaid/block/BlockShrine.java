package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityShrine;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockShrine extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 5, 16),
            Block.box(2, 5, 2, 14, 10, 14),
            Block.box(4, 10, 4, 12, 16, 12));
    private static final MapCodec<BlockShrine> CODEC = simpleCodec(BlockShrine::new);

    public BlockShrine(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0F, 3.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    public BlockShrine(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos,
                                       Player playerIn, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && worldIn.getBlockEntity(pos) instanceof TileEntityShrine shrine) {
            if (playerIn.isShiftKeyDown()) {
                if (!shrine.isEmpty()) {
                    ItemStack storageItem = shrine.extractStorageItem();
                    playerIn.getInventory().placeItemBackInInventory(storageItem);
                    worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1, 1);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            if (shrine.isEmpty()) {
                if (shrine.canInsert(playerIn.getMainHandItem())) {
                    shrine.insertStorageItem(playerIn.getMainHandItem().copyWithCount(1));
                    playerIn.getMainHandItem().shrink(1);
                    worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1, 1);
                    return InteractionResult.SUCCESS;
                }
                if (!worldIn.isClientSide()) {
                    playerIn.sendSystemMessage(Component.translatable("message.touhou_little_maid.shrine.not_film"));
                }
                return InteractionResult.PASS;
            }
            if (playerIn.getMainHandItem().isEmpty()) {
                // 创造模式玩家可以随意复活
                if (!playerIn.isCreative()) {
                    if (playerIn.getHealth() < (playerIn.getMaxHealth() / 2) + 1) {
                        if (!worldIn.isClientSide()) {
                            playerIn.sendSystemMessage(Component.translatable("message.touhou_little_maid.shrine.health_low"));
                        }
                        return InteractionResult.FAIL;
                    }
                    playerIn.setHealth(0.25f);
                }
                ItemStack film = shrine.getStorageItem();
                ItemFilm.filmToMaid(film, worldIn, pos.above(), playerIn);
                if (playerIn instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.SHRINE_REBORN_MAID);
                }
            }
        }
        return super.useItemOn(itemStack, state, worldIn, pos, playerIn, hand, hit);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = Lists.newArrayList(super.getDrops(state, params));
        BlockEntity parameter = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof TileEntityShrine shrine) {
            ItemStack storageItem = shrine.extractStorageItem();
            drops.add(storageItem);
        }
        return drops;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new TileEntityShrine(pos, blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
