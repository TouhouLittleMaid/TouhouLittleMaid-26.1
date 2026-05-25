package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public abstract class GeckoUpdateTask<TData extends GeckoRenderData> {
    private static final GeckoUpdateTask<GeckoRenderData> NOP = new GeckoUpdateTask<>(null) {
        @Override
        public void start() {}

        @Override
        @Nullable
        public GeckoRenderData getResult() {
            return null;
        }
    };

    protected Callable<@Nullable TData> supplier;

    public GeckoUpdateTask(Callable<@Nullable TData> supplier) {
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
    public abstract TData getResult();

    @SuppressWarnings("unchecked")
    public static <TData extends GeckoRenderData> GeckoUpdateTask<TData> nop() {
        return (GeckoUpdateTask<TData>) NOP;
    }
}
