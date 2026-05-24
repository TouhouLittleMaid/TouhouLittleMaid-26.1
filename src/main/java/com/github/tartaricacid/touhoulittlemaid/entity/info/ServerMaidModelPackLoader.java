package com.github.tartaricacid.touhoulittlemaid.entity.info;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

final class ServerMaidModelPackLoader {
    private static final Marker MARKER = MarkerManager.getMarker("ServerMaidModelPackLoader");

    static void load(Path rootPath, String domain) {
        File file = rootPath.resolve("assets")
                .resolve(domain)
                .resolve(ServerCustomPackLoader.SERVER_MAID_MODELS.getJsonFileName())
                .toFile();

        if (!file.isFile()) {
            return;
        }

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            load(stream, domain, file.getPath());
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Failed to load maid model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
    }

    static void load(ZipFile zipFile, String domain) {
        String path = "assets/%s/%s".formatted(domain, ServerCustomPackLoader.SERVER_MAID_MODELS.getJsonFileName());
        ZipEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            return;
        }

        try (InputStream stream = zipFile.getInputStream(entry)) {
            load(stream, domain, zipFile.getName());
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Failed to load maid model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
    }

    private static void load(InputStream stream, String domain, String sourceName) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");

        CustomModelPack<MaidModelInfo> pack = ServerCustomPackLoader.GSON.fromJson(
                new InputStreamReader(stream, StandardCharsets.UTF_8),
                new TypeToken<CustomModelPack<MaidModelInfo>>() {
                }.getType());

        if (pack == null) {
            LOGGER.warn(MARKER, "Model pack in domain {} is null, file is {}", domain, sourceName);
            return;
        }

        pack.decorate(domain);

        for (MaidModelInfo maidModelInfo : pack.getModelList()) {
            if (maidModelInfo.getEasterEgg() == null) {
                String id = maidModelInfo.getModelId().toString();
                ServerCustomPackLoader.SERVER_MAID_MODELS.putInfo(id, maidModelInfo);
                LOGGER.debug(MARKER, "Loaded model info: {}", id);
            }
        }

        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }
}
