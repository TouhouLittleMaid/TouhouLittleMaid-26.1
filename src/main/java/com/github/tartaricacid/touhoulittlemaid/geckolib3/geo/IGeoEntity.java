package com.github.tartaricacid.touhoulittlemaid.geckolib3.geo;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

public interface IGeoEntity {
    IMaid getMaid();

    MaidModelInfo getMaidInfo();

    void setMaidInfo(MaidModelInfo info);

    void setYsmModel(String modelId, String texture);

    void updateRoamingVars(Object2FloatOpenHashMap<String> roamingVars);
}