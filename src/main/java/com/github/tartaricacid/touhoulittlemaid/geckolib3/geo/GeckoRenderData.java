package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.GeoModelState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.model.provider.data.EntityModelData;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;

/**
 * 如果要添加与特定实体渲染相关的字段，不要加在这里，应该：
 * 1. 另起一个类 class GeckoXXXRenderData extends GeckoRenderData
 * 2. 实现 GeckoXXXEntity 的 createRenderData 和 extractRenderData 函数
 **/
public class GeckoRenderData implements AutoCloseable {
    public GeoModelState modelState;
    public EntityModelData modelData;
    public Identifier texture;
    public RenderContext ctx;

    public int color = 0xFFFFFFFF;
    public int overlayUV = OverlayTexture.NO_OVERLAY;

    @Nullable
    public Matrix4f transform;

    public BiConsumer<RenderContext, GeoModelState> returnFunc;

    public boolean isClosed() {
        return returnFunc == null;
    }

    @Override
    public void close() {
        if (returnFunc != null) {
            returnFunc.accept(ctx, modelState);
            modelState = null;
            returnFunc = null;
        }
    }
}
