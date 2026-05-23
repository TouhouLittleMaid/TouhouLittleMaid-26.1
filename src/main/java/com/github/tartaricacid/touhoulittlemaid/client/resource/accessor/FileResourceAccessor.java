package com.github.tartaricacid.touhoulittlemaid.client.resource.accessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileResourceAccessor implements ResourceAccessor {
    private final Path rootPath;

    public FileResourceAccessor(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return Files.newInputStream(rootPath.resolve(path));
    }

    @Override
    public boolean exists(String path) {
        return rootPath.resolve(path).toFile().isFile();
    }
}
