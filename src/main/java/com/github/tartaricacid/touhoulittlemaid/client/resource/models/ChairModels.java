package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import org.jspecify.annotations.Nullable;

public final class ChairModels extends AbstractClientModels<EntityChairModel, ChairModelInfo, EntityChairRenderState> {
    private static @Nullable ChairModels INSTANCE;

    private ChairModels() {
        super("maid_chair.json", DefaultPackConstant.CHAIR_SORT);
    }

    public static ChairModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChairModels();
        }
        return INSTANCE;
    }

    public float getModelMountedYOffset(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).getMountedYOffset();
        }
        return 0.0f;
    }

    public boolean getModelTameableCanRide(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).isTameableCanRide();
        }
        return true;
    }

    public boolean getModelNoGravity(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).isNoGravity();
        }
        return false;
    }
}
