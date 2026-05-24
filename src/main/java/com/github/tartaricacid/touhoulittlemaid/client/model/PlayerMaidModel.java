package com.github.tartaricacid.touhoulittlemaid.client.model;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class PlayerMaidModel extends EntityMaidModel {
    private static final Identifier STEVE = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "models/bedrock/entity/player_maid.json");
    private static final Identifier ALEX = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "models/bedrock/entity/player_maid_slim.json");

    public PlayerMaidModel(InputStream stream) {
        super(stream);
    }

    public PlayerMaidModel() {
    }

    public static PlayerMaidModel create(boolean smallArms) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (InputStream stream = manager.open(smallArms ? ALEX : STEVE)) {
            return new PlayerMaidModel(stream);
        } catch (IOException e) {
            TouhouLittleMaid.LOGGER.error("Failed to load player maid model", e);
        }
        // 不太可能触发
        return new PlayerMaidModel();
    }
}
