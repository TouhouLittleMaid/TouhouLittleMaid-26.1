package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityGarageKit;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.data.ProfileData;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGarageKit;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.EntitySpawnReason.SPAWN_ITEM_USE;

public class BlockGarageKit extends HorizontalDirectionalBlock implements EntityBlock {
    public static final VoxelShape BLOCK_AABB = Block.box(4, 0, 4, 12, 16, 12);
    private static final MapCodec<BlockGarageKit> CODEC = simpleCodec(BlockGarageKit::new);

    public BlockGarageKit(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(SoundType.MUD)
                .strength(1, 2)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    public BlockGarageKit(Properties properties) {
        super(properties);
    }

    public static void fillItemCategory(CreativeModeTab.Output items) {
        if (FMLEnvironment.getDist() != Dist.CLIENT) {
            return;
        }

        for (String modelId : CustomPackLoader.MAID_MODELS.getModelIdSet()) {
            ItemStack stack = new ItemStack(InitBlocks.GARAGE_KIT.get());
            CustomData customData = stack.get(InitDataComponent.MAID_INFO);

            CompoundTag data;
            if (customData == null) {
                data = new CompoundTag();
            } else {
                data = customData.copyTag();
            }

            Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get());
            data.putString(InitDataComponent.ENTITY_ID_TAG_NAME, key.toString());
            ProfileData.directSetModelId(data, modelId);
            stack.set(InitDataComponent.MAID_INFO, CustomData.of(data));

            items.accept(stack);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityGarageKit(pos, state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = Lists.newArrayList(super.getDrops(state, params));
        BlockEntity parameter = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof BlockEntityGarageKit kit) {
            ItemStack stack = new ItemStack(InitBlocks.GARAGE_KIT.get());
            stack.set(InitDataComponent.MAID_INFO, CustomData.of(kit.getExtraData()));
            drops.add(stack);
        }
        return drops;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction opposite = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, opposite);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        this.getGarageKit(worldIn, pos).ifPresent(te -> {
            CustomData data = ItemGarageKit.getMaidData(stack);
            te.setExtraData(data.copyTag());
        });
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state,
                                       boolean includeData, Player player) {
        ItemStack stack = new ItemStack(InitBlocks.GARAGE_KIT.get());
        this.getGarageKit(level, pos).ifPresent(te -> {
            CustomData data = CustomData.of(te.getExtraData());
            stack.set(InitDataComponent.MAID_INFO, data);
        });
        return stack;
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos,
                                       Player playerIn, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if (!(worldIn instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        if (!(stack.getItem() instanceof SpawnEggItem)) {
            return InteractionResult.PASS;
        }
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof BlockEntityGarageKit garageKit)) {
            return InteractionResult.PASS;
        }
        EntityType<?> type = SpawnEggItem.getType(stack);
        if (type == null) {
            return InteractionResult.PASS;
        }

        String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        CompoundTag data = new CompoundTag();
        data.putString("id", id);

        Entity entity = type.create(worldIn, SPAWN_ITEM_USE);
        if (entity instanceof Mob mobEntity) {
            DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(pos);
            EventHooks.finalizeMobSpawn(mobEntity, serverLevel, difficulty, SPAWN_ITEM_USE, null);

            TagValueOutput context = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            mobEntity.saveWithoutId(context);
            data.merge(context.buildResult());
        }

        garageKit.setExtraData(data);
        return InteractionResult.SUCCESS;
    }

    private Optional<BlockEntityGarageKit> getGarageKit(BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockEntityGarageKit kit) {
            return Optional.of(kit);
        }
        return Optional.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BLOCK_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
