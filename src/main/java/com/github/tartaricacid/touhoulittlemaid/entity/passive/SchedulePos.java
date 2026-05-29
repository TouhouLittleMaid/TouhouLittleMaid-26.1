package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.util.TeleportHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public final class SchedulePos {
    private static final int MAX_TELEPORT_ATTEMPTS_TIMES = 10;

    private BlockPos workPos;
    private BlockPos idlePos;
    private BlockPos sleepPos;
    private Identifier dimension;
    private boolean configured = false;

    public static final StreamCodec<RegistryFriendlyByteBuf, SchedulePos> SCHEDULE_POS_STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SchedulePos::getWorkPos,
            BlockPos.STREAM_CODEC, SchedulePos::getIdlePos,
            BlockPos.STREAM_CODEC, SchedulePos::getSleepPos,
            Identifier.STREAM_CODEC, SchedulePos::getDimension,
            SchedulePos::new
    );

    public SchedulePos(BlockPos workPos, BlockPos idlePos, BlockPos sleepPos, Identifier dimension) {
        this.workPos = workPos;
        this.idlePos = idlePos;
        this.sleepPos = sleepPos;
        this.dimension = dimension;
    }

    public SchedulePos(BlockPos workPos, BlockPos idlePos, Identifier dimension) {
        this(workPos, idlePos, idlePos, dimension);
    }

    public SchedulePos(BlockPos workPos, Identifier dimension) {
        this(workPos, workPos, dimension);
    }

    public void setWorkPos(BlockPos workPos) {
        this.workPos = workPos;
    }

    public void setIdlePos(BlockPos idlePos) {
        this.idlePos = idlePos;
    }

    public void setSleepPos(BlockPos sleepPos) {
        this.sleepPos = sleepPos;
    }

    public void setDimension(Identifier dimension) {
        this.dimension = dimension;
    }

    public void tick(EntityMaid maid) {
        if (maid.tickCount % 40 == 0) {
            this.restrictTo(maid);
            if (maid.isWithinHome()) {
                return;
            }
            if (!maid.canBrainMoving()) {
                return;
            }
            double distanceSqr = maid.getHomePosition().distSqr(maid.blockPosition());
            int minTeleportDistance = (int) maid.getHomeRadius() + 4;
            if (distanceSqr > (minTeleportDistance * minTeleportDistance) && !this.sameWithRestrictCenter(maid)) {
                teleport(maid);
            } else {
                BehaviorUtils.setWalkAndLookTargetMemories(maid, maid.getHomePosition(), 0.7f, 3);
            }
        }
    }

    public void save(ValueOutput output) {
        ValueOutput child = output.child("MaidSchedulePos");
        child.store("Work", BlockPos.CODEC, this.workPos);
        child.store("Idle", BlockPos.CODEC, this.idlePos);
        child.store("Sleep", BlockPos.CODEC, this.sleepPos);
        child.store("Dimension", Codec.STRING, this.dimension.toString());
        child.store("Configured", Codec.BOOL, this.configured);
    }

    public void load(ValueInput input, EntityMaid maid) {
        ValueInput child = input.childOrEmpty("MaidSchedulePos");
        child.read("Work", BlockPos.CODEC).ifPresent(pos -> this.workPos = pos);
        child.read("Idle", BlockPos.CODEC).ifPresent(pos -> this.idlePos = pos);
        child.read("Sleep", BlockPos.CODEC).ifPresent(pos -> this.sleepPos = pos);
        child.read("Dimension", Codec.STRING).ifPresent(dim -> this.dimension = Identifier.parse(dim));
        child.read("Configured", Codec.BOOL).ifPresent(cfg -> this.configured = cfg);
        this.restrictTo(maid);
    }

    public void restrictTo(EntityMaid maid) {
        if (!maid.components.config.isHomeModeEnable()) {
            return;
        }
        Activity activity = maid.components.task.getScheduleDetail();
        if (activity == Activity.WORK) {
            maid.setHomeTo(this.workPos, MaidConfig.MAID_WORK_RANGE.get());
            return;
        }
        if (activity == Activity.IDLE) {
            maid.setHomeTo(this.idlePos, MaidConfig.MAID_IDLE_RANGE.get());
            return;
        }
        if (activity == Activity.REST) {
            maid.setHomeTo(this.sleepPos, MaidConfig.MAID_SLEEP_RANGE.get());
        }
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public BlockPos getWorkPos() {
        return workPos;
    }

    public BlockPos getIdlePos() {
        return idlePos;
    }

    public BlockPos getSleepPos() {
        return sleepPos;
    }

    public boolean isConfigured() {
        return configured;
    }

    public Identifier getDimension() {
        return dimension;
    }

    public void clear(EntityMaid maid) {
        this.idlePos = this.workPos;
        this.sleepPos = this.workPos;
        this.configured = false;
        this.dimension = maid.level.dimension().identifier();
        this.restrictTo(maid);
    }

    public void setHomeModeEnable(EntityMaid maid, BlockPos pos) {
        if (!this.configured) {
            this.workPos = pos;
            this.idlePos = pos;
            this.sleepPos = pos;
            this.dimension = maid.level.dimension().identifier();
        }
        this.restrictTo(maid);
    }

    @Nullable
    public BlockPos getNearestPos(EntityMaid maid) {
        if (this.configured) {
            BlockPos pos = this.workPos;
            double workDistance = maid.blockPosition().distSqr(this.workPos);
            double idleDistance = maid.blockPosition().distSqr(this.idlePos);
            double sleepDistance = maid.blockPosition().distSqr(this.sleepPos);
            if (workDistance > idleDistance) {
                pos = this.idlePos;
                workDistance = idleDistance;
            }
            if (workDistance > sleepDistance) {
                pos = this.sleepPos;
            }
            return pos;
        }
        return null;
    }

    private boolean sameWithRestrictCenter(EntityMaid maid) {
        BlockPos restrictCenter = maid.getHomePosition();
        return maid.getBrain().getMemory(MemoryModuleType.WALK_TARGET)
                .filter(walkTarget -> walkTarget.getTarget().currentBlockPosition().equals(restrictCenter))
                .isPresent();
    }

    private void teleport(EntityMaid maid) {
        for (int i = 0; i < MAX_TELEPORT_ATTEMPTS_TIMES; ++i) {
            if (TeleportHelper.teleportToRestrictCenter(maid)) {
                return;
            }
        }
    }
}
