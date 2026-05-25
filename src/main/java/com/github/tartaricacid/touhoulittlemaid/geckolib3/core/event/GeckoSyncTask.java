package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class GeckoSyncTask<TData extends GeckoRenderData> extends GeckoUpdateTask<TData> {
    private TData result;

    public GeckoSyncTask(Callable<@Nullable TData> supplier) {
        super(supplier);
    }

    @Override
    public void start() {
        if (supplier != null) {
            try {
                result = supplier.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            supplier = null;
        }
    }

    @Override
    public @Nullable TData getResult() {
        start();
        return result;
    }
}