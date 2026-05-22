package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class Rot2Camera extends ContextFunction<Object> {
    @Override
    protected Object eval(ExecutionContext<IContext<Object>> ctx, ArgumentCollection arguments) {
        int axis = arguments.getAsInt(ctx, 0);
        if (axis < 0 || axis > 1) {
            return null;
        }
        Camera mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (axis == 0) {
            return -mainCamera.xRot();
        } else {
            return 180 + mainCamera.yRot();
        }
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 1;
    }
}
