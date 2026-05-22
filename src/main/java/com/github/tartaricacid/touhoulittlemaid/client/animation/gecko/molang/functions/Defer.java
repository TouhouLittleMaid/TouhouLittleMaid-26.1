package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;

public class Defer extends ContextFunction<Object> {
    @Override
    protected Object eval(ExecutionContext<IContext<Object>> ctx, ArgumentCollection arguments) {
        if (ctx.entity().allowEmitting()) {
            var animCtx = ctx.entity().animationContext();
            if (animCtx != null) {
                var name = arguments.getAsPooledString(ctx, 0);
                if (name != StringPool.EMPTY) {
                    animCtx.defer(ctx, name, arguments, 1);
                }
            }
        }
        return null;
    }
}
