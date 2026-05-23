package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class GetJarResources {
    public static void copyFolder(String sourcePath, Path targetPath) throws IOException, URISyntaxException {
        // 必须是相对路径
        if (sourcePath.startsWith("/")) {
            sourcePath = sourcePath.substring(1);
        }
        final String finalSourcePath = sourcePath;

        IModFile modFile = ModList.get().getModFileById(TouhouLittleMaid.MOD_ID).getFile();
        modFile.getContents().visitContent(finalSourcePath, (relativePath, resource) -> {
            Path relativeSourcePath = Path.of(finalSourcePath).relativize(Path.of(relativePath));
            Path target = targetPath.resolve(relativeSourcePath);
            try {
                // 检查父目录是否存在，如果不存在则创建
                Path parentDir = target.getParent();
                if (parentDir != null && !Files.isDirectory(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                Files.copy(resource.open(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                TouhouLittleMaid.LOGGER.error("Failed to copy file from {} to target: {}", relativePath, e.getMessage());
            } catch (Exception e) {
                TouhouLittleMaid.LOGGER.error("Unexpected error during file copy: {}", e.getMessage());
            }
        });
    }
}
