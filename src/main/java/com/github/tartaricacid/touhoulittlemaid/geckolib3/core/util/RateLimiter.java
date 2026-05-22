package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util;

public class RateLimiter {
    private float interval;
    private float aggregate;
    private float lastRequestTime;

    public RateLimiter() {
        interval = 1f / 120;
        aggregate = 1;
        lastRequestTime = 0;
    }

    public void setLimit(int limitPerSec) {
        interval = 1f / limitPerSec;
    }

    public boolean request(float time) {
        aggregate += time - lastRequestTime;
        lastRequestTime = time;

        if (aggregate < interval) {
            return false;
        }

        this.aggregate %= this.interval;
        return true;
    }

    public float getInterval() {
        return interval;
    }

    public void reset() {
        aggregate = interval;
        lastRequestTime = 0;
    }
}
