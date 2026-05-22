package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.ScopedObject;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.*;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ObjectBinding;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserFunctionBinding implements ObjectBinding, ScopedObject {
    private final Object2ReferenceOpenHashMap<String, UserFunction> funcCache = new Object2ReferenceOpenHashMap<>();

    @Override
    public Function getProperty(String name) {
        return funcCache.computeIfAbsent(name, UserFunction::new);
    }

    public void resetScoped() {
        funcCache.clear();
    }

    private static class UserFunction implements Function {
        private String name;
        private IValue cache;

        private UserFunction(String name) {
            this.name = name;
        }

        @Override
        public @Nullable Object evaluate(@NotNull ExecutionContext<?> context, @NotNull ArgumentCollection arguments) {
            if (context.entity() instanceof IContext<?> ctx) {
                if (cache == null) {
                    if (name == null) {
                        return null;
                    }
                    cache = ctx.getUserFunction(name);
                    if (cache == null) {
                        ctx.debugPrint("User function not found: %s", name);
                        name = null;
                        return null;
                    }
                }

                return ctx.callUserFunction(context, cache, arguments);
            }
            return null;
        }
    }
}
