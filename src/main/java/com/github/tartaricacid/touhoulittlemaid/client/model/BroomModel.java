package com.github.tartaricacid.touhoulittlemaid.client.model;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockEntityModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityBroomRenderState;

import java.io.InputStream;

public class BroomModel extends SimpleBedrockEntityModel<EntityBroomRenderState> {
    private final BedrockPart all;

    public BroomModel(InputStream stream) {
        super(stream);
        this.all = this.getPart("all");
    }

    @Override
    public void setupAnim(EntityBroomRenderState state) {
        float headPitch = state.xRot;
        float netHeadYaw = state.yRot;
        all.yRot = netHeadYaw * ((float) Math.PI / 180F);
        if (state.isVehicle) {
            all.xRot = headPitch * ((float) Math.PI / 180F) / 10;
        }
    }
}
