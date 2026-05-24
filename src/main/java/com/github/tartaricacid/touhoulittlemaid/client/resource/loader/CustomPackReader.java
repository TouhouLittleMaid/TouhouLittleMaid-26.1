package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.FileResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ZipResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

final class CustomPackReader {
    private static final Marker MARKER = MarkerManager.getMarker("CustomPackReader");
    private static final Pattern DOMAIN = Pattern.compile("^assets/([\\w.]+)/$");

    static void readFolder(File root) {
        try {
            File[] domainFiles = root.toPath()
                    .resolve("assets")
                    .toFile()
                    .listFiles((dir, name) -> true);
            if (domainFiles == null) {
                return;
            }
            Path rootPath = root.toPath();
            var accessor = new FileResourceAccessor(rootPath);
            for (File domainDir : domainFiles) {
                if (domainDir.isDirectory()) {
                    String domain = domainDir.getName();
                    MaidPackLoader.loadPack(accessor, domain);
                    ChairPackLoader.loadPack(accessor, domain);
                    LanguageLoader.readLanguageFile(rootPath, domain);
                    CustomSoundLoader.loadSoundPack(rootPath, domain);
                }
            }
        } catch (IOException ioException) {
            LOGGER.error(MARKER, "Failed to read custom pack folder {}", root, ioException);
        }
    }

    static void readZip(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> iteration = zipFile.entries();
            while (iteration.hasMoreElements()) {
                String filePath = iteration.nextElement().getName();
                Matcher matcher = DOMAIN.matcher(filePath);
                if (matcher.find()) {
                    Path path = Paths.get(zipFile.getName());
                    var accessor = new ZipResourceAccessor(path);
                    String domain = matcher.group(1);
                    MaidPackLoader.loadPack(accessor, domain);
                    ChairPackLoader.loadPack(accessor, domain);
                    CustomSoundLoader.loadSoundPack(zipFile, domain);
                    continue;
                }
                // 语言文件单独加载
                LanguageLoader.readLanguageFile(zipFile, filePath);
            }
        } catch (IOException ioException) {
            LOGGER.error(MARKER, "Failed to read custom pack zip {}", file.getName(), ioException);
        }
    }
}
