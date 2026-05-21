package com.github.tartaricacid.touhoulittlemaid.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;

public class CacheIconTexture extends ReloadableTexture {
    private final @Nullable NativeImage imageIn;

    public CacheIconTexture(Identifier modelId, @Nullable NativeImage imageIn) {
        super(modelId);
        this.imageIn = imageIn;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) {
        if (imageIn == null) {
            return TextureContents.createMissing();
        }
        return new TextureContents(imageIn, null);
    }
}
