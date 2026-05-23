package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.model.EasterEggModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.network.chat.Component;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public final class SpecialMaidModelResolver {
    public static final String EASTER_EGG_MODEL = "touhou_little_maid:easter_egg_model";
    /**
     * ECMAScript 6 箭头函数表达式风格的前缀，不错吧
     */
    private static final String PLAYER_NAME_PREFIX = "=>";

    public static Optional<MaidModels.ModelData> resolveSpecialModel(EntityMaidRenderState state, MaidModels models) {
        Component customName = state.customName;
        if (customName == null) {
            return Optional.empty();
        }

        String name = customName.getString();
        if (!StringUtils.isNotBlank(name)) {
            return Optional.empty();
        }

        // 玩家模型
        if (name.startsWith(PLAYER_NAME_PREFIX)) {
            String playerName = name.substring(2);
            return Optional.of(new MaidModels.ModelData(
                    PlayerMaidModels.getPlayerMaidModel(playerName),
                    PlayerMaidModels.getPlayerMaidInfo(playerName)));
        }

        // 加密彩蛋
        String sha1Hex = DigestUtils.sha1Hex(name);
        Optional<MaidModels.ModelData> encrypted = models.getEasterEggEncryptTagModel(sha1Hex);
        if (encrypted.isPresent()) {
            return encrypted;
        }

        // 普通彩蛋
        Optional<MaidModels.ModelData> normal = models.getEasterEggNormalTagModel(name);
        if (normal.isPresent()) {
            return normal;
        }

        // 极为特殊的盒子模型，仅用于模型切换界面显示
        if (EASTER_EGG_MODEL.equals(state.modelId)) {
            MaidModels.ModelData modelData = new MaidModels.ModelData(
                    EasterEggModel.getInstance(),
                    EasterEggModel.getInfo()
            );
            return Optional.of(modelData);
        }

        return Optional.empty();
    }
}
