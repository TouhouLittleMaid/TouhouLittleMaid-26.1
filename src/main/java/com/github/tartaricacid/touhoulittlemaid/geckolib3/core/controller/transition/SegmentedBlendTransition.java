package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.transition;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.OrderedSegmentSearcher;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.List;


@SuppressWarnings("unused")
public class SegmentedBlendTransition implements IBlendTransition {
    private final List<Segment> segments;
    private final OrderedSegmentSearcher<Segment> segmentSearcher;

    
    public SegmentedBlendTransition(float[] time, float[] position) {
        var segments = new ReferenceArrayList<Segment>(time.length - 1);
        for (int i = 0; i < time.length - 1; i++) {
            segments.add(new Segment(time[i] * 20, time[i + 1] * 20, 1 - position[i], 1 - position[i + 1]));
        }

        this.segments = segments;
        this.segmentSearcher = new OrderedSegmentSearcher<>(this.segments, 0, s -> s.endTick);
    }

    private SegmentedBlendTransition(List<Segment> segments) {
        this.segments = segments;
        this.segmentSearcher = new OrderedSegmentSearcher<>(this.segments, 0, s -> s.endTick);
    }

    @Override
    public float get(float tick) {
        var segment = segmentSearcher.search(tick);
        if (tick <= segment.startTick) {
            return segment.startPosition;
        } else if (tick >= segment.endTick) {
            return segment.startPosition + segment.positionDelta;
        }
        var progress = (tick - segment.startTick) / segment.totalTick;
        return segment.startPosition + segment.positionDelta * progress;
    }

    @Override
    public float length() {
        return segmentSearcher.rightBound();
    }

    @Override
    public SegmentedBlendTransition startNew() {
        return new SegmentedBlendTransition(segments);
    }

    private static class Segment {
        public final float startTick;
        public final float totalTick;
        public final float endTick;
        public final float startPosition;
        public final float positionDelta;

        public Segment(float startTick, float endTick, float startPosition, float endPosition) {
            this.startTick = startTick;
            this.totalTick = endTick - startTick;
            this.endTick = endTick;
            this.startPosition = startPosition;
            this.positionDelta = endPosition - startPosition;
        }
    }
}
