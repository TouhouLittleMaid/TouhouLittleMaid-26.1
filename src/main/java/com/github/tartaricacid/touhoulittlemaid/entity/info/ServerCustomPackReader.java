package com.github.tartaricacid.touhoulittlemaid.entity.info;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.SettingReader;
import com.github.tartaricacid.touhoulittlemaid.util.ZipFileCheck;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

final class ServerCustomPackReader {
    private static final Marker MARKER = MarkerManager.getMarker("ServerCustomPackReader");
    private static final Pattern DOMAIN = Pattern.compile("^assets/([\\w.]+)/$");

    static void reload(Path packFolderPath) {
        File packFolder = packFolderPath.toFile();
        if (!packFolder.isDirectory()) {
            try {
                Files.createDirectories(packFolder.toPath());
            } catch (IOException e) {
                LOGGER.error(MARKER, "Failed to create custom pack directory {}", packFolder, e);
                return;
            }
        }
        loadPacks(packFolder);
    }

    private static void loadPacks(File packFolder) {
        File[] files = packFolder.listFiles((dir, name) -> true);
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".zip")) {
                try {
                    if (ZipFileCheck.isZipFile(file)) {
                        readZip(file);
                    } else {
                        TouhouLittleMaid.LOGGER.error("{} file is corrupt and cannot be loaded.", file.getName());
                    }
                } catch (IOException ioException) {
                    LOGGER.error(MARKER, "Failed to inspect custom pack file {}", file.getName(), ioException);
                }
            }
            if (file.isDirectory()) {
                readFolder(file);
            }
        }
    }

    static void readFolder(File root) {
        File[] domainFiles = root.toPath().resolve("assets").toFile().listFiles((dir, name) -> true);
        if (domainFiles == null) {
            return;
        }
        Path rootPath = root.toPath();
        for (File domainDir : domainFiles) {
            if (domainDir.isDirectory()) {
                String domain = domainDir.getName();
                ServerMaidModelPackLoader.load(rootPath, domain);
                // 读取 AI 预设
                SettingReader.readCustomPack(rootPath, domain);
            }
        }
    }

    static void readZip(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> iteration = zipFile.entries();
            while (iteration.hasMoreElements()) {
                Matcher matcher = DOMAIN.matcher(iteration.nextElement().getName());
                if (matcher.find()) {
                    String domain = matcher.group(1);
                    ServerMaidModelPackLoader.load(zipFile, domain);
                    // 读取 AI 预设
                    SettingReader.readCustomPack(zipFile, domain);
                }
            }
        } catch (IOException ioException) {
            LOGGER.error(MARKER, "Failed to read custom pack zip {}", file.getName(), ioException);
        }
    }
}
