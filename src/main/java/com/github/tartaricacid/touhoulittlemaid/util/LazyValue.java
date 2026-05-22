package com.github.tartaricacid.touhoulittlemaid.util;

import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.util.function.Supplier;

public class LazyValue<T> extends LazyInitializer<T> {
    private final Supplier<T> supplier;

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T initialize() {
        return supplier.get();
    }
}
