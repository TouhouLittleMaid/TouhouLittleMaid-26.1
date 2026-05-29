package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;

public class MaidCollectHoneyTask extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 100;
    private final float speed;
    private final int closeEnoughDist;

    public MaidCollectHoneyTask(float speed, int closeEnoughDist) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                InitBrains.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT));
        this.speed = speed;
        this.closeEnoughDist = closeEnoughDist;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid maid) {
        if (super.checkExtraStartConditions(worldIn, maid) && maid.canBrainMoving()) {
            BlockPos beehivePos = findBeehive(worldIn, maid);
            if (beehivePos != null && maid.isWithinHome(beehivePos)) {
                if (beehivePos.distToCenterSqr(maid.position()) < Math.pow(this.closeEnoughDist, 2)) {
                    maid.getBrain().setMemory(InitBrains.TARGET_POS.get(), new BlockPosTracker(beehivePos));
                    return true;
                }
                BehaviorUtils.setWalkAndLookTargetMemories(maid, beehivePos, speed, 1);
                this.setNextCheckTickCount(5);
            } else {
                maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get());
            }
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        maid.getBrain().getMemory(InitBrains.TARGET_POS.get()).ifPresent(target -> {
            BlockPos hivePos = target.currentBlockPosition();
            BlockState hiveBlockState = level.getBlockState(hivePos);
            if (!hiveBlockState.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                return;
            }
            if (hiveBlockState.getValue(BeehiveBlock.HONEY_LEVEL) < 5) {
                return;
            }
            CombinedResourceHandler<@NotNull ItemResource> maidAvailableInv = maid.components.item.getAvailableInv(true);
            if (!this.collectHoneyComb(level, maid, maidAvailableInv, hiveBlockState, hivePos)) {
                this.collectHoneyBottle(level, maid, maidAvailableInv, hiveBlockState, hivePos);
            }
        });
        maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get());
        maid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private void collectHoneyBottle(ServerLevel level, EntityMaid maid, CombinedResourceHandler<ItemResource> maidAvailableInv, BlockState hiveBlockState, BlockPos hivePos) {
        int slot = ItemsUtil.findStackSlot(maidAvailableInv, stack -> stack.is(Items.HONEY_BOTTLE));
        if (slot == -1)
            return;
        try (Transaction transaction = Transaction.openRoot()) {
            int bottle = maidAvailableInv.extract(slot, ItemResource.of(Items.HONEY_BOTTLE), 1, transaction);
            if (bottle != 0) {
                int result = maidAvailableInv.insert(ItemResource.of(Items.HONEY_BOTTLE), 1, transaction);
                // 背包满了就不收集了
                if (result != 0) {
                    return;
                }
                level.playSound(null, maid.getX(), maid.getY(), maid.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                resetHoneyLevel(level, hiveBlockState, hivePos);
                maid.swing(InteractionHand.MAIN_HAND);
                transaction.commit();
            }
        }
    }

    private boolean collectHoneyComb(ServerLevel level, EntityMaid maid, CombinedResourceHandler<@NotNull ItemResource> maidAvailableInv, BlockState hiveBlockState, BlockPos hivePos) {
        boolean hasShears = maid.getMainHandItem().canPerformAction(ItemAbilities.SHEARS_HARVEST);
        if (hasShears) {
            try (Transaction transaction = Transaction.openRoot()) {
                int result = maidAvailableInv.extract(ItemResource.of(Items.HONEYCOMB), 3, transaction);
                // 背包满了就不收集了
                if (result != 0) {
                    return false;
                }
                level.playSound(null, maid.getX(), maid.getY(), maid.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                resetHoneyLevel(level, hiveBlockState, hivePos);
                maid.swing(InteractionHand.MAIN_HAND);
                maid.getMainHandItem().hurtAndBreak(1, maid, EquipmentSlot.MAINHAND);
                transaction.commit();
                return true;
            }
        }
        return false;
    }

    @Nullable
    private BlockPos findBeehive(ServerLevel world, EntityMaid maid) {
        BlockPos blockPos = maid.getBrainSearchPos();
        PoiManager poiManager = world.getPoiManager();
        int range = (int) maid.getHomeRadius();
        return poiManager.getInRange(type -> type.is(PoiTypeTags.BEE_HOME), blockPos, range, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos).filter(pos -> canCollectHoney(world, pos))
                .min(Comparator.comparingDouble(pos -> pos.distSqr(maid.blockPosition()))).orElse(null);
    }

    private boolean canCollectHoney(ServerLevel world, BlockPos hivePos) {
        BlockState state = world.getBlockState(hivePos);
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
            return state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5;
        }
        return false;
    }

    public void resetHoneyLevel(Level level, BlockState state, BlockPos pos) {
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
            level.setBlock(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), Block.UPDATE_ALL);
        }
    }
}
