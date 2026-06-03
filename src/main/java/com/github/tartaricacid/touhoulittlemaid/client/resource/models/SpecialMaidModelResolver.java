package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.EasterEggModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import net.minecraft.network.chat.Component;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public final class SpecialMaidModelResolver {
    public static final String EASTER_EGG_MODEL = "touhou_little_maid:easter_egg_model";
    /**
     * ECMAScript 6 箭头函数表达式风格的前缀，不错吧
     */
    private static final String PLAYER_NAME_PREFIX = "=>";

    public static boolean resolveSpecialModel(EntityMaidRenderState state, MaidModels models) {
        if (resolveEasterEggPlaceholder(state)) {
            return true;
        }
        return resolveNamedSpecialModel(state, models);
    }

    private static boolean resolveEasterEggPlaceholder(EntityMaidRenderState state) {
        // 极为特殊的盒子模型，仅用于模型切换界面显示
        if (!EASTER_EGG_MODEL.equals(state.modelId)) {
            return false;
        }
        applyModel(
                state,
                EasterEggModel.model(),
                EasterEggModel.info(),
                Collections.emptyList()
        );
        return true;
    }

    private static boolean resolveNamedSpecialModel(EntityMaidRenderState state, MaidModels models) {
        // 命名彩蛋
        Component customName = state.customName;
        if (customName == null) {
            return false;
        }

        String name = customName.getString();
        if (!StringUtils.isNotBlank(name)) {
            return false;
        }

        if (resolvePlayerModel(state, name)) {
            return true;
        }
        return resolveEasterEggModel(state, models, name);
    }

    private static boolean resolvePlayerModel(EntityMaidRenderState state, String name) {
        // 玩家模型
        if (!name.startsWith(PLAYER_NAME_PREFIX)) {
            return false;
        }
        String playerName = name.substring(2);
        applyModel(
                state,
                PlayerMaidModels.model(playerName),
                PlayerMaidModels.info(playerName),
                PlayerMaidModels.animations()
        );
        return true;
    }

    private static boolean resolveEasterEggModel(EntityMaidRenderState state, MaidModels models, String name) {
        // 加密彩蛋
        String sha1Hex = DigestUtils.sha1Hex(name);
        var encrypted = models.getEasterEggEncryptTagModelId(sha1Hex);
        if (encrypted.isPresent()) {
            applyModel(state, models, encrypted.get());
            return true;
        }

        // 普通彩蛋
        var normal = models.getEasterEggNormalTagModelId(name);
        if (normal.isPresent()) {
            applyModel(state, models, normal.get());
            return true;
        }

        return false;
    }

    private static void applyModel(EntityMaidRenderState state, MaidModels models, String modelId) {
        models.getModel(modelId).ifPresent(model -> state.bedrockModel = model);
        models.getInfo(modelId).ifPresent(info -> state.modelInfo = info);
        models.getAnimation(modelId).ifPresent(animations -> state.animations = animations);
    }

    private static void applyModel(
            EntityMaidRenderState state,
            EntityMaidModel model,
            MaidModelInfo info,
            List<IAnimation<EntityMaidRenderState>> animations
    ) {
        state.bedrockModel = model;
        state.modelInfo = info;
        state.animations = animations;
    }
}
