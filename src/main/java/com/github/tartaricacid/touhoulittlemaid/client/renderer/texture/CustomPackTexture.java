package com.github.tartaricacid.touhoulittlemaid.client.renderer.texture;

import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class CustomPackTexture extends SizeTexture {
    private final ResourceAccessor accessor;
    private int width = 16;
    private int height = 16;

    public CustomPackTexture(ResourceAccessor accessor, Identifier texturePath) {
        super(texturePath);
        this.accessor = accessor;
    }

    @Override
    public boolean isExist() {
        Identifier id = resourceId();
        String path = "assets/%s/%s".formatted(id.getNamespace(), id.getPath());
        return accessor.exists(path);
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        Identifier id = resourceId();
        String path = "assets/%s/%s".formatted(id.getNamespace(), id.getPath());
        if (accessor.exists(path)) {
            try (InputStream stream = accessor.open(path)) {
                NativeImage imageIn = NativeImage.read(stream);
                width = imageIn.getWidth();
                height = imageIn.getHeight();
                return new TextureContents(imageIn, null);
            }
        }
        return TextureContents.createMissing();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
