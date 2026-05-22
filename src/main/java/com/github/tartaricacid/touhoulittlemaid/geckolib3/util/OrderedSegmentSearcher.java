package com.github.tartaricacid.touhoulittlemaid.geckolib3.util;

import java.util.List;

public class OrderedSegmentSearcher<T> {
    private final List<T> segments;
    private final float leftBound;
    private final float rightBound;
    private final ToFloatFunction<T> rightBoundGetter;

    private int currentIndex;
    private float currentLeft;
    private float currentRight;

    public OrderedSegmentSearcher(List<T> segments, float leftBound, ToFloatFunction<T> rightBoundGetter) {
        this.segments = segments;
        this.leftBound = leftBound;
        this.rightBound = rightBoundGetter.apply(segments.getLast());
        this.rightBoundGetter = rightBoundGetter;

        this.currentIndex = 0;
        this.currentLeft = leftBound;
        this.currentRight = this.rightBoundGetter.apply(segments.getFirst());
        if (leftBound > this.currentRight || leftBound > this.rightBound || this.currentRight > this.rightBound) {
            throw new IllegalArgumentException();
        }
    }

    public T search(final float point) {
        if (segments.size() == 1) {
            return segments.getFirst();
        }

        if (point < currentLeft) {
            var firstSegment = segments.getFirst();
            if (currentIndex == 0) {
                return firstSegment;
            }

            currentIndex = 0;
            currentLeft = leftBound;
            currentRight = rightBoundGetter.apply(firstSegment);
            if (point >= currentRight) {
                return search(point);
            } else {
                return firstSegment;
            }
        }

        if (point == currentLeft || point < currentRight || currentRight == rightBound) {
            return segments.get(currentIndex);
        }

        if (point >= rightBound) {
            var lastSegment = segments.getLast();
            var left = rightBoundGetter.apply(segments.get(segments.size() - 2));
            var right = rightBoundGetter.apply(lastSegment);
            if (left > right) {
                throw new IllegalArgumentException();
            }
            currentIndex = segments.size() - 1;
            currentLeft = left;
            currentRight = right;
            return lastSegment;
        }

        float left = currentRight;
        for (int index = currentIndex + 1; ; ++index) {
            var segment = segments.get(index);
            var right = rightBoundGetter.apply(segment);
            if (left > right) {
                throw new IllegalArgumentException();
            }
            if (point < right) {
                currentIndex = index;
                currentLeft = left;
                currentRight = right;
                return segment;
            }
            left = right;
        }
    }

    public float leftBound() {
        return leftBound;
    }

    public float rightBound() {
        return rightBound;
    }

    @FunctionalInterface
    public interface ToFloatFunction<T> {
        float apply(T t);
    }
}
