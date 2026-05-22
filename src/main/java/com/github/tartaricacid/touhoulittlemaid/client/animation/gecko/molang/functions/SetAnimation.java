package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.CtrlBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import org.apache.commons.lang3.StringUtils;

public class SetAnimation extends ContextFunction<Object> {
    @Override
    protected Object eval(ExecutionContext<IContext<Object>> ctx, ArgumentCollection arguments) {
        var controller = ctx.entity().animationEvent().getCodedController();
        if (controller == null) {
            return null;
        }
        var anim = arguments.getAsString(ctx, 0);
        if (StringUtils.isEmpty(anim)) {
            return null;
        }

        LoopType loopType;
        if (arguments.size() == 1) {
            loopType = null;
        } else {
            loopType = switch (arguments.getAsInt(ctx, 1)) {
                case CtrlBinding.PLAY_ONCE -> LoopType.PLAY_ONCE;
                case CtrlBinding.LOOP -> LoopType.LOOP;
                case CtrlBinding.HOLD_ON_LAST_FRAME -> LoopType.HOLD_ON_LAST_FRAME;
                default -> null;
            };
        }

        controller.setAnimation(anim, loopType);
        return null;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 1 || size == 2;
    }
}
