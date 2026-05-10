package com.github.tartaricacid.touhoulittlemaid.entity.ai.path;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.debug.target.DebugMaidManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 用于加入debug信息
 */
public class MaidWrappedPathFinder extends PathFinder {
    protected Mob mob;

    public MaidWrappedPathFinder(NodeEvaluator nodeEvaluator, int maxVisitedNodes) {
        super(nodeEvaluator, maxVisitedNodes);
    }

    @Nullable
    @Override
    public Path findPath(PathNavigationRegion region, Mob mob, Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
        this.mob = mob;
        this.setCaptureDebug(() -> TouhouLittleMaid.DEBUG && mob instanceof EntityMaid maid && DebugMaidManager.getDebuggingPlayer(maid) != null);
        return super.findPath(region, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
    }
}
