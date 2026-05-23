package com.github.tartaricacid.touhoulittlemaid.client.model.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.AbstractBedrockEntityModel;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.io.InputStream;

public class SimpleBedrockEntityModel<T extends EntityRenderState> extends AbstractBedrockEntityModel<T> {
    public static final BedrockPart EMPTY = new BedrockPart();

    public SimpleBedrockEntityModel() {
        super();
    }

    public SimpleBedrockEntityModel(InputStream stream) {
        super(stream);
    }

    public SimpleBedrockEntityModel(BedrockModelPOJO pojo, BedrockVersion version) {
        super(pojo, version);
    }

    public BedrockPart getPart(String partName) {
        return this.modelMap.getOrDefault(partName, EMPTY);
    }
}
