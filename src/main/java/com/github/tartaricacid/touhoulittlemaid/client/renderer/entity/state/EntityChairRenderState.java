package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.ChairModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 椅子渲染状态
 */
public class EntityChairRenderState extends LivingEntityRenderState {
    public ModelType modelType = ModelType.NONE;

    public ChairModelInfo chairInfo;
    @Nullable
    public BedrockModel<EntityChairRenderState> bedrockModel;
    public List<Object> chairAnimations = ObjectLists.emptyList();

    @Nullable
    public AABB hitbox;

    public EntityChair chair;   // TODO

    public boolean hasPassenger;
    public float passengerXRot;
    public float passengerYRot;

    public void clear() {
        modelType = ModelType.NONE;

        chairInfo = null;
        bedrockModel = null;
        chairAnimations = ObjectLists.emptyList();

        hitbox = null;

        chair = null;
    }
}
