package com.github.tartaricacid.touhoulittlemaid.client.model;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class PlayerMaidModel extends BedrockModel<EntityMaidRenderState> {
    private static final Identifier STEVE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "models/bedrock/entity/player_maid.json");
    private static final Identifier ALEX = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "models/bedrock/entity/player_maid_slim.json");

    public PlayerMaidModel(boolean smallArms) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (smallArms) {
            // TODO: loadNewModel(BedrockModelPOJO) 方法签名已变更，需要传入 BedrockPart 参数
            // 等待 BedrockModelLoader API 更新后恢复加载逻辑
            try (InputStream stream = manager.open(ALEX)) {
                // TODO: loadNewModel API 签名已变更，需要适配新版本的 BedrockModelPOJO 参数
                // loadNewModel(CustomPackLoader.GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class));
            } catch (IOException exception) {
                TouhouLittleMaid.LOGGER.error("Failed to load alex player maid model", exception);
            }
        } else {
            try (InputStream stream = manager.open(STEVE)) {
                // TODO: loadLegacyModel API 签名已变更，需要适配新版本的 BedrockModelPOJO 参数
                // loadLegacyModel(CustomPackLoader.GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BedrockModelPOJO.class));
            } catch (IOException exception) {
                TouhouLittleMaid.LOGGER.error("Failed to load steve player maid model", exception);
            }
        }
        // this.modelMap.forEach((key, model) -> modelMapWrapper.put(key, new ModelRendererWrapper(model)));
    }
}
