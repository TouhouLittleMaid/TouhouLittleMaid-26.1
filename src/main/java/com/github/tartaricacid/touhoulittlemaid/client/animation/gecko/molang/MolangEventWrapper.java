package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatLists;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;

public class MolangEventWrapper {
    public static final String MAID_INIT = "maid_init";
    public static final String MAID_UPDATE = "maid_update";
    public static final String SYNC = "sync";
    public static final String DEFER = "defer";

    public static IValue wrap(List<IValue> handlers, FloatArrayList args) {
        return wrap(handlers, args != null ? args : FloatLists.emptyList());
    }

    public static IValue wrap(List<IValue> handlers) {
        return wrap(handlers, ObjectLists.emptyList());
    }

    public static IValue wrap(List<IValue> handlers, List<?> array) {
        return evaluator -> {
            if (evaluator.entity() instanceof IContext<?> ctx) {
                for (var handler : handlers) {
                    ctx.callUserFunction(evaluator, handler, array);
                }
            }
            return null;
        };
    }
}
