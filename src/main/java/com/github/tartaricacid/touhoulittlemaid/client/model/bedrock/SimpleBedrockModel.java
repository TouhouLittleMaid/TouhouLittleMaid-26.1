package com.github.tartaricacid.touhoulittlemaid.client.model.bedrock;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.AbstractBedrockModel;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockModelPOJO;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.pojo.BedrockVersion;

import java.io.InputStream;

public class SimpleBedrockModel<T> extends AbstractBedrockModel<T> {
    public static final BedrockPart EMPTY = new BedrockPart();

    public SimpleBedrockModel() {
        super();
    }

    public SimpleBedrockModel(InputStream stream) {
        super(stream);
    }

    public SimpleBedrockModel(BedrockModelPOJO pojo, BedrockVersion version) {
        super(pojo, version);
    }

    public BedrockPart getPart(String partName) {
        return this.modelMap.getOrDefault(partName, EMPTY);
    }
}
