package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityMaidModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

public final class MaidModels extends AbstractClientModels<EntityMaidModel, MaidModelInfo, EntityMaidRenderState> {
    private static @Nullable MaidModels INSTANCE;
    /**
     * 彩蛋模型，用彩蛋的 tag 命中某个已有模型
     */
    private final HashMap<String, String> easterEggNormalTagModelIdMap;
    private final HashMap<String, String> easterEggEncryptTagModelIdMap;

    private MaidModels() {
        super("maid_model.json", DefaultPackConstant.MAID_SORT);
        this.easterEggNormalTagModelIdMap = Maps.newHashMap();
        this.easterEggEncryptTagModelIdMap = Maps.newHashMap();
    }

    public static MaidModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MaidModels();
        }
        return INSTANCE;
    }

    @Override
    public void clearAll() {
        super.clearAll();
        this.easterEggNormalTagModelIdMap.clear();
        this.easterEggEncryptTagModelIdMap.clear();
    }

    public boolean containsInfo(String modelId) {
        return idInfoMap.containsKey(modelId);
    }

    public Optional<String> getEasterEggNormalTagModelId(String tag) {
        return Optional.ofNullable(this.easterEggNormalTagModelIdMap.get(tag));
    }

    public Optional<String> getEasterEggEncryptTagModelId(String tag) {
        return Optional.ofNullable(this.easterEggEncryptTagModelIdMap.get(tag));
    }

    public void putEasterEggNormalTagModelId(String tag, String modelId) {
        this.easterEggNormalTagModelIdMap.put(tag, modelId);
    }

    public void putEasterEggEncryptTagModelId(String tag, String modelId) {
        this.easterEggEncryptTagModelIdMap.put(tag, modelId);
    }
}
