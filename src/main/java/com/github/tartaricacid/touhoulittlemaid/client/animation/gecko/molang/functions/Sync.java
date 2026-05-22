package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.PlayerEntityFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.player.AbstractClientPlayer;

public class Sync extends PlayerEntityFunction {
    private static final int MAX_ARGS_SIZE = 16;

    @Override
    protected Object eval(ExecutionContext<IContext<AbstractClientPlayer>> context, ArgumentCollection arguments) {
        if (!context.entity().allowEmitting()) {
            return null;
        }

        // 当前未实现网络同步
        context.entity().animatableEntity().molangSync(packArguments(context, arguments));

        return null;
    }

    private static FloatArrayList packArguments(ExecutionContext<IContext<AbstractClientPlayer>> context, ArgumentCollection arguments) {
        var args = new FloatArrayList(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            args.add(arguments.getAsFloat(context, i));
        }
        return args;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size <= MAX_ARGS_SIZE;
    }
}
