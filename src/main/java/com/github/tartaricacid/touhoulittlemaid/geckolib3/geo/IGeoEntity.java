package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

public interface IGeoEntity {
    EntityMaid getMaid();

    MaidModelInfo getMaidInfo();

    void setMaidInfo(MaidModelInfo info);

    void setYsmModel(String modelId, String texture);

    void updateRoamingVars(Object2FloatOpenHashMap<String> roamingVars);
}