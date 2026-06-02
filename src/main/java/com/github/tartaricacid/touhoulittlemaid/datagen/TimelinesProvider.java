package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.timeline.Timeline;

public class TimelinesProvider {
    public static final ResourceKey<Timeline> MAID_SCHEDULE = key("maid_schedule");

    public static void bootstrap(BootstrapContext<Timeline> ctx) {
        HolderGetter<WorldClock> clocks = ctx.lookup(Registries.WORLD_CLOCK);
        Holder.Reference<WorldClock> overworldClock = clocks.getOrThrow(WorldClocks.OVERWORLD);

        ctx.register(MAID_SCHEDULE, Timeline
                .builder(overworldClock)
                .setPeriodTicks(24000)
                .addTrack(
                        InitBrains.MAID_DAY_SHIFT_ACTIVITY.get(),
                        track -> track.addKeyframe(0, Activity.WORK)
                                .addKeyframe(12000, Activity.IDLE)
                                .addKeyframe(16000, Activity.REST)
                )
                .addTrack(
                        InitBrains.MAID_NIGHT_SHIFT_ACTIVITY.get(),
                        track -> track.addKeyframe(0, Activity.REST)
                                .addKeyframe(8000, Activity.IDLE)
                                .addKeyframe(12000, Activity.WORK)
                )
                .addTrack(
                        InitBrains.MAID_ALL_DAY_ACTIVITY.get(),
                        track -> track.addKeyframe(0, Activity.WORK)
                )
                .build()
        );
    }

    public static ResourceKey<Timeline> key(String id) {
        return ResourceKey.create(Registries.TIMELINE, IdentifierUtil.modLoc(id));
    }
}
