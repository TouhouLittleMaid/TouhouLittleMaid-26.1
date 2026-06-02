package com.github.tartaricacid.touhoulittlemaid.client.model;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class EasterEggModel extends EntityMaidModel {
    private static final Identifier MODEL_PATH = IdentifierUtil.modLoc("models/bedrock/entity/easter_egg_model.json");
    private static final Identifier TEXTURE_PATH = IdentifierUtil.modLoc("textures/bedrock/entity/easter_egg_model.png");

    private static @Nullable EasterEggModel MODEL;
    private static @Nullable MaidModelInfo INFO;

    public EasterEggModel(InputStream stream) {
        super(stream);
    }

    public EasterEggModel() {
    }

    public static EasterEggModel model() {
        if (MODEL != null) {
            return MODEL;
        }
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (InputStream stream = manager.open(MODEL_PATH)) {
            MODEL = new EasterEggModel(stream);
        } catch (IOException e) {
            TouhouLittleMaid.LOGGER.error("Failed to load player maid model", e);
            // 不太可能触发
            MODEL = new EasterEggModel();
        }
        return MODEL;
    }

    public static MaidModelInfo info() {
        if (INFO != null) {
            return INFO;
        }
        INFO = new MaidModelInfo() {
            @Override
            public Identifier getTexture() {
                return TEXTURE_PATH;
            }
        };
        return INFO;
    }
}
