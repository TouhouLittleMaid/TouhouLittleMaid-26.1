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
     * 彩蛋模型
     */
    private final HashMap<String, ModelData> easterEggNormalTagModelMap;
    private final HashMap<String, ModelData> easterEggEncryptTagModelMap;

    private MaidModels() {
        super("maid_model.json", DefaultPackConstant.MAID_SORT);
        this.easterEggNormalTagModelMap = Maps.newHashMap();
        this.easterEggEncryptTagModelMap = Maps.newHashMap();
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
        this.easterEggNormalTagModelMap.clear();
        this.easterEggEncryptTagModelMap.clear();
    }

    public boolean containsInfo(String modelId) {
        return idInfoMap.containsKey(modelId);
    }

    public Optional<ModelData> getEasterEggNormalTagModel(String tag) {
        return Optional.ofNullable(this.easterEggNormalTagModelMap.get(tag));
    }

    public Optional<ModelData> getEasterEggEncryptTagModel(String tag) {
        return Optional.ofNullable(this.easterEggEncryptTagModelMap.get(tag));
    }

    public void putEasterEggNormalTagModel(String tag, ModelData data) {
        this.easterEggNormalTagModelMap.put(tag, data);
    }

    public void putEasterEggEncryptTagModel(String tag, ModelData data) {
        this.easterEggEncryptTagModelMap.put(tag, data);
    }

    public record ModelData(@Nullable EntityMaidModel model, MaidModelInfo info) {
    }
}
