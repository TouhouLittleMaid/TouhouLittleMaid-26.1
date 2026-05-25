package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.EntityChairModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.GeckoUpdateTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeckoRenderData;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 椅子渲染状态
 */
public class EntityChairRenderState extends LivingEntityRenderState {
    public ModelType modelType = ModelType.NONE;

    public ChairModelInfo chairInfo;
    public EntityChairModel bedrockModel;
    public List<IAnimation<EntityChairRenderState>> chairAnimations = Collections.emptyList();

    @Nullable
    public AABB hitbox;

    public boolean hasPassenger;
    public float passengerXRot;
    public float passengerYRot;

    public GeckoUpdateTask<GeckoRenderData> geckoUpdateTask;

    public void clear() {
        modelType = ModelType.NONE;

        chairInfo = null;
        bedrockModel = null;
        chairAnimations = Collections.emptyList();

        hitbox = null;

        hasPassenger = false;
        passengerXRot = 0;
        passengerYRot = 0;

        geckoUpdateTask = null;
    }
}
