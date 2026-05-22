package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserFunctionArgument implements Variable {
    public static final UserFunctionArgument INSTANCE = new UserFunctionArgument();

    @Override
    public @Nullable Object evaluate(@NotNull ExecutionContext<?> context) {
        if (context.entity() instanceof IContext<?> ctx) {
            return ctx.userFunctionArgs();
        }
        return null;
    }
}
