package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.PlayerMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class PlayerMaidModels {
    private static @Nullable PlayerMaidModel PLAYER_MAID_MODEL = null;
    private static @Nullable PlayerMaidModel PLAYER_MAID_MODEL_SLIM = null;

    private static List<IAnimation<EntityMaidRenderState>> PLAYER_MAID_ANIMATIONS = List.of();
    private static final List<Identifier> PLAYER_MAID_ANIMATION_IDS = Lists.newArrayList(
            IdentifierUtil.modLoc("animation/maid/default/head/default.js"),
            IdentifierUtil.modLoc("animation/maid/default/head/beg.js"),
            IdentifierUtil.modLoc("animation/maid/default/leg/default.js"),
            IdentifierUtil.modLoc("animation/maid/player/arm/default.js"),
            IdentifierUtil.modLoc("animation/maid/default/arm/swing.js"),
            IdentifierUtil.modLoc("animation/maid/player/sit/default.js")
    );

    private static final Map<String, MaidModelInfo> INFOS = Maps.newHashMap();
    private static final Function<String, ResolvableProfile> PROFILE = Util.memoize(ResolvableProfile::createUnresolved);

    public static void reload() {
        PLAYER_MAID_MODEL = null;
        PLAYER_MAID_MODEL_SLIM = null;
        INFOS.clear();
        reloadAnimations();
    }

    public static EntityMaidModel model(String name) {
        PlayerModelType type = getSkin(name).model();
        if (type == PlayerModelType.SLIM) {
            PlayerMaidModel slimModel = PLAYER_MAID_MODEL_SLIM;
            if (slimModel == null) {
                slimModel = PlayerMaidModel.create(true);
                PLAYER_MAID_MODEL_SLIM = slimModel;
            }
            return slimModel;
        } else {
            PlayerMaidModel model = PLAYER_MAID_MODEL;
            if (model == null) {
                model = PlayerMaidModel.create(false);
                PLAYER_MAID_MODEL = model;
            }
            return model;
        }
    }

    public static MaidModelInfo info(String name) {
        return INFOS.computeIfAbsent(name, s -> new MaidModelInfo() {
            @Override
            public Identifier getTexture() {
                return getSkin(s).body().texturePath();
            }
        });
    }

    private static PlayerSkin getSkin(String name) {
        PlayerSkinRenderCache cache = Minecraft.getInstance().playerSkinRenderCache();
        ResolvableProfile profile = PROFILE.apply(name);
        return cache.getOrDefault(profile).playerSkin();
    }

    public static List<IAnimation<EntityMaidRenderState>> animations() {
        return PLAYER_MAID_ANIMATIONS;
    }

    private static void reloadAnimations() {
        List<IAnimation<EntityMaidRenderState>> animations = Lists.newArrayList();
        for (Identifier animationId : PLAYER_MAID_ANIMATION_IDS) {
            if (InnerAnimation.containsKey(animationId)) {
                animations.add(InnerAnimation.get(animationId));
            }
        }
        PLAYER_MAID_ANIMATIONS = List.copyOf(animations);
    }
}
