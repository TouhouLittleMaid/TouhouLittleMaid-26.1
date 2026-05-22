package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.PooledStringHashMap;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;

import java.util.function.Consumer;

public class MolangMemory implements IScopedVariableStorage {
    private static final int SCOPED_INIT_CAPACITY = 16;

    private final StackMemory stackMemory = new StackMemory();
    private final PooledStringHashMap<VariableValueHolder> scopedMap = new PooledStringHashMap<>(SCOPED_INIT_CAPACITY);

    @Override
    public Object getScoped(int name) {
        VariableValueHolder valueHolder = scopedMap.computeIfAbsent(name, n -> new VariableValueHolder());
        return valueHolder.value;
    }

    @Override
    public void setScoped(int name, Object value) {
        VariableValueHolder valueHolder = scopedMap.computeIfAbsent(name, n -> new VariableValueHolder());
        valueHolder.value = value;
    }

    public StackMemory getStackMemory() {
        return stackMemory;
    }

    public void initialize() {
        scopedMap.clear();
    }

    public void visitScopedVariableNames(Consumer<String> visitor) {
        for (var name : scopedMap.keySet()) {
            visitor.accept(StringPool.getString(name));
        }
    }

    private static class VariableValueHolder {
        public Object value = null;
    }
}
