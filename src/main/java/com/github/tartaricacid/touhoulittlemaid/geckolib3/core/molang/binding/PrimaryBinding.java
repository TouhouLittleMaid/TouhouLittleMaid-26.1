package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.variable.ContextVariableBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.variable.ScopedVariableBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.variable.TempVariableBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.builtin.MathBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.builtin.QueryBinding;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ObjectBinding;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.StandardBindings;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrimaryBinding implements ObjectBinding {
    protected final Object2ReferenceOpenHashMap<String, Object> bindings = new Object2ReferenceOpenHashMap<>();
    protected final ScopedVariableBinding scopedBinding = new ScopedVariableBinding();
    protected final ContextVariableBinding ctxBinding = new ContextVariableBinding();
    protected final TempVariableBinding tempBinding = new TempVariableBinding();

    private final List<TransientObject> transientObjects;
    private final List<ScopedObject> scopedObjects;

    public PrimaryBinding(@Nullable Map<String, Object> extraBindings) {
        if (extraBindings != null) {
            bindings.putAll(extraBindings);
        }
        bindings.put("math", MathBinding.INSTANCE);
        bindings.put("query", QueryBinding.INSTANCE);
        bindings.put("q", QueryBinding.INSTANCE);
        bindings.put("loop", StandardBindings.LOOP_FUNC);
        bindings.put("for_each", StandardBindings.FOR_EACH_FUNC);

        bindings.put("variable", scopedBinding);
        bindings.put("v", scopedBinding);

        bindings.put("context", ctxBinding);
        bindings.put("c", ctxBinding);

        bindings.put("temp", tempBinding);
        bindings.put("t", tempBinding);

        transientObjects = bindings.values().stream()
                .filter(obj -> obj instanceof TransientObject)
                .map(obj -> (TransientObject) obj)
                .collect(Collectors.toList());
        scopedObjects = bindings.values().stream()
                .filter(obj -> obj instanceof ScopedObject)
                .map(obj -> (ScopedObject) obj)
                .collect(Collectors.toList());
    }

    @Override
    public Object getProperty(String name) {
        return bindings.get(name);
    }

    public void resetScoped() {
        for (ScopedObject scopedObject : scopedObjects) {
            scopedObject.resetScoped();
        }
    }

    public void resetTransient() {
        for (TransientObject transientObject : transientObjects) {
            transientObject.resetTransient();
        }
    }
}
