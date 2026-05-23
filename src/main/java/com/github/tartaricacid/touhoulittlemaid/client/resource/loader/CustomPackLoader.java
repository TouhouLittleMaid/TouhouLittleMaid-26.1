package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.CubesItem;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.CustomPackTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.FileResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ZipResourceAccessor;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.ChairModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierAdapter;
import com.github.tartaricacid.touhoulittlemaid.util.ZipFileCheck;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid.LOGGER;

public class CustomPackLoader {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Identifier.class, new IdentifierAdapter())
            .registerTypeAdapter(CubesItem.class, new CubesItem.Deserializer())
            .create();

    public static final Path PACK_FOLDER = Paths
            .get(Minecraft.getInstance().gameDirectory.toURI())
            .resolve("tlm_custom_pack");

    public static final MaidModels MAID_MODELS = MaidModels.getInstance();
    public static final ChairModels CHAIR_MODELS = ChairModels.getInstance();

    /**
     * 用于标记已经注册过的材质，避免反复注册同一个材质
     */
    private static final Set<Identifier> TMP_REGISTER_TEXTURE = Sets.newHashSet();

    private static final Marker MARKER = MarkerManager.getMarker("CustomPackLoader");
    private static final Pattern DOMAIN = Pattern.compile("^assets/([\\w.]+)/$");

    public static void reloadPacks() {
        // 清除
        MAID_MODELS.clearAll();
        CHAIR_MODELS.clearAll();
        TMP_REGISTER_TEXTURE.clear();
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

    public static void readModelFromZipFile(File file) {
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

    public static void registerTexture(ResourceAccessor accessor, Identifier texturePath) {
        if (!TMP_REGISTER_TEXTURE.contains(texturePath)) {
            CustomPackTexture texture = new CustomPackTexture(accessor, texturePath);
            if (texture.isExist()) {
                Minecraft.getInstance().getTextureManager().registerAndLoad(texturePath, texture);
                TMP_REGISTER_TEXTURE.add(texturePath);
            }
        }
    }

    public static String assetPath(Identifier identifier) {
        return "assets/%s/%s".formatted(identifier.getNamespace(), identifier.getPath());
    }

    public static String assetPath(String domain, String fileName) {
        return "assets/%s/%s".formatted(domain, fileName);
    }
}
