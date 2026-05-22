package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class AnimationSyncTask extends AnimationUpdateTask {
    private AnimationEvent<?> result;

    public AnimationSyncTask(Callable<@Nullable AnimationEvent<?>> supplier) {
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
    public @Nullable AnimationEvent<?> getResult() {
        start();
        return result;
    }
}