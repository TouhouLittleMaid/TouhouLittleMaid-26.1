package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityShrine;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 5, 16),
            Block.box(2, 5, 2, 14, 16, 14),
            Block.box(0, 16, 0, 16, 22, 16)
    );

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
        if (hand != InteractionHand.MAIN_HAND) {
            return super.useItemOn(itemStack, state, worldIn, pos, playerIn, hand, hit);
        }
        if (!(worldIn.getBlockEntity(pos) instanceof BlockEntityShrine shrine)) {
            return super.useItemOn(itemStack, state, worldIn, pos, playerIn, hand, hit);
        }

        if (playerIn.isShiftKeyDown()) {
            if (!shrine.isEmpty()) {
                ItemStack storageItem = shrine.extractStorageItem();
                playerIn.getInventory().placeItemBackInInventory(storageItem);
                worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                        SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }

        if (shrine.isEmpty()) {
            if (shrine.canInsert(itemStack)) {
                ItemStack split = itemStack.split(1);
                shrine.insertStorageItem(split);
                worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM,
                        SoundSource.PLAYERS, 1, 1);
                return InteractionResult.SUCCESS;
            }
            if (!worldIn.isClientSide()) {
                MutableComponent component = Component.translatable("message.touhou_little_maid.shrine.not_film");
                playerIn.sendSystemMessage(component);
            }
            return InteractionResult.SUCCESS;
        }

        if (itemStack.isEmpty()) {
            // 创造模式玩家可以随意复活
            if (!playerIn.isCreative()) {
                if (playerIn.getHealth() < (playerIn.getMaxHealth() / 2) + 1) {
                    if (!worldIn.isClientSide()) {
                        MutableComponent component = Component.translatable("message.touhou_little_maid.shrine.health_low");
                        playerIn.sendSystemMessage(component);
                    }
                    return InteractionResult.SUCCESS;
                }
                playerIn.setHealth(0.25f);
            }

            ItemStack film = shrine.extractStorageItem();
            ItemFilm.filmToMaid(film, worldIn, pos.above(), playerIn);
            if (playerIn instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.get().trigger(serverPlayer, TriggerType.SHRINE_REBORN_MAID);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = Lists.newArrayList(super.getDrops(state, params));
        BlockEntity parameter = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof BlockEntityShrine shrine) {
            ItemStack storageItem = shrine.extractStorageItem();
            drops.add(storageItem);
        }
        return drops;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction opposite = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, opposite);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new BlockEntityShrine(pos, blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn,
                               BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
