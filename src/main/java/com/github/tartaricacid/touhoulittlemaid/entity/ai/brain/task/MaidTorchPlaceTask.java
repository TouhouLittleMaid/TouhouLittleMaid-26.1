package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

public class MaidTorchPlaceTask extends Behavior<EntityMaid> {
    private final double closeEnoughDist;

    public MaidTorchPlaceTask(double closeEnoughDist) {
        super(ImmutableMap.of(InitBrains.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT));
        this.closeEnoughDist = closeEnoughDist;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        Brain<EntityMaid> brain = owner.getBrain();
        return brain.getMemory(InitBrains.TARGET_POS.get()).map(targetPos -> {
            Vec3 targetV3d = targetPos.currentPosition();
            if (owner.distanceToSqr(targetV3d) > Math.pow(closeEnoughDist, 2)) {
                Optional<WalkTarget> walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET);
                if (walkTarget.isEmpty() || !walkTarget.get().getTarget().currentPosition().equals(targetV3d)) {
                    brain.eraseMemory(InitBrains.TARGET_POS.get());
                }
                return false;
            }
            return true;
        }).orElse(false);
    }

    @Override
    protected void start(ServerLevel world, EntityMaid maid, long gameTimeIn) {
        maid.getBrain().getMemory(InitBrains.TARGET_POS.get()).ifPresent(posWrapper -> {
            if (getAndExtractTorchItem(maid)) {
                BlockPos pos = posWrapper.currentBlockPosition().above();
                BlockState torchState = Blocks.TORCH.defaultBlockState();
                world.setBlock(pos, torchState, Block.UPDATE_ALL_IMMEDIATE);
                SoundType soundType = torchState.getSoundType(world, pos, maid);
                world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                maid.swing(InteractionHand.MAIN_HAND);
                maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get());
                maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            }
        });
    }

    private boolean getAndExtractTorchItem(EntityMaid entityMaid) {
        CombinedResourceHandler<ItemResource> itemHandler = entityMaid.getAvailableInv(false);
        try(Transaction transaction = Transaction.openRoot()){
            int extract = itemHandler.extract(ItemResource.of(Items.TORCH), 1, transaction);
            if(extract != 0){
                transaction.commit();
                return true;
            }
        }
        return false;
    }
}
