package com.github.tartaricacid.touhoulittlemaid.client.renderer.texture;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipPackTexture extends SizeTexture {
    private final Path zipFilePath;
    private int width = 16;
    private int height = 16;

    public ZipPackTexture(String zipFilePath, Identifier texturePath) {
        super(texturePath);
        this.zipFilePath = Paths.get(zipFilePath);
    }

    @Override
    public boolean isExist() {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            String filePath = String.format("assets/%s/%s", resourceId().getNamespace(), resourceId().getPath());
            ZipEntry entry = zipFile.getEntry(filePath);
            return entry != null;
        } catch (IOException e) {
            TouhouLittleMaid.LOGGER.error("Failed to inspect zip texture {}", resourceId(), e);
        }
        return false;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            String filePath = String.format("assets/%s/%s", resourceId().getNamespace(), resourceId().getPath());
            ZipEntry entry = zipFile.getEntry(filePath);
            if (entry == null) {
                return TextureContents.createMissing();
            }
            try (InputStream stream = zipFile.getInputStream(entry)) {
                NativeImage imageIn = NativeImage.read(stream);
                width = imageIn.getWidth();
                height = imageIn.getHeight();
                return new TextureContents(imageIn, null);
            }
        }
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
