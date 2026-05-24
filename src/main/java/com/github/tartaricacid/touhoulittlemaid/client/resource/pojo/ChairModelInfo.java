package com.github.tartaricacid.touhoulittlemaid.client.resource.pojo;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChairModelInfo implements IModelInfo {
    private static final float RENDER_ENTITY_SCALE_MIN = 0.2f;
    private static final float RENDER_ENTITY_SCALE_MAX = 2.0f;
    private static final String GECKO_ANIMATION = ".json";

    private @SerializedName("name") @Nullable String name;
    private @SerializedName("description") @Nullable List<String> description;
    private @SerializedName("model") @Nullable Identifier model;
    private @SerializedName("texture") @Nullable Identifier texture;
    private @SerializedName("extra_textures") @Nullable List<Identifier> extraTextures;
    private @SerializedName("model_id") @Nullable Identifier modelId;
    private @SerializedName("render_item_scale") float renderItemScale = 1.0f;
    private @SerializedName("render_entity_scale") float renderEntityScale = 1.0f;
    private @SerializedName("animation") @Nullable List<Identifier> animation;
    private @SerializedName("mounted_height") float mountedYOffset;
    private @SerializedName("tameable_can_ride") boolean tameableCanRide = true;
    private @SerializedName("no_gravity") boolean noGravity = false;
    private @SerializedName("is_gecko") boolean isGeckoModel = false;

    @Override
    public Identifier getTexture() {
        return Objects.requireNonNull(texture, "texture must be decorated before access");
    }

    @Override
    @Nullable
    public List<Identifier> getExtraTextures() {
        return extraTextures;
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(name, "name must be decorated before access");
    }

    @Override
    public List<String> getDescription() {
        return Objects.requireNonNull(description, "description must be decorated before access");
    }

    @Override
    @Nullable
    public List<Identifier> getAnimation() {
        return animation;
    }

    @Override
    public Identifier getModelId() {
        return Objects.requireNonNull(modelId, "modelId must be decorated before access");
    }

    @Override
    public Identifier getModel() {
        return Objects.requireNonNull(model, "model must be decorated before access");
    }

    @Override
    public boolean isGeckoModel() {
        return isGeckoModel;
    }

    public float getMountedYOffset() {
        return mountedYOffset;
    }

    public boolean isTameableCanRide() {
        return tameableCanRide;
    }

    @Override
    public float getRenderItemScale() {
        return renderItemScale;
    }

    public float getRenderEntityScale() {
        return renderEntityScale;
    }

    public boolean isNoGravity() {
        return noGravity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChairModelInfo extra(Identifier newModelId, Identifier texture) {
        ChairModelInfo cloneInfo = new ChairModelInfo();
        cloneInfo.modelId = newModelId;
        cloneInfo.texture = texture;
        cloneInfo.name = this.name;
        cloneInfo.description = this.description;
        cloneInfo.model = this.model;
        cloneInfo.renderItemScale = this.renderItemScale;
        cloneInfo.renderEntityScale = this.renderEntityScale;
        cloneInfo.animation = this.animation;
        cloneInfo.mountedYOffset = this.mountedYOffset;
        cloneInfo.tameableCanRide = this.tameableCanRide;
        cloneInfo.noGravity = this.noGravity;
        cloneInfo.isGeckoModel = this.isGeckoModel;
        return cloneInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChairModelInfo decorate() {
        // description 设置为空列表
        if (description == null) {
            description = Collections.EMPTY_LIST;
        }
        // 如果 model_id 为空，抛出异常
        if (modelId == null) {
            throw new JsonSyntaxException("Expected \"model_id\" in model");
        }
        // 如果 model 或 texture 为空，自动生成默认位置的模型
        if (model == null) {
            model = Identifier.fromNamespaceAndPath(modelId.getNamespace(), "models/entity/" + modelId.getPath() + ".json");
        }
        if (texture == null) {
            texture = Identifier.fromNamespaceAndPath(modelId.getNamespace(), "textures/entity/" + modelId.getPath() + ".png");
        }
        // 如果名称为空，自动生成本地化名称
        if (name == null) {
            name = String.format("{model.%s.%s.name}", modelId.getNamespace(), modelId.getPath());
        }
        if (isGeckoModel) {
            if (animation == null || animation.isEmpty()) {
                animation = ReferenceLists.emptyList();
            } else {
                animation = animation.stream().filter(res -> res.getPath().endsWith(GECKO_ANIMATION)).collect(Collectors.toList());
            }
        }
        renderEntityScale = Mth.clamp(renderEntityScale, RENDER_ENTITY_SCALE_MIN, RENDER_ENTITY_SCALE_MAX);
        // 将写入的高度转换为游戏内部的高度
        mountedYOffset = (mountedYOffset - 3) * 0.0625f;
        return this;
    }
}
