package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public abstract class AnimationUpdateTask {
    public static final AnimationUpdateTask NOP = new AnimationUpdateTask(null) {
        @Override
        public void start() {}

        @Override
        @Nullable
        public AnimationEvent<?> getResult() {
            return null;
        }
    };

    protected Callable<@Nullable AnimationEvent<?>> supplier;

    public AnimationUpdateTask(Callable<@Nullable AnimationEvent<?>> supplier) {
        this.supplier = supplier;
    }

    /**
     * 启动任务
     */
    public abstract void start();

    /**
     * 获取结果，若还未完成则等待
     */
    @Nullable
    public abstract AnimationEvent<?> getResult();
}
