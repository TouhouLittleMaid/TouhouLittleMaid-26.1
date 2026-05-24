package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.CubesItem;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.ChairModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierAdapter;
import com.github.tartaricacid.touhoulittlemaid.util.ZipFileCheck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

public class CustomPackLoader {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
            .registerTypeAdapter(CubesItem.class, new CubesItem.Deserializer())
            .create();

    public static final Path PACK_FOLDER = FMLPaths.GAMEDIR.get().resolve("tlm_custom_pack");

    public static final MaidModels MAID_MODELS = MaidModels.getInstance();
    public static final ChairModels CHAIR_MODELS = ChairModels.getInstance();

    private static final Marker MARKER = MarkerManager.getMarker("CustomPackLoader");

    public static void reloadPacks() {
        // 清除
        MAID_MODELS.clearAll();
        CHAIR_MODELS.clearAll();
        CustomPackTextureLoader.clear();
        LanguageLoader.clear();
        CustomSoundLoader.clear();

        // 读取
        loadPacks(PACK_FOLDER.toFile());
        LanguageLoader.loadDownloadInfoLanguages();

        // 对读取的列表进行排序，把默认模型包排在最前面
        // 其他模型包按照 namespace 字典排序
        MAID_MODELS.sortPackList();
        CHAIR_MODELS.sortPackList();
        CustomSoundLoader.sortSoundPack();
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
                        readModelFromZipFile(file);
                    } else {
                        TouhouLittleMaid.LOGGER.error("{} file is corrupt and cannot be loaded.", file.getName());
                    }
                } catch (IOException ioException) {
                    LOGGER.error(MARKER, "Failed to inspect custom pack file {}", file.getName(), ioException);
                }
            }
            if (file.isDirectory()) {
                readModelFromFolder(file);
            }
        }
    }

    public static void readModelFromFolder(File root) {
        CustomPackReader.readFolder(root);
    }

    public static void readModelFromZipFile(File file) {
        CustomPackReader.readZip(file);
    }

    public static void registerTexture(ResourceAccessor accessor, Identifier texturePath) {
        CustomPackTextureLoader.register(accessor, texturePath);
    }

    public static String assetPath(Identifier identifier) {
        return "assets/%s/%s".formatted(identifier.getNamespace(), identifier.getPath());
    }

    public static String assetPath(String domain, String fileName) {
        return "assets/%s/%s".formatted(domain, fileName);
    }
}
