package com.github.tartaricacid.touhoulittlemaid.entity.info;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.GetJarResources;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;
import static com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader.PACK_FOLDER;

public class CommonDefaultPack {
    private static final String CUSTOM_PACK_DIR_NAME = "tlm_custom_pack";
    private static final String DEFAULT_PACK_NAME = "touhou_little_maid-1.0.0";
    private static final Marker MARKER = MarkerManager.getMarker("CommonDefaultPack");

    public static void initCommonDefaultPack() {
        TouhouLittleMaid.LOGGER.info("common default pack init start...");

        StopWatch watch = StopWatch.createStarted();
        {
            File packFolder = PACK_FOLDER.resolve(DEFAULT_PACK_NAME).toFile();
            createCustomPackFolder(packFolder);
            unpackDefaultPack(packFolder);
        }
        watch.stop();

        double time = watch.getTime(TimeUnit.MICROSECONDS) / 1000.0;
        TouhouLittleMaid.LOGGER.info("common default pack init finished, cost time: {} ms", time);
    }

    private static void createCustomPackFolder(File packFolder) {
        if (!packFolder.isDirectory()) {
            try {
                Files.createDirectories(packFolder.toPath());
            } catch (IOException e) {
                LOGGER.error(MARKER, "Failed to create folder {}", packFolder.getAbsolutePath(), e);
            }
        }
    }

    private static void unpackDefaultPack(File packFolder) {
        // 不管存不存在，强行覆盖
        String jarDefaultPackPath = "assets/%s/%s/%s".formatted(TouhouLittleMaid.MOD_ID, CUSTOM_PACK_DIR_NAME, DEFAULT_PACK_NAME);
        try {
            GetJarResources.copyFolder(jarDefaultPackPath, packFolder.toPath());
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(MARKER, "Failed to unpack default pack: {}", jarDefaultPackPath, e);
        }
    }
}
