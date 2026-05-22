package com.github.tartaricacid.touhoulittlemaid.client.entity;

import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;

import java.util.function.Consumer;

public class GeckoChairEntity extends AnimatableEntity<EntityChair> {
    private ChairModelInfo chairInfo;

    public GeckoChairEntity(EntityChair entity) {
        super(entity, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onSetupAnimationController() {
        var container = getGeckoContainer();
        if (container != null) {
            ((Consumer<GeckoChairEntity>) container.controllerFactory()).accept(this);
        }
    }

    public ChairModelInfo getChairInfo() {
        return chairInfo;
    }

    public void setChair(ChairModelInfo chairInfo) {
        this.chairInfo = chairInfo;
    }
}
