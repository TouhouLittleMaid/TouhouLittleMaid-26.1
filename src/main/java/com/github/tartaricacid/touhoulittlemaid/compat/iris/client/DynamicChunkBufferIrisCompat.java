package com.github.tartaricacid.touhoulittlemaid.compat.iris.client;

import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.RenderType;

/**
 * @author Argon4W
 */
public class DynamicChunkBufferIrisCompat {
    public static RenderType unwrap(RenderType renderType) {
        return renderType instanceof WrappableRenderType wrappable ? wrappable.unwrap() : renderType;
    }

    public static boolean isRenderingShadow() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
    }
}
