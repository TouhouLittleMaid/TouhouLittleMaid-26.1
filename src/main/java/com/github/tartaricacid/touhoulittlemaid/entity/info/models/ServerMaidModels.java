package com.github.tartaricacid.touhoulittlemaid.entity.info.models;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import org.jspecify.annotations.Nullable;

public final class ServerMaidModels extends AbstractServerModels<MaidModelInfo> {
    private static @Nullable ServerMaidModels INSTANCE;

    public ServerMaidModels() {
        super("maid_model.json");
    }

    public static ServerMaidModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerMaidModels();
        }
        return INSTANCE;
    }

    public int getModelSize() {
        return idInfoMap.size();
    }
}
