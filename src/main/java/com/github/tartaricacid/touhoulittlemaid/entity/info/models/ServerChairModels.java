package com.github.tartaricacid.touhoulittlemaid.entity.info.models;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import org.jspecify.annotations.Nullable;


public final class ServerChairModels extends AbstractServerModels<ChairModelInfo> {
    private static @Nullable ServerChairModels INSTANCE;

    public ServerChairModels() {
        super("maid_chair.json");
    }

    public static ServerChairModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerChairModels();
        }
        return INSTANCE;
    }
}
