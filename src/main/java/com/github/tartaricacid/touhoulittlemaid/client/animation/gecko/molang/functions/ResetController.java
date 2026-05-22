package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;

public class ResetController extends ContextFunction<Object> {
    @Override
    protected Object eval(ExecutionContext<IContext<Object>> ctx, ArgumentCollection arguments) {
        var controller = ctx.entity().animationEvent().getCodedController();
        if (controller == null) {
            return null;
        }

        controller.reset();
        return null;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 0;
    }
}
