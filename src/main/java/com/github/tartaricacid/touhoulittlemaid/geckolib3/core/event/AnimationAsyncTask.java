package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import com.github.tartaricacid.touhoulittlemaid.util.ThreadTools;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationAsyncTask extends AnimationUpdateTask {
    private final AtomicBoolean flag = new AtomicBoolean(true);
    private final AtomicReference<Future<@Nullable AnimationEvent<?>>> future = new AtomicReference<>(null);

    public AnimationAsyncTask(Callable<@Nullable AnimationEvent<?>> supplier) {
        super(supplier);
    }

    private ExclusiveScope lock() {
        while (!flag.compareAndSet(true, false)) {
            Thread.onSpinWait();
        }
        return () -> flag.set(true);
    }

    @Override
    public void start() {
        if (future.getAcquire() == null) {
            try (var _ = lock()) {
                if (future.getAcquire() == null) {
                    VarHandle.releaseFence();
                    final var supplierValue = this.supplier;
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
    public AnimationEvent<?> getResult() {
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