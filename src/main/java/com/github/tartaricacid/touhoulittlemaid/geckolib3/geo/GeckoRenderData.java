package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.GeoModelState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * 如果要添加与特定实体渲染相关的字段，不要加在这里，应该：
 * 1. 另起一个类 class GeoXXXRenderData extends GeoRenderData
 * 2. 实现 GeckoXXXEntity 的 createRenderData 和 extractRenderData 函数
 **/
public class GeckoRenderData {
    public GeoModelState modelState;
    public Identifier texture;
    public RenderContext ctx;

    public int color = ARGB.color(1, 1, 1, 1);
    public int outlineColor;
    public int lightUV;
    public int overlayUV = OverlayTexture.NO_OVERLAY;

    @Nullable
    public Matrix4f transform;

    public float heightScale;
    public float widthScale;
}
