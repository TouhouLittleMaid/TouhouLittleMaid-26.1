package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.cache.CacheIconManager;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityChairRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ChairModels {
    private static final String JSON_FILE_NAME = "maid_chair.json";
    private static @Nullable ChairModels INSTANCE;
    private final List<CustomModelPack<ChairModelInfo>> packList;
    private final HashMap<String, EntityChairModel> idModelMap;
    private final HashMap<String, List<IAnimation<EntityChairRenderState>>> idAnimationMap;
    private final HashMap<String, ChairModelInfo> idInfoMap;

    private ChairModels() {
        this.packList = Lists.newArrayList();
        this.idModelMap = Maps.newHashMap();
        this.idAnimationMap = Maps.newHashMap();
        this.idInfoMap = Maps.newHashMap();
    }

    public static ChairModels getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChairModels();
        }
        return INSTANCE;
    }

    public void clearAll() {
        this.packList.clear();
        this.idModelMap.clear();
        this.idAnimationMap.clear();
        this.idInfoMap.clear();
    }

    public String getJsonFileName() {
        return JSON_FILE_NAME;
    }

    public List<CustomModelPack<ChairModelInfo>> getPackList() {
        return packList;
    }

    public Set<String> getModelIdSet() {
        return idInfoMap.keySet();
    }

    public void addPack(CustomModelPack<ChairModelInfo> pack) {
        this.packList.add(pack);
        CacheIconManager.addChairPack(pack);
    }

    public void putModel(String modelId, EntityChairModel modelJson) {
        this.idModelMap.put(modelId, modelJson);
    }

    public void putInfo(String modelId, ChairModelInfo chairModelItem) {
        this.idInfoMap.put(modelId, chairModelItem);
    }

    public void putAnimation(String modelId, List<IAnimation<EntityChairRenderState>> animations) {
        this.idAnimationMap.put(modelId, animations);
    }

    public void sortPackList() {
        List<CustomModelPack<ChairModelInfo>> defaultPackList = Lists.newArrayList();
        List<CustomModelPack<ChairModelInfo>> sortPackList = Lists.newArrayList();

        // 先把默认模型查到，按顺序放进去
        for (String id : DefaultPackConstant.CHAIR_SORT) {
            this.packList.stream().filter(info -> info.getId().equals(id)).findFirst().ifPresent(defaultPackList::add);
        }
        // 剩余模型放进另一个里，进行字典排序
        this.packList.stream().filter(info -> !DefaultPackConstant.CHAIR_SORT.contains(info.getId())).forEach(sortPackList::add);
        sortPackList.sort(Comparator.comparing(CustomModelPack::getId));

        // 最后顺次放入
        this.packList.clear();
        this.packList.addAll(defaultPackList);
        this.packList.addAll(sortPackList);
    }

    public Optional<EntityChairModel> getModel(String modelId) {
        return Optional.ofNullable(idModelMap.get(modelId));
    }

    public float getModelRenderItemScale(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).getRenderItemScale();
        }
        return 1.0f;
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

    public Optional<ChairModelInfo> getInfo(String modelId) {
        return Optional.ofNullable(idInfoMap.get(modelId));
    }

    public Optional<List<IAnimation<EntityChairRenderState>>> getAnimation(String modelId) {
        return Optional.ofNullable(idAnimationMap.get(modelId));
    }
}
