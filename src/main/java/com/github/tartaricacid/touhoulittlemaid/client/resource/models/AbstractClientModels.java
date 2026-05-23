package com.github.tartaricacid.touhoulittlemaid.client.resource.models;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.*;

public abstract class AbstractClientModels<M, I extends IModelInfo, A extends EntityRenderState> {
    protected final String jsonFileName;
    protected final List<String> defaultSortOrder;
    protected final List<CustomModelPack<I>> packList;
    protected final HashMap<String, M> idModelMap;
    protected final HashMap<String, List<IAnimation<A>>> idAnimationMap;
    protected final HashMap<String, I> idInfoMap;

    protected AbstractClientModels(String jsonFileName, List<String> defaultSortOrder) {
        this.jsonFileName = jsonFileName;
        this.defaultSortOrder = defaultSortOrder;
        this.packList = Lists.newArrayList();
        this.idModelMap = Maps.newHashMap();
        this.idAnimationMap = Maps.newHashMap();
        this.idInfoMap = Maps.newHashMap();
    }

    public void clearAll() {
        this.packList.clear();
        this.idModelMap.clear();
        this.idAnimationMap.clear();
        this.idInfoMap.clear();
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public List<CustomModelPack<I>> getPackList() {
        return packList;
    }

    public Set<String> getModelIdSet() {
        return idInfoMap.keySet();
    }

    public void addPack(CustomModelPack<I> pack) {
        this.packList.add(pack);
    }

    public void putModel(String modelId, M modelJson) {
        this.idModelMap.put(modelId, modelJson);
    }

    public void putInfo(String modelId, I info) {
        this.idInfoMap.put(modelId, info);
    }

    public void putAnimation(String modelId, List<IAnimation<A>> animations) {
        this.idAnimationMap.put(modelId, animations);
    }

    public Optional<M> getModel(String modelId) {
        return Optional.ofNullable(idModelMap.get(modelId));
    }

    public float getModelRenderItemScale(String modelId) {
        if (idInfoMap.containsKey(modelId)) {
            return idInfoMap.get(modelId).getRenderItemScale();
        }
        return 1.0f;
    }

    public Optional<I> getInfo(String modelId) {
        return Optional.ofNullable(idInfoMap.get(modelId));
    }

    public Optional<List<IAnimation<A>>> getAnimation(String modelId) {
        return Optional.ofNullable(idAnimationMap.get(modelId));
    }

    public void sortPackList() {
        List<CustomModelPack<I>> defaultPackList = Lists.newArrayList();
        List<CustomModelPack<I>> sortPackList = Lists.newArrayList();

        // 先把默认模型查到，按顺序放进去
        for (String id : defaultSortOrder) {
            this.packList.stream().filter(info -> info.getId().equals(id)).findFirst().ifPresent(defaultPackList::add);
        }
        // 剩余模型放进另一个里，进行字典排序
        this.packList.stream().filter(info -> !defaultSortOrder.contains(info.getId())).forEach(sortPackList::add);
        sortPackList.sort(Comparator.comparing(CustomModelPack::getId));

        // 最后顺次放入
        this.packList.clear();
        this.packList.addAll(defaultPackList);
        this.packList.addAll(sortPackList);
    }
}
