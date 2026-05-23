package com.github.tartaricacid.touhoulittlemaid.client.resource;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.CubesItem;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.cache.CacheIconManager;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.FilePackTexture;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.ZipPackTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.ChairModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.resource.GeckoContainer;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierAdapter;
import com.github.tartaricacid.touhoulittlemaid.util.ZipFileCheck;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
    public static final MaidModels MAID_MODELS = MaidModels.getInstance();
    public static final ChairModels CHAIR_MODELS = ChairModels.getInstance();
    private static final Set<Identifier> TMP_REGISTER_TEXTURE = Sets.newHashSet();
    private static final String CUSTOM_PACK_DIR_NAME = "tlm_custom_pack";
    public static final Path PACK_FOLDER = Paths.get(Minecraft.getInstance().gameDirectory.toURI()).resolve(CUSTOM_PACK_DIR_NAME);
    private static final Marker MARKER = MarkerManager.getMarker("CustomPackLoader");
    private static final Pattern DOMAIN = Pattern.compile("^assets/([\\w.]+)/$");

    public static void reloadPacks() {
        // 清除
        MAID_MODELS.clearAll();
        CHAIR_MODELS.clearAll();
        TMP_REGISTER_TEXTURE.clear();
        LanguageLoader.clear();
        CustomSoundLoader.clear();
        CacheIconManager.clearCache();

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
            File[] domainFiles = root.toPath().resolve("assets").toFile().listFiles((dir, name) -> true);
            if (domainFiles == null) {
                return;
            }
            for (File domainDir : domainFiles) {
                if (domainDir.isDirectory()) {
                    Path rootPath = root.toPath();
                    String domain = domainDir.getName();
                    loadMaidModelPack(rootPath, domain);
                    loadChairModelPack(rootPath, domain);
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
                    String domain = matcher.group(1);
                    loadMaidModelPack(zipFile, domain);
                    loadChairModelPack(zipFile, domain);
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

    private static void loadMaidModelPack(Path rootPath, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");
        File file = rootPath.resolve("assets").resolve(domain).resolve(MAID_MODELS.getJsonFileName()).toFile();
        if (!file.isFile()) {
            return;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            CustomModelPack<MaidModelInfo> pack = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<MaidModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            // 加载图标贴图
            if (pack.getIcon() != null) {
                registerFilePackTexture(rootPath, pack.getIcon());
            }
            for (MaidModelInfo maidModelItem : pack.getModelList()) {
                if (maidModelItem.isGeckoModel()) {
                    loadGeckoMaidModelElement(rootPath, maidModelItem);
                } else {
                    loadMaidModelElement(rootPath, maidModelItem);
                }
            }
            MAID_MODELS.addPack(pack);
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Failed to load maid model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }

    private static void loadMaidModelElement(Path rootPath, MaidModelInfo maidModelItem) {
        // 尝试加载模型
        EntityMaidModel modelJson = loadMaidModel(rootPath, maidModelItem.getModel());
        // 加载贴图
        registerFilePackTexture(rootPath, maidModelItem.getTexture());
        if (modelJson != null) {
            // 加载彩蛋，彩蛋不允许为空
            if (maidModelItem.getEasterEgg() != null && StringUtils.isNotBlank(maidModelItem.getEasterEgg().getTag())) {
                putMaidEasterEggData(maidModelItem, modelJson);
            } else {
                putMaidModelData(maidModelItem, modelJson);
            }
            // 打印日志
            LOGGER.debug(MARKER, "Loaded model: {}", maidModelItem.getModel());
        }
    }

    private static void loadGeckoMaidModelElement(Path rootPath, MaidModelInfo maidModelItem) throws IOException {
        loadGeckoModelElement(rootPath, maidModelItem, GeckoContainer.Type.MAID);
        if (maidModelItem.getEasterEgg() != null && StringUtils.isNotBlank(maidModelItem.getEasterEgg().getTag())) {
            putMaidEasterEggData(maidModelItem, null);
        } else {
            MAID_MODELS.putInfo(maidModelItem.getModelId().toString(), maidModelItem);
        }
    }

    private static void loadGeckoChairModelElement(Path rootPath, ChairModelInfo chairModelItem) throws IOException {
        loadGeckoModelElement(rootPath, chairModelItem, GeckoContainer.Type.CHAIR);
        CHAIR_MODELS.putInfo(chairModelItem.getModelId().toString(), chairModelItem);
    }

    private static void loadGeckoModelElement(Path rootPath, IModelInfo maidModelItem, GeckoContainer.Type type) throws IOException {
        Identifier uid = maidModelItem.getModelId();
        // 尝试加载模型
        Identifier modelLocation = maidModelItem.getModel();
        File modelFile = rootPath.resolve("assets").resolve(modelLocation.getNamespace()).resolve(modelLocation.getPath()).toFile();
        if (!modelFile.isFile()) {
            return;
        }
        // 加载贴图
        registerFilePackTexture(rootPath, maidModelItem.getTexture());
        GeckoContainerBuilder.registerModelContainer(uid, () -> Files.newInputStream(modelFile.toPath()),
                id -> {
                    File animationFile = rootPath.resolve("assets").resolve(id.getNamespace()).resolve(id.getPath()).toFile();
                    if (!animationFile.isFile()) {
                        return null;
                    }
                    return Files.newInputStream(animationFile.toPath());
                },
                maidModelItem.getAnimation(),
                maidModelItem.getTexture(),
                type);
        // 打印日志
        LOGGER.debug(MARKER, "Loaded model: {}", maidModelItem.getModel());
    }

    private static void loadMaidModelPack(ZipFile zipFile, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");
        ZipEntry entry = zipFile.getEntry(String.format("assets/%s/%s", domain, MAID_MODELS.getJsonFileName()));
        if (entry == null) {
            return;
        }
        try (InputStream stream = zipFile.getInputStream(entry)) {
            CustomModelPack<MaidModelInfo> pack = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<MaidModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            // 加载图标贴图
            if (pack.getIcon() != null) {
                registerZipPackTexture(zipFile.getName(), pack.getIcon());
            }
            for (MaidModelInfo maidModelItem : pack.getModelList()) {
                if (maidModelItem.isGeckoModel()) {
                    loadGeckoMaidModelElement(zipFile, maidModelItem);
                } else {
                    loadMaidModelElement(zipFile, maidModelItem);
                }
            }
            MAID_MODELS.addPack(pack);
        } catch (IOException e) {
            LOGGER.warn(MARKER, "Failed to load maid model pack in domain {}", domain, e);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }

    private static void loadMaidModelElement(ZipFile zipFile, MaidModelInfo maidModelItem) {
        // 尝试加载模型
        EntityMaidModel modelJson = loadMaidModel(zipFile, maidModelItem.getModel());
        // 加载贴图
        registerZipPackTexture(zipFile.getName(), maidModelItem.getTexture());
        if (modelJson != null) {
            // 加载彩蛋，彩蛋不允许为空
            if (maidModelItem.getEasterEgg() != null && StringUtils.isNotBlank(maidModelItem.getEasterEgg().getTag())) {
                putMaidEasterEggData(maidModelItem, modelJson);
            } else {
                putMaidModelData(maidModelItem, modelJson);
            }
            // 打印日志
            LOGGER.debug(MARKER, "Loaded model: {}", maidModelItem.getModel());
        }
    }

    private static void loadGeckoMaidModelElement(ZipFile zipFile, MaidModelInfo maidModelItem) throws IOException {
        loadGeckoModelElement(zipFile, maidModelItem, GeckoContainer.Type.MAID);
        if (maidModelItem.getEasterEgg() != null && StringUtils.isNotBlank(maidModelItem.getEasterEgg().getTag())) {
            putMaidEasterEggData(maidModelItem, null);
        } else {
            MAID_MODELS.putInfo(maidModelItem.getModelId().toString(), maidModelItem);
        }
    }

    private static void loadGeckoChairModelElement(ZipFile zipFile, ChairModelInfo chairModelItem) throws IOException {
        loadGeckoModelElement(zipFile, chairModelItem, GeckoContainer.Type.CHAIR);
        CHAIR_MODELS.putInfo(chairModelItem.getModelId().toString(), chairModelItem);
    }

    private static void loadGeckoModelElement(ZipFile zipFile, IModelInfo maidModelItem, GeckoContainer.Type type) throws IOException {
        Identifier uid = maidModelItem.getModelId();
        // 尝试加载模型
        Identifier modelLocation = maidModelItem.getModel();
        String path = String.format("assets/%s/%s", modelLocation.getNamespace(), modelLocation.getPath());
        ZipEntry modelZipEntry = zipFile.getEntry(path);
        if (modelZipEntry == null) {
            return;
        }
        // 加载贴图
        registerZipPackTexture(zipFile.getName(), maidModelItem.getTexture());
        GeckoContainerBuilder.registerModelContainer(uid, () -> zipFile.getInputStream(modelZipEntry),
                id -> {
                    ZipEntry animationZipEntry = zipFile.getEntry(String.format("assets/%s/%s", id.getNamespace(), id.getPath()));
                    if (animationZipEntry == null) {
                        return null;
                    }
                    return zipFile.getInputStream(animationZipEntry);
                },
                maidModelItem.getAnimation(),
                maidModelItem.getTexture(),
                type);
        // 打印日志
        LOGGER.debug(MARKER, "Loaded model: {}", maidModelItem.getModel());
    }

    @SuppressWarnings("all")
    private static void putMaidEasterEggData(MaidModelInfo maidModelItem, @Nullable EntityMaidModel modelJson) {
        MaidModelInfo.EasterEgg easterEgg = maidModelItem.getEasterEgg();
        MAID_MODELS.putAnimation(maidModelItem.getModelId().toString(), resolveMaidAnimations(maidModelItem));
        MaidModels.ModelData data = new MaidModels.ModelData(modelJson, maidModelItem);
        if (easterEgg.isEncrypt()) {
            MAID_MODELS.putEasterEggEncryptTagModel(easterEgg.getTag(), data);
        } else {
            MAID_MODELS.putEasterEggNormalTagModel(easterEgg.getTag(), data);
        }
    }

    private static void putMaidModelData(MaidModelInfo maidModelItem, EntityMaidModel modelJson) {
        String id = maidModelItem.getModelId().toString();
        // 如果加载的模型不为空
        MAID_MODELS.putModel(id, modelJson);
        MAID_MODELS.putAnimation(id, resolveMaidAnimations(maidModelItem));
        MAID_MODELS.putInfo(id, maidModelItem);
    }

    private static void loadChairModelPack(Path rootPath, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");
        File file = rootPath.resolve("assets").resolve(domain).resolve(CHAIR_MODELS.getJsonFileName()).toFile();
        if (!file.isFile()) {
            return;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            // 将其转换为 pojo 对象
            // 这个 pojo 是二次修饰的过的对象，所以一部分数据异常已经进行了处理或者抛出
            CustomModelPack<ChairModelInfo> pack = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<ChairModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            // 加载图标贴图
            if (pack.getIcon() != null) {
                registerFilePackTexture(rootPath, pack.getIcon());
            }
            for (ChairModelInfo chairModelItem : pack.getModelList()) {
                if (chairModelItem.isGeckoModel()) {
                    loadGeckoChairModelElement(rootPath, chairModelItem);
                } else {
                    loadChairModelElement(rootPath, chairModelItem);
                }
            }
            CHAIR_MODELS.addPack(pack);
        } catch (IOException ignore) {
            // 忽略错误，因为资源域很多
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }

    private static void loadChairModelElement(Path rootPath, ChairModelInfo chairModelItem) {
        // 尝试加载模型
        EntityChairModel modelJson = loadChairModel(rootPath, chairModelItem.getModel());
        // 加载贴图
        registerFilePackTexture(rootPath, chairModelItem.getTexture());
        if (modelJson != null) {
            String id = chairModelItem.getModelId().toString();
            // 如果加载的模型不为空
            CHAIR_MODELS.putModel(id, modelJson);
            CHAIR_MODELS.putAnimation(id, resolveChairAnimations(chairModelItem));
            CHAIR_MODELS.putInfo(id, chairModelItem);
            // 打印日志
            LOGGER.debug(MARKER, "Loaded model: {}", chairModelItem.getModel());
        }
    }

    private static void loadChairModelPack(ZipFile zipFile, String domain) {
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loading...");
        ZipEntry entry = zipFile.getEntry(String.format("assets/%s/%s", domain, CHAIR_MODELS.getJsonFileName()));
        if (entry == null) {
            return;
        }
        try (InputStream stream = zipFile.getInputStream(entry)) {
            // 将其转换为 pojo 对象
            // 这个 pojo 是二次修饰的过的对象，所以一部分数据异常已经进行了处理或者抛出
            CustomModelPack<ChairModelInfo> pack = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<CustomModelPack<ChairModelInfo>>() {
                    }.getType());
            pack.decorate(domain);
            // 加载图标贴图
            if (pack.getIcon() != null) {
                registerZipPackTexture(zipFile.getName(), pack.getIcon());
            }
            for (ChairModelInfo chairModelItem : pack.getModelList()) {
                if (chairModelItem.isGeckoModel()) {
                    loadGeckoChairModelElement(zipFile, chairModelItem);
                } else {
                    loadChairModelElement(zipFile, chairModelItem);
                }
            }
            CHAIR_MODELS.addPack(pack);
        } catch (IOException ignore) {
            // 忽略错误，因为资源域很多
        } catch (JsonSyntaxException e) {
            LOGGER.warn(MARKER, "Fail to parse model pack in domain {}", domain, e);
        }
        LOGGER.debug(MARKER, "Touhou little maid mod's model is loaded");
    }

    private static void loadChairModelElement(ZipFile zipFile, ChairModelInfo chairModelItem) {
        // 尝试加载模型
        EntityChairModel modelJson = loadChairModel(zipFile, chairModelItem.getModel());
        // 加载贴图
        registerZipPackTexture(zipFile.getName(), chairModelItem.getTexture());
        if (modelJson != null) {
            String id = chairModelItem.getModelId().toString();
            // 如果加载的模型不为空
            CHAIR_MODELS.putModel(id, modelJson);
            CHAIR_MODELS.putAnimation(id, resolveChairAnimations(chairModelItem));
            CHAIR_MODELS.putInfo(id, chairModelItem);
            // 打印日志
            LOGGER.debug(MARKER, "Loaded model: {}", chairModelItem.getModel());
        }
    }

    @Nullable
    public static EntityMaidModel loadMaidModel(Path rootPath, Identifier modelLocation) {
        File file = rootPath.resolve("assets").resolve(modelLocation.getNamespace()).resolve(modelLocation.getPath()).toFile();
        if (!file.isFile()) {
            return null;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            BedrockModelPOJO pojo = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class);
            // 先判断是不是 1.10.0 版本基岩版模型文件
            if (BedrockVersion.isLegacyVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelLegacy() != null) {
                    return new EntityMaidModel(pojo, BedrockVersion.LEGACY);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            // 判定是不是 1.12.0 版本基岩版模型文件
            if (BedrockVersion.isNewVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelNew() != null) {
                    return new EntityMaidModel(pojo, BedrockVersion.NEW);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            LOGGER.warn(MARKER, "{} model version is not 1.10.0 or 1.12.0", modelLocation);
        } catch (IOException ioe) {
            // 可能用来判定错误，打印下
            LOGGER.warn(MARKER, "Failed to load model: {}", modelLocation, ioe);
        }
        // 如果前面出了错，返回 Null
        return null;
    }

    @Nullable
    public static EntityMaidModel loadMaidModel(ZipFile zipFile, Identifier modelLocation) {
        String path = String.format("assets/%s/%s", modelLocation.getNamespace(), modelLocation.getPath());
        ZipEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            return null;
        }
        try (InputStream stream = zipFile.getInputStream(entry)) {
            BedrockModelPOJO pojo = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class);
            // 先判断是不是 1.10.0 版本基岩版模型文件
            if (BedrockVersion.isLegacyVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelLegacy() != null) {
                    return new EntityMaidModel(pojo, BedrockVersion.LEGACY);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            // 判定是不是 1.12.0 版本基岩版模型文件
            if (BedrockVersion.isNewVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelNew() != null) {
                    return new EntityMaidModel(pojo, BedrockVersion.NEW);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            LOGGER.warn(MARKER, "{} model version is not 1.10.0 or 1.12.0", modelLocation);
        } catch (IOException ioe) {
            // 可能用来判定错误，打印下
            LOGGER.warn(MARKER, "Failed to load model: {}", modelLocation, ioe);
        }
        // 如果前面出了错，返回 Null
        return null;
    }

    @Nullable
    public static EntityChairModel loadChairModel(Path rootPath, Identifier modelLocation) {
        File file = rootPath.resolve("assets").resolve(modelLocation.getNamespace()).resolve(modelLocation.getPath()).toFile();
        if (!file.isFile()) {
            return null;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            BedrockModelPOJO pojo = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class);
            // 先判断是不是 1.10.0 版本基岩版模型文件
            if (BedrockVersion.isLegacyVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelLegacy() != null) {
                    return new EntityChairModel(pojo, BedrockVersion.LEGACY);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            // 判定是不是 1.12.0 版本基岩版模型文件
            if (BedrockVersion.isNewVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelNew() != null) {
                    return new EntityChairModel(pojo, BedrockVersion.NEW);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            LOGGER.warn(MARKER, "{} model version is not 1.10.0 or 1.12.0", modelLocation);
        } catch (IOException ioe) {
            // 可能用来判定错误，打印下
            LOGGER.warn(MARKER, "Failed to load model: {}", modelLocation, ioe);
        }
        // 如果前面出了错，返回 Null
        return null;
    }

    @Nullable
    public static EntityChairModel loadChairModel(ZipFile zipFile, Identifier modelLocation) {
        String path = String.format("assets/%s/%s", modelLocation.getNamespace(), modelLocation.getPath());
        ZipEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            return null;
        }
        try (InputStream stream = zipFile.getInputStream(entry)) {
            BedrockModelPOJO pojo = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class);
            // 先判断是不是 1.10.0 版本基岩版模型文件
            if (BedrockVersion.isLegacyVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelLegacy() != null) {
                    return new EntityChairModel(pojo, BedrockVersion.LEGACY);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            // 判定是不是 1.12.0 版本基岩版模型文件
            if (BedrockVersion.isNewVersion(pojo)) {
                // 如果 model 字段不为空
                if (pojo.getGeometryModelNew() != null) {
                    return new EntityChairModel(pojo, BedrockVersion.NEW);
                } else {
                    // 否则日志给出提示
                    LOGGER.warn(MARKER, "{} model file don't have model field", modelLocation);
                    return null;
                }
            }

            LOGGER.warn(MARKER, "{} model version is not 1.10.0 or 1.12.0", modelLocation);
        } catch (IOException ioe) {
            // 可能用来判定错误，打印下
            LOGGER.warn(MARKER, "Failed to load model: {}", modelLocation, ioe);
        }
        // 如果前面出了错，返回 Null
        return null;
    }

    public static void registerFilePackTexture(Path rootPath, Identifier texturePath) {
        if (!TMP_REGISTER_TEXTURE.contains(texturePath)) {
            FilePackTexture filePackTexture = new FilePackTexture(rootPath, texturePath);
            if (filePackTexture.isExist()) {
                Minecraft.getInstance().getTextureManager().registerAndLoad(texturePath, filePackTexture);
                TMP_REGISTER_TEXTURE.add(texturePath);
            }
        }
    }

    public static void registerZipPackTexture(String zipFilePath, Identifier texturePath) {
        if (!TMP_REGISTER_TEXTURE.contains(texturePath)) {
            ZipPackTexture zipPackTexture = new ZipPackTexture(zipFilePath, texturePath);
            if (zipPackTexture.isExist()) {
                Minecraft.getInstance().getTextureManager().registerAndLoad(texturePath, zipPackTexture);
                TMP_REGISTER_TEXTURE.add(texturePath);
            }
        }
    }

    private static List<IAnimation<EntityMaidRenderState>> resolveMaidAnimations(MaidModelInfo maidModelItem) {
        List<IAnimation<EntityMaidRenderState>> animations = new ArrayList<>();
        List<Identifier> animationIds = maidModelItem.getAnimation();
        if (animationIds == null || animationIds.isEmpty()) {
            return animations;
        }
        for (Identifier animationId : animationIds) {
            if (InnerAnimation.containsKey(animationId)) {
                animations.add(InnerAnimation.get(animationId));
            }
        }
        return animations;
    }

    private static List<IAnimation<EntityChairRenderState>> resolveChairAnimations(ChairModelInfo chairModelItem) {
        List<IAnimation<EntityChairRenderState>> animations = new ArrayList<>();
        List<Identifier> animationIds = chairModelItem.getAnimation();
        if (animationIds == null || animationIds.isEmpty()) {
            return animations;
        }
        for (Identifier animationId : animationIds) {
            if (InnerAnimation.containsKey(animationId)) {
                animations.add(InnerAnimation.get(animationId));
            }
        }
        return animations;
    }
}
