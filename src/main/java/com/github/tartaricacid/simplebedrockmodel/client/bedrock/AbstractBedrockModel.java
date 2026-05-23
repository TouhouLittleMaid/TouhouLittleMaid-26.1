package com.github.tartaricacid.simplebedrockmodel.client.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 将基岩版实体模型文件读取为 Java 版的 net.minecraft.client.model.Model 模型，此类和 AbstractBedrockEntityModel 一样
 * <p>
 * 但由于 net.minecraft.client.model.EntityModel 和 net.minecraft.client.model.Model 是继承关系，无法复用，故重复代码
 */
public abstract class AbstractBedrockModel<T> extends Model<T> implements BedrockModelProvider {
    /**
     * 存储 BedrockPart 的 HashMap
     */
    protected final HashMap<String, BedrockPart> modelMap = new HashMap<>();
    /**
     * 模型的 AABB
     */
    protected AABB renderBoundingBox;

    public AbstractBedrockModel(InputStream stream) {
        BedrockPart root = new BedrockPart();
        Pair<HashMap<String, BedrockPart>, AABB> result = null;

        BedrockModelPOJO pojo = BedrockModelUtil.GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class);
        if (BedrockVersion.isLegacyVersion(pojo)) {
            result = LegacyModelReader.load(pojo, root);
        }
        if (BedrockVersion.isNewVersion(pojo)) {
            result = NewModelReader.load(pojo, root);
        }

        super(root, RenderTypes::entityCutout);
        if (result != null) {
            modelMap.putAll(result.getLeft());
            renderBoundingBox = result.getRight();
        } else {
            renderBoundingBox = new AABB(-1, 0, -1, 1, 2, 1);
        }
    }

    public AbstractBedrockModel(BedrockModelPOJO pojo, BedrockVersion version) {
        BedrockPart root = new BedrockPart();
        Pair<HashMap<String, BedrockPart>, AABB> result = null;
        if (version == BedrockVersion.LEGACY) {
            result = LegacyModelReader.load(pojo, root);
        }
        if (version == BedrockVersion.NEW) {
            result = NewModelReader.load(pojo, root);
        }

        super(root, RenderTypes::entityCutout);
        if (result != null) {
            modelMap.putAll(result.getLeft());
            renderBoundingBox = result.getRight();
        } else {
            renderBoundingBox = new AABB(-1, 0, -1, 1, 2, 1);
        }
    }

    public AbstractBedrockModel(BedrockModelPOJO pojo) throws InvalidVersionSpecificationException {
        this(pojo, BedrockVersion.getVersion(pojo));
    }

    public AbstractBedrockModel() {
        super(new BedrockPart(), RenderTypes::entityCutout);
        renderBoundingBox = new AABB(-1, 0, -1, 1, 2, 1);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return renderBoundingBox;
    }

    @Override
    public HashMap<String, BedrockPart> getModelMap() {
        return modelMap;
    }
}