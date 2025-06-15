package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MaidMoveToPredicateBlockTask extends MaidMoveToBlockTask {
    private final Predicate<EntityMaid> searchCondition;
    private final BiPredicate<EntityMaid, BlockPos> blockPredicate;

    public MaidMoveToPredicateBlockTask(float movementSpeed, int verticalSearchRange,
                                        @Nullable Predicate<EntityMaid> searchCondition,
                                        @Nullable BiPredicate<EntityMaid, BlockPos> blockPredicate) {
        super(movementSpeed, verticalSearchRange);
        this.searchCondition = Objects.requireNonNullElseGet(searchCondition, () -> entity -> false);
        this.blockPredicate = Objects.requireNonNullElseGet(blockPredicate, () -> (entity, pos) -> false);
    }

    @Override
    protected void start(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        if (searchCondition.test(pEntity)) {
            searchForDestination(pLevel, pEntity);
        }
    }

    @Override
    protected boolean shouldMoveTo(ServerLevel worldIn, EntityMaid entityIn, BlockPos pos) {
        return blockPredicate.test(entityIn, pos);
    }

    @Override
    protected boolean checkPathReach(EntityMaid maid, MaidPathFindingBFS pathFinding, BlockPos pos) {
        // 检测周围 3x3x2 的方块是否可达
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (pathFinding.canPathReach(pos.offset(x, y, z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
