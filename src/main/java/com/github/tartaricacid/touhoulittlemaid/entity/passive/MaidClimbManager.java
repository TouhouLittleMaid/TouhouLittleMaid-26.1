package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@MaidManagerDef(alias = "climbManager", exposeView = true)
public class MaidClimbManager {
    private final EntityMaid maid;
    /**
     * 女仆主动爬行标志位，用于管控女仆当前时刻需不需要攀爬
     */
    private boolean canClimb = false;

    /**
     * 爬梯的计时器，用于在爬梯后的一段时间内禁用摔落伤害
     */
    private int climbFallDelayTicks = 0;

    public MaidClimbManager(EntityMaid maid) {
        this.maid = maid;
    }

    public boolean isCanClimb() {
        return canClimb;
    }

    public void setCanClimb(boolean canClimb) {
        this.canClimb = canClimb;
    }

    void tick() {
        if (climbFallDelayTicks > 0) {
            climbFallDelayTicks--;
            maid.fallDistance = 0;
        }
    }

    Vec3 handleOnClimbable(Vec3 oriDelta) {
        // 主动爬行过程中严禁水平方向偏移，防止摔伤，y轴保持原样
        if (this.isCanClimb()) {
            Vec3 vec3 = maid.position();
            if (vec3.x() % 1 != 0.5D || vec3.z() % 1 != 0.5) {
                BlockPos currentPosition = maid.blockPosition().mutable();
                Vec3 centerPos = Vec3.atBottomCenterOf(currentPosition);
                maid.moveOrInterpolateTo(new Vec3(centerPos.x, vec3.y(), centerPos.z));
            }
            oriDelta = new Vec3(0, oriDelta.y, 0);
        }
        return oriDelta;
    }

    /**
     * 爬梯子状态加上路径判断
     */
    boolean onClimbable(BooleanSupplier superMethod) {
        boolean result = false;
        Path path = maid.getRawNavigation().getPath();
        if (path != null && !path.isDone()) {
            // 女仆是要爬梯子而不是路过梯子，那么也就意味着当前节点的前后必有一个节点是同坐标的
            for (int i = Math.max(0, path.getNextNodeIndex() - 3); i < Math.min(path.getNodeCount(), path.getNextNodeIndex() + 3) - 1; i++) {
                BlockPos pos1 = path.getNodePos(i);
                BlockPos pos2 = path.getNodePos(i + 1);
                if (pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ()) {
                    result = true;
                    break;
                }
            }
        }
        if (result) {
            result = superMethod.getAsBoolean();
            Level level = maid.level();
            BlockPos below = maid.blockPosition().below();
            // 用作脚手架和卡在梯子顶部的特判，避免女仆卡在脚手架顶上
            if (!result && !maid.isSpectator()) {
                Optional<BlockPos> ladderPos = CommonHooks.isLivingOnLadder(
                        level.getBlockState(below), level, below, maid
                );
                if (ladderPos.isPresent()) {
                    result = true;
                }
            }
        }
        if (result) {
            // 爬梯后一段时间禁用摔落伤害
            this.climbFallDelayTicks = 30;
            // 爬梯时，禁止旋转
            maid.getLastClimbablePos().ifPresent(climbablePos -> {
                BlockState blockState = maid.level.getBlockState(climbablePos);
                blockState.getOptionalValue(HorizontalDirectionalBlock.FACING).ifPresent(direction -> {
                    int yRot = direction.getOpposite().get2DDataValue() * 90;
                    maid.setYRot(yRot);
                    maid.setYHeadRot(yRot);
                });
            });
        }
        return result;
    }

    void travel(Vec3 travelVector, Consumer<Vec3> superMethod) {
        if (maid.isInWater()) {
            if (maid.getSwimManager().wantToSwim()) {
                maid.moveRelative(0.01F, travelVector);
                maid.move(MoverType.SELF, maid.getDeltaMovement());
                maid.setDeltaMovement(maid.getDeltaMovement().scale(0.9));
            } else if (maid.getSwimManager().isReadyToLand() || maid.isUnderWater()) {
                superMethod.accept(travelVector.scale(1.2).add(0, 0.5, 0));
            } else {
                superMethod.accept(travelVector.scale(1.2).add(0, 0.05, 0));
            }
        } else {
            superMethod.accept(travelVector);
        }
    }

    /**
     * 略微修改原版的方法，禁用了向上的动力源
     */
    Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction) {
        float speed = maid.getFrictionInfluencedSpeed(friction);
        maid.moveRelative(speed, deltaMovement);
        Vec3 vec3 = this.handleOnClimbable(maid.getDeltaMovement());
        maid.setDeltaMovement(vec3);
        maid.move(MoverType.SELF, maid.getDeltaMovement());
        return maid.getDeltaMovement();
    }

    interface View {
        MaidClimbManager getClimbManager();

        default boolean isCanClimb() {
            return getClimbManager().isCanClimb();
        }

        default void setCanClimb(boolean canClimb) {
            this.getClimbManager().setCanClimb(canClimb);
        }
    }
}
