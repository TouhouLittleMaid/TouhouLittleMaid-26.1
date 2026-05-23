package com.github.tartaricacid.touhoulittlemaid.entity.info.models;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractServerModels<I extends IModelInfo> {
    protected final String jsonFileName;
    protected final HashMap<String, I> idInfoMap;

    protected AbstractServerModels(String jsonFileName) {
        this.jsonFileName = jsonFileName;
        this.idInfoMap = Maps.newHashMap();
    }

    public void clearAll() {
        idInfoMap.clear();
    }

    public void putInfo(String modelId, I info) {
        this.idInfoMap.put(modelId, info);
    }

    public Optional<I> getInfo(String modelId) {
        return Optional.ofNullable(idInfoMap.get(modelId));
    }

    public boolean containsInfo(String modelId) {
        return idInfoMap.containsKey(modelId);
    }

    public Set<String> getModelIdSet() {
        return idInfoMap.keySet();
    }

    public String getJsonFileName() {
        return jsonFileName;
    }
}
