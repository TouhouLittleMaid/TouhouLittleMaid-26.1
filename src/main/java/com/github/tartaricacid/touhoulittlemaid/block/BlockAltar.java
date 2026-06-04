package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.github.tartaricacid.touhoulittlemaid.util.PosListData;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble.RANDOM;
import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.sounds.SoundSource.BLOCKS;
import static net.minecraft.sounds.SoundSource.PLAYERS;


public class BlockAltar extends Block implements EntityBlock {
    public BlockAltar(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.STONE)
                .strength(2, 2)
                .noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAltar(pos, state);
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos,
                                       Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND) {
            return super.useItemOn(itemStack, state, worldIn, pos, player, handIn, hit);
        }
        var optional = this.getAltar(worldIn, pos);
        if (optional.isEmpty()) {
            return super.useItemOn(itemStack, state, worldIn, pos, player, handIn, hit);
        }
        TileEntityAltar altar = optional.get();
        if (player.isShiftKeyDown() || player.getMainHandItem().isEmpty()) {
            takeOutItem(worldIn, altar, player);
        } else {
            takeInOrCraft(worldIn, altar, player);
        }
        altar.refresh();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onBlockExploded(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion) {
        this.getAltar(world, pos).ifPresent(altar -> {
            PosListData posList = altar.getBlockPosList();
            this.restoreStorageBlock(world, pos, posList);
        });
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (worldIn.isClientSide()) {
            return super.playerWillDestroy(worldIn, pos, state, player);
        }
        this.getAltar(worldIn, pos).ifPresent(altar -> {
            this.restoreStorageBlock(worldIn, pos, altar.getBlockPosList());
            if (!player.isCreative()) {
                Block block = altar.getStorageState().getBlock();
                Block.popResource(worldIn, pos, new ItemStack(block));
            }
        });
        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return this.getAltar(level, pos)
                .map(altar -> new ItemStack(altar.getStorageState().getBlock()))
                .orElse(super.getCloneItemStack(level, pos, state, includeData, player));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        return this.getAltar(world, pos)
                .map(altar -> {
                    BlockState storageState = altar.getStorageState();
                    // 虽然不太可能，但是还是判断一下避免循环调用
                    if (storageState.getBlock() instanceof BlockAltar) {
                        return SoundType.STONE;
                    }
                    return storageState.getSoundType(world, pos, entity);
                }).orElse(super.getSoundType(state, world, pos, entity));
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    private void restoreStorageBlock(Level worldIn, BlockPos currentPos, PosListData posList) {
        for (BlockPos storagePos : posList.getData()) {
            if (storagePos.equals(currentPos)) {
                continue;
            }
            this.getAltar(worldIn, storagePos).ifPresent(altar -> {
                BlockState storageState = altar.getStorageState();
                worldIn.setBlock(storagePos, storageState, Block.UPDATE_ALL);
            });
        }
        worldIn.playSound(null, currentPos, BEACON_DEACTIVATE, BLOCKS, 1.5f, 1);
    }

    private void takeOutItem(Level world, TileEntityAltar altar, Player player) {
        if (altar.isCanPlaceItem() && !ItemUtil.getStack(altar.handler, 0).isEmpty()) {
            ItemStack extractItem = ItemsUtil.extractItem(altar.handler, 0, 1, false, null);
            player.getInventory().placeItemBackInInventory(extractItem);
            world.playSound(null, altar.getBlockPos(), ITEM_FRAME_REMOVE_ITEM, PLAYERS, 1, 1);
            altarCraft(world, altar, player);
        }
    }

    private void takeInOrCraft(Level world, TileEntityAltar altar, Player playerIn) {
        if (altar.isCanPlaceItem() && ItemUtil.getStack(altar.handler, 0).isEmpty()) {
            ItemsUtil.setStackInSlot(altar.handler, 0, playerIn.getMainHandItem().copyWithCount(1));
            if (!playerIn.isCreative()) {
                playerIn.getMainHandItem().shrink(1);
            }
            world.playSound(null, altar.getBlockPos(), ITEM_FRAME_ADD_ITEM, PLAYERS, 1, 1);
            altarCraft(world, altar, playerIn);
        }
    }

    private void altarCraft(Level world, TileEntityAltar altar, Player playerIn) {
        List<ItemStack> items = Lists.newArrayList();
        List<BlockPos> posList = altar.getCanPlaceItemPosList().getData();

        for (int i = 0; i < posList.size(); i++) {
            BlockEntity te = world.getBlockEntity(posList.get(i));
            if (te instanceof TileEntityAltar altarOther) {
                items.add(i, altarOther.getStorageItem());
            }
        }
        if (items.isEmpty()) {
            return;
        }

        CraftingInput craftingInput = CraftingInput.of(6, 1, items);
        PowerAttachment powerNum = playerIn.getData(InitDataAttachment.POWER_NUM);
        if (world instanceof ServerLevel serverLevel) {
            RecipeManager recipeManager = serverLevel.recipeAccess();
            var recipeFor = recipeManager.getRecipeFor(InitRecipes.ALTAR_RECIPE.get(), craftingInput, world);
            recipeFor.ifPresent(recipe ->
                    spawnResultEntity(world, playerIn, powerNum, recipe, items, altar)
            );
        }
    }

    private Optional<TileEntityAltar> getAltar(BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileEntityAltar altar) {
            return Optional.of(altar);
        }
        return Optional.empty();
    }

    private void spawnResultEntity(Level world, Player playerIn, PowerAttachment power,
                                   RecipeHolder<AltarRecipe> holder, List<ItemStack> inventory,
                                   TileEntityAltar altar
    ) {
        ResourceKey<Recipe<?>> key = holder.id();
        AltarRecipe recipe = holder.value();

        if (power.get() < recipe.getPower()) {
            if (!world.isClientSide()) {
                MutableComponent info = Component.translatable("message.touhou_little_maid.altar.not_enough_power");
                playerIn.sendSystemMessage(info);
            }
            return;
        }

        power.min(recipe.getPower());
        playerIn.setData(InitDataAttachment.POWER_NUM, power.copy());

        BlockPos centrePos = getCentrePos(altar.getBlockPosList(), altar.getBlockPos());
        if (world instanceof ServerLevel serverLevel) {
            recipe.spawnOutputEntity(serverLevel, centrePos.above(2), inventory);
        }

        removeAllAltarItem(world, altar);
        spawnParticleInCentre(world, centrePos);
        world.playSound(null, centrePos, InitSounds.ALTAR_CRAFT.get(), PLAYERS, 1.0f, 1.0f);
        if (playerIn instanceof ServerPlayer serverPlayer) {
            InitTrigger.ALTAR_CRAFT.get().trigger(serverPlayer, key.identifier());
        }
    }

    private BlockPos getCentrePos(PosListData posList, BlockPos posClick) {
        int x = 0;
        int y = posClick.getY() - 2;
        int z = 0;
        for (BlockPos pos : posList.getData()) {
            if (pos.getY() == y) {
                x += pos.getX();
                z += pos.getZ();
            }
        }
        return new BlockPos(x / 8, y, z / 8);
    }

    private void removeAllAltarItem(Level world, TileEntityAltar altar) {
        for (BlockPos pos : altar.getCanPlaceItemPosList().getData()) {
            this.getAltar(world, pos).ifPresent(te -> {
                te.handler.set(0, ItemResource.EMPTY, 0);
                te.refresh();
                spawnParticleInCentre(world, te.getBlockPos());
            });
        }
    }

    private void spawnParticleInCentre(Level world, BlockPos centrePos) {
        int width = 1;
        int height = 1;
        for (int i = 0; i < 5; ++i) {
            double xSpeed = RANDOM.nextGaussian() * 0.02;
            double ySpeed = RANDOM.nextGaussian() * 0.02;
            double zSpeed = RANDOM.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.CLOUD,
                    centrePos.getX() + RANDOM.nextFloat() * width * 2 - width - xSpeed * 10,
                    centrePos.getY() + RANDOM.nextFloat() * height - ySpeed * 10,
                    centrePos.getZ() + RANDOM.nextFloat() * width * 2 - width - zSpeed * 10,
                    xSpeed, ySpeed, zSpeed);
            world.addParticle(ParticleTypes.SMOKE,
                    centrePos.getX() + RANDOM.nextFloat() * width * 2 - width - xSpeed * 10,
                    centrePos.getY() + RANDOM.nextFloat() * height - ySpeed * 10,
                    centrePos.getZ() + RANDOM.nextFloat() * width * 2 - width - zSpeed * 10,
                    xSpeed, ySpeed, zSpeed);
        }
    }
}
