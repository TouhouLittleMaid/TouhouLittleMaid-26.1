package com.github.tartaricacid.touhoulittlemaid.client.resource.loader;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.CustomPackTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.accessor.ResourceAccessor;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.Set;

final class CustomPackTextureLoader {
    /**
     * 用于标记已经注册过的材质，避免反复注册同一个材质
     */
    private static final Set<Identifier> TMP_REGISTER_TEXTURE = Sets.newHashSet();

    static void clear() {
        TMP_REGISTER_TEXTURE.clear();
    }

    static void register(ResourceAccessor accessor, Identifier texturePath) {
        if (!TMP_REGISTER_TEXTURE.contains(texturePath)) {
            CustomPackTexture texture = new CustomPackTexture(accessor, texturePath);
            if (texture.isExist()) {
                Minecraft.getInstance().getTextureManager().registerAndLoad(texturePath, texture);
                TMP_REGISTER_TEXTURE.add(texturePath);
            }
        }
    }
}
