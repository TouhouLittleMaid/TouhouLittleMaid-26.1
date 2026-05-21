package com.github.tartaricacid.touhoulittlemaid.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePackTexture extends SizeTexture {
    private final Path rootPath;
    private int width = 16;
    private int height = 16;

    public FilePackTexture(Path rootPath, Identifier texturePath) {
        super(texturePath);
        this.rootPath = rootPath;
    }

    @Override
    public boolean isExist() {
        return rootPath
                .resolve("assets")
                .resolve(resourceId().getNamespace())
                .resolve(resourceId().getPath())
                .toFile()
                .isFile();
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        File textureFile = rootPath
                .resolve("assets")
                .resolve(resourceId().getNamespace())
                .resolve(resourceId().getPath())
                .toFile();

        if (textureFile.isFile()) {
            try (InputStream stream = Files.newInputStream(textureFile.toPath())) {
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
