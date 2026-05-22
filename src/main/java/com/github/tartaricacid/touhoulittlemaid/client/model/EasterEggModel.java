package com.github.tartaricacid.touhoulittlemaid.client.model;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.LegacyModelReader;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.script.ModelRendererWrapper;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EasterEggModel extends BedrockModel<EntityMaidRenderState> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "models/bedrock/entity/easter_egg_model.json");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/bedrock/entity/easter_egg_model.png");
    private static EasterEggModel INSTANCE;
    private static MaidModelInfo INFO;

    public EasterEggModel() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (InputStream stream = manager.open(MODEL)) {
            LegacyModelReader.load(CustomPackLoader.GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class));
        } catch (IOException exception) {
            TouhouLittleMaid.LOGGER.error("Failed to load easter egg model", exception);
        }
        this.modelMap.forEach((key, model) -> modelMapWrapper.put(key, new ModelRendererWrapper(model)));
    }

    public static EasterEggModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EasterEggModel();
        }
        return INSTANCE;
    }

    public static MaidModelInfo getInfo() {
        if (INFO == null) {
            INFO = new MaidModelInfo() {
                @Override
                public Identifier getTexture() {
                    return TEXTURE;
                }
            };
        }
        return INFO;
    }
}
