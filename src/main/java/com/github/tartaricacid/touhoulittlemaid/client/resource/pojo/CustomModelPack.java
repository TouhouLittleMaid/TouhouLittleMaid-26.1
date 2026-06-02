package com.github.tartaricacid.touhoulittlemaid.client.resource.pojo;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.util.Md5Utils;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CustomModelPack<T extends IModelInfo> {
    private @Expose(serialize = false, deserialize = false) String id = "";
    private @SerializedName("date") @Nullable String date;
    private @SerializedName("model_list") @Nullable List<T> modelList;
    private @SerializedName("pack_name") @Nullable String packName;
    private @SerializedName("author") @Nullable List<String> author;
    private @SerializedName("description") @Nullable List<String> description;
    private @SerializedName("version") @Nullable String version;
    private @SerializedName("icon") @Nullable Identifier icon;
    private @SerializedName("icon_delay") int iconDelay = 2;
    private @Expose(deserialize = false) AnimationState iconAnimation = AnimationState.UNCHECK;
    private @Expose(deserialize = false) int iconAspectRatio = 1;

    public String getId() {
        return id;
    }

    @Nullable
    public String getDate() {
        return date;
    }

    public List<T> getModelList() {
        return Objects.requireNonNull(modelList, "modelList must be decorated before access");
    }

    public String getPackName() {
        return Objects.requireNonNull(packName, "packName must be decorated before access");
    }

    public List<String> getAuthor() {
        return Objects.requireNonNull(author, "author must be decorated before access");
    }

    public List<String> getDescription() {
        return Objects.requireNonNull(description, "description must be decorated before access");
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @Nullable
    public Identifier getIcon() {
        return icon;
    }

    public AnimationState getIconAnimation() {
        return iconAnimation;
    }

    public void setIconAnimation(AnimationState iconAnimation) {
        this.iconAnimation = iconAnimation;
    }

    public int getIconAspectRatio() {
        return iconAspectRatio;
    }

    public void setIconAspectRatio(int iconAspectRatio) {
        this.iconAspectRatio = iconAspectRatio;
    }

    public int getIconDelay() {
        return iconDelay;
    }

    @SuppressWarnings("unchecked")
    public CustomModelPack<T> decorate(String id) {
        // 必须传入 ID
        if (StringUtils.isBlank(id)) {
            throw new RuntimeException("pack id must not be empty");
        }
        this.id = id;

        // 包名和 model list 不能为空
        if (packName == null) {
            throw new JsonSyntaxException("Expected \"pack_name\" in pack");
        }
        if (modelList == null || modelList.isEmpty()) {
            throw new JsonSyntaxException("Expected \"model_list\" in pack");
        }
        if (description == null) {
            description = Collections.EMPTY_LIST;
        }
        if (author == null) {
            author = Collections.EMPTY_LIST;
        }
        if (icon == null) {
            icon = IdentifierUtil.modLoc("textures/gui/empty_model_pack_icon.png");
        }
        if (iconDelay <= 0) {
            iconDelay = 1;
        }

        // 为此包的模型对象进行二次修饰
        modelList.forEach(T::decorate);

        // 多材质模型拆分
        List<T> newModelList = Lists.newArrayList();
        for (T item : modelList) {
            Identifier modelId = item.getModelId();
            newModelList.add(item.extra(modelId, item.getTexture()));
            List<Identifier> extraTextures = item.getExtraTextures();
            if (extraTextures != null && !extraTextures.isEmpty()) {
                extraTextures.forEach(r -> {
                    String suffix = Md5Utils.md5Hex(r.getPath()).toLowerCase(Locale.US);
                    Identifier newModelId = Identifier.fromNamespaceAndPath(modelId.getNamespace(), modelId.getPath() + "_" + suffix);
                    newModelList.add(item.extra(newModelId, r));
                });
            }
        }
        modelList = newModelList;

        return this;
    }

    public enum AnimationState {
        // 拥有动画
        TRUE,
        // 没有动画
        FALSE,
        // 还未检查其是否拥有动画
        UNCHECK
    }
}
