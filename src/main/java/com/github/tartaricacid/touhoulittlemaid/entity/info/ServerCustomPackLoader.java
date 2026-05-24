package com.github.tartaricacid.touhoulittlemaid.entity.info;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.setting.SettingReader;
import com.github.tartaricacid.touhoulittlemaid.entity.info.models.ServerMaidModels;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ServerCustomPackLoader {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
            .create();

    public static final ServerMaidModels SERVER_MAID_MODELS = ServerMaidModels.getInstance();

    public static final Path PACK_FOLDER = FMLPaths.GAMEDIR.get().resolve("tlm_custom_pack");

    public static void reloadPacks() {
        SettingReader.clear();
        SERVER_MAID_MODELS.clearAll();
        ServerCustomPackReader.reload(PACK_FOLDER);
    }
}
