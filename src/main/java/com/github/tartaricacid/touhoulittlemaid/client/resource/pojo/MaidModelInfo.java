package com.github.tartaricacid.touhoulittlemaid.client.resource.pojo;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.google.common.collect.Lists;
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

public class MaidModelInfo implements IModelInfo {
    public static final String ENCRYPT_EGG_NAME = "{gui.touhou_little_maid.model_gui.easter_egg.encrypt}";
    public static final String NORMAL_EGG_NAME = "{gui.touhou_little_maid.model_gui.easter_egg.normal}";

    private static final float RENDER_ENTITY_SCALE_MIN = 0.2f;
    private static final float RENDER_ENTITY_SCALE_MAX = 2.0f;

    private static final String GECKO_ANIMATION = ".json";

    private @SerializedName("name") @Nullable String name;
    private @SerializedName("description") @Nullable List<String> description;
    private @SerializedName("model") @Nullable Identifier model;
    private @SerializedName("texture") @Nullable Identifier texture;
    private @SerializedName("extra_textures") @Nullable List<Identifier> extraTextures;
    private @SerializedName("model_id") @Nullable Identifier modelId;
    private @SerializedName("use_sound_pack_id") @Nullable String useSoundPackId;
    private @SerializedName("render_item_scale") float renderItemScale = 1.0f;
    private @SerializedName("render_entity_scale") float renderEntityScale = 1.0f;
    private @SerializedName("animation") @Nullable List<Identifier> animation;
    private @SerializedName("show_backpack") boolean showBackpack = true;
    private @SerializedName("show_custom_head") boolean showCustomHead = true;
    private @SerializedName("easter_egg") @Nullable EasterEgg easterEgg = null;
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

    @Nullable
    public String getUseSoundPackId() {
        return useSoundPackId;
    }

    @Override
    public Identifier getModel() {
        return Objects.requireNonNull(model, "model must be decorated before access");
    }

    @Override
    public boolean isGeckoModel() {
        return isGeckoModel;
    }

    @Override
    public float getRenderItemScale() {
        return renderItemScale;
    }

    public float getRenderEntityScale() {
        return renderEntityScale;
    }

    public boolean isShowBackpack() {
        return showBackpack;
    }

    public boolean isShowCustomHead() {
        return showCustomHead;
    }

    @Nullable
    public EasterEgg getEasterEgg() {
        return easterEgg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MaidModelInfo extra(Identifier newModelId, Identifier texture) {
        MaidModelInfo cloneInfo = new MaidModelInfo();
        cloneInfo.modelId = newModelId;
        cloneInfo.texture = texture;
        cloneInfo.name = this.name;
        cloneInfo.description = this.description;
        cloneInfo.model = this.model;
        cloneInfo.useSoundPackId = this.useSoundPackId;
        cloneInfo.renderItemScale = this.renderItemScale;
        cloneInfo.renderEntityScale = this.renderEntityScale;
        cloneInfo.animation = this.animation;
        cloneInfo.showBackpack = this.showBackpack;
        cloneInfo.showCustomHead = this.showCustomHead;
        cloneInfo.easterEgg = this.easterEgg;
        cloneInfo.isGeckoModel = this.isGeckoModel;
        return cloneInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MaidModelInfo decorate() {
        // description 设置为空列表
        if (description == null) {
            description = Collections.emptyList();
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
        // 彩蛋
        if (easterEgg != null) {
            if (easterEgg.isEncrypt()) {
                name = ENCRYPT_EGG_NAME;
            } else {
                name = NORMAL_EGG_NAME;
            }
        }
        // 如果名称为空，自动生成本地化名称
        if (name == null) {
            name = String.format("{model.%s.%s.name}", modelId.getNamespace(), modelId.getPath());
        }
        if (isGeckoModel) {
            if (animation == null || animation.isEmpty()) {
                animation = ReferenceLists.emptyList();
            } else {
                animation = animation.stream()
                        .filter(res -> res.getPath().endsWith(GECKO_ANIMATION))
                        .collect(Collectors.toList());
            }
        } else {
            if (animation == null || animation.isEmpty()) {
                animation = Lists.newArrayList(
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/blink.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/beg.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/music_shake.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/leg/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/arm/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/arm/swing.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/arm/vertical.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/sit/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/armor/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/armor/reverse.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/wing/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/tail/default.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/sit/skirt_rotation.js"),
                        Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/base/float/default.js")
                );
            }
        }
        renderEntityScale = Mth.clamp(renderEntityScale, RENDER_ENTITY_SCALE_MIN, RENDER_ENTITY_SCALE_MAX);
        return this;
    }

    public static class EasterEgg {
        private @SerializedName("encrypt") boolean encrypt = false;
        private @SerializedName("tag") String tag = "";

        public boolean isEncrypt() {
            return encrypt;
        }

        public String getTag() {
            return tag;
        }
    }
}
