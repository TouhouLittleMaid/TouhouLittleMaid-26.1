package com.github.tartaricacid.touhoulittlemaid.client.resource;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceAccessor implements ResourceAccessor {
    private final Path zipFilePath;

    public ZipResourceAccessor(Path zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    @Override
    public InputStream open(String path) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            ZipEntry entry = zipFile.getEntry(path);
            if (entry == null) {
                throw new IOException("Entry not found in zip " + zipFilePath + ": " + path);
            }
            try (InputStream stream = zipFile.getInputStream(entry)) {
                byte[] bytes = stream.readAllBytes();
                return new ByteArrayInputStream(bytes);
            }
        }
    }

    @Override
    public boolean exists(String path) {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            return zipFile.getEntry(path) != null;
        } catch (IOException e) {
            TouhouLittleMaid.LOGGER.error("Failed to inspect zip {} for {}", zipFilePath, path, e);
            return false;
        }
    }
}
