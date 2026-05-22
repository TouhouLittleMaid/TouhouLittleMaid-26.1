package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;

public class SetBeginningTransitionLength extends ContextFunction<Object> {
    @Override
    protected Object eval(ExecutionContext<IContext<Object>> ctx, ArgumentCollection arguments) {
        var controller = ctx.entity().animationEvent().getCodedController();
        if (controller == null) {
            return null;
        }
        var blendTime = arguments.getAsFloat(ctx, 0);
        if (blendTime < 0) {
            return null;
        }
        controller.setBeginningTransitionLength(blendTime);
        return null;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 1;
    }
}
