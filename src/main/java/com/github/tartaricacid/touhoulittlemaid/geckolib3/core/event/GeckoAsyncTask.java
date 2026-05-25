package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import com.github.tartaricacid.touhoulittlemaid.util.ThreadTools;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GeckoAsyncTask<TData extends GeckoRenderData> extends GeckoUpdateTask<TData> {
    private final AtomicBoolean flag = new AtomicBoolean(true);
    private final AtomicReference<Future<@Nullable TData>> future = new AtomicReference<>(null);

    public GeckoAsyncTask(Callable<@Nullable TData> supplier) {
        super(supplier);
    }

    private ExclusiveScope lock() {
        while (!flag.compareAndSet(true, false)) {
            Thread.onSpinWait();
        }
        return () -> flag.set(true);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start() {
        if (future.getAcquire() == null) {
            try (var _ = lock()) {
                if (future.getAcquire() == null) {
                    final var supplierValue = this.supplier;
                    VarHandle.releaseFence();
                    future.setRelease(ThreadTools.submit(() -> {
                        try {
                            VarHandle.acquireFence();
                            return supplierValue.call();
                        } finally {
                            VarHandle.releaseFence();
                        }
                    }));
                    supplier = null;
                }
            }
        }
    }

    @Override
    @Nullable
    public TData getResult() {
        try {
            var futureValue = future.getAcquire();
            if (futureValue == null) {
                try (var _ = lock()) {
                    futureValue = future.getAcquire();
                    if (futureValue == null) {
                        futureValue = CompletableFuture.completedFuture(supplier.call());
                        supplier = null;
                        future.setRelease(futureValue);
                    }
                }
            }
            var result = futureValue.get();
            VarHandle.acquireFence();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private interface ExclusiveScope extends AutoCloseable {
        void close();
    }
}