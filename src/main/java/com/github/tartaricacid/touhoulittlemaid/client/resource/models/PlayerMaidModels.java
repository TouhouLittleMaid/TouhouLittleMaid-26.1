package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.PlayerMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.google.common.collect.Lists;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class PlayerMaidModels {
    // TODO: GameProfile 和 Cache 相关类型需重新添加 import
    // GAME_PROFILE_CACHE 和 EMPTY_GAME_PROFILE 在新 API（PlayerSkinRenderCache）下不再需要
    private static final PlayerMaidModel PLAYER_MAID_MODEL = new PlayerMaidModel(false);
    private static final PlayerMaidModel PLAYER_MAID_MODEL_SLIM = new PlayerMaidModel(true);
    private static final List<Identifier> PLAYER_MAID_ANIMATION_RES = Lists.newArrayList(
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/default.js"),
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/head/beg.js"),
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/leg/default.js"),
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/player/arm/default.js"),
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/default/arm/swing.js"),
            Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "animation/maid/player/sit/default.js")
    );
    private static final Identifier TEXTURE_ALEX = Identifier.withDefaultNamespace("textures/entity/alex.png");
    private static final List<IAnimation<EntityMaidRenderState>> PLAYER_MAID_ANIMATIONS = Lists.newArrayList();
    private static MaidModelInfo playerMaidInfo;
    private static Identifier playerSkin;

    public static void reload() {
        PLAYER_MAID_ANIMATIONS.clear();
        for (Identifier res : PLAYER_MAID_ANIMATION_RES) {
            PLAYER_MAID_ANIMATIONS.add(InnerAnimation.get(res));
        }
        playerMaidInfo = new MaidModelInfo() {
            @Override
            public Identifier getTexture() {
                return playerSkin;
            }
        };
    }

    // TODO: getPlayerMaidModel 需要迁移至 26.1.2 新 API
    // SkullBlockEntity.fetchGameProfile(String) 和 SkinManager.getInsecureSkin(GameProfile) 已被移除。
    // 需使用 PlayerSkinRenderCache + ResolvableProfile（参考 PlayerDollItemRenderer 模式）：
    // 1. 通过 SkullBlockEntity.getOrCreateProfile() 获取 ResolvableProfile
    // 2. 通过 PlayerSkinRenderCache.getOrDefault(profile) 获取 RenderInfo
    // 3. 从 RenderInfo.playerSkin().model() 判断是否为 SLIM
    public static EntityMaidModel getPlayerMaidModel(String name) {
        // TODO: 实现新的 PlayerSkinRenderCache 获取逻辑
        return PLAYER_MAID_MODEL;
    }

    public static List<IAnimation<EntityMaidRenderState>> getPlayerMaidAnimations() {
        return PLAYER_MAID_ANIMATIONS;
    }

    // TODO: getPlayerMaidInfo 需要迁移至 26.1.2 新 API（依赖 getPlayerSkin 修复）
    public static MaidModelInfo getPlayerMaidInfo(String name) {
        // TODO: 实现新的皮肤纹理获取逻辑，通过 PlayerSkinRenderCache
        playerSkin = TEXTURE_ALEX;  // 临时回退到默认纹理
        return playerMaidInfo;
    }

    // TODO: getPlayerSkin 需要迁移至 26.1.2 新 API
    // SkullBlockEntity.fetchGameProfile(String) 和 SkinManager.getInsecureSkin(GameProfile) 已被移除。
    // 需使用 PlayerSkinRenderCache 代替 SkinManager.getInsecureSkin，
    // 使用 ResolvableProfile 代替直接 fetchGameProfile。
    public static Identifier getPlayerSkin(String name) {
        return TEXTURE_ALEX;
    }
}
