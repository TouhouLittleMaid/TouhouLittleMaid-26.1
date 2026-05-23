package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.cache.CacheIconManager;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.*;

public final class MaidModels {
    private static final String JSON_FILE_NAME = "maid_model.json";
    private static @Nullable MaidModels INSTANCE;
    private final List<CustomModelPack<MaidModelInfo>> packList;
    private final HashMap<String, BedrockModel<EntityMaidRenderState>> idModelMap;
    private final HashMap<String, MaidModelInfo> idInfoMap;
    private final HashMap<String, ModelData> easterEggNormalTagModelMap;
    private final HashMap<String, ModelData> easterEggEncryptTagModelMap;

    private MaidModels() {
        this.packList = Lists.newArrayList();
        this.idModelMap = Maps.newHashMap();
        this.idInfoMap = Maps.newHashMap();
        this.easterEggNormalTagModelMap = Maps.newHashMap();
        this.easterEggEncryptTagModelMap = Maps.newHashMap();
    }

    public static MaidModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MaidModels();
        }
        return INSTANCE;
    }

    public void clearAll() {
        this.packList.clear();
        this.idModelMap.clear();
        this.idInfoMap.clear();
        this.easterEggNormalTagModelMap.clear();
        this.easterEggEncryptTagModelMap.clear();
    }

    public String getJsonFileName() {
        return JSON_FILE_NAME;
    }

    public List<CustomModelPack<MaidModelInfo>> getPackList() {
        return packList;
    }

    public Set<String> getModelIdSet() {
        return idInfoMap.keySet();
    }

    public void addPack(CustomModelPack<MaidModelInfo> pack) {
        this.packList.add(pack);
        CacheIconManager.addMaidPack(pack);
    }

    public void putModel(String modelId, BedrockModel<EntityMaidRenderState> modelJson) {
        this.idModelMap.put(modelId, modelJson);
    }

    public void putInfo(String modelId, MaidModelInfo maidModelItem) {
        this.idInfoMap.put(modelId, maidModelItem);
    }

    public Optional<BedrockModel<EntityMaidRenderState>> getModel(String modelId) {
        return Optional.ofNullable(idModelMap.get(modelId));
    }

    public float getModelRenderItemScale(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).getRenderItemScale();
        }
        return 1.0f;
    }

    public Optional<MaidModelInfo> getInfo(String modelId) {
        return Optional.ofNullable(idInfoMap.get(modelId));
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

    public void sortPackList() {
        List<CustomModelPack<MaidModelInfo>> defaultPackList = Lists.newArrayList();
        List<CustomModelPack<MaidModelInfo>> sortPackList = Lists.newArrayList();

        // 先把默认模型查到，按顺序放进去
        for (String id : DefaultPackConstant.MAID_SORT) {
            this.packList.stream().filter(info -> info.getId().equals(id)).findFirst().ifPresent(defaultPackList::add);
        }
        // 剩余模型放进另一个里，进行字典排序
        this.packList.stream().filter(info -> !DefaultPackConstant.MAID_SORT.contains(info.getId())).forEach(sortPackList::add);
        sortPackList.sort(Comparator.comparing(CustomModelPack::getId));

        // 最后顺次放入
        this.packList.clear();
        this.packList.addAll(defaultPackList);
        this.packList.addAll(sortPackList);
    }

    public static class ModelData {
        private @Nullable BedrockModel<EntityMaidRenderState> model;
        private MaidModelInfo info;

        public ModelData(@Nullable BedrockModel<EntityMaidRenderState> model, MaidModelInfo info) {
            this.model = model;
            this.info = info;
        }

        @Nullable
        public BedrockModel<EntityMaidRenderState> getModel() {
            return model;
        }

        public void setModel(@Nullable BedrockModel<EntityMaidRenderState> model) {
            this.model = model;
        }

        public MaidModelInfo getInfo() {
            return info;
        }

        public void setInfo(MaidModelInfo info) {
            this.info = info;
        }
    }
}
