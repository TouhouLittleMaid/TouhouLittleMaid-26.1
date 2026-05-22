package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;

import java.util.List;
import java.util.function.Consumer;

public interface IAnimationController<T extends AnimatableEntity<?>> {
    /**
     * 获取动画控制器名称
     */
    String getName();

    /**
     * 获取控制器当时所处状态
     */
    String getStateName();

    /**
     * 更新模型
     */
    void updateModel(List<BoneTopLevelSnapshot> modelRendererList, Object2ReferenceMap<String, List<IValue>> eventHandlers);

    void process(AnimationEvent<T> event, ExpressionEvaluator<MolangContext<?>> evaluator, boolean allowEmitting);

    void visitBoneAnimationQueues(Consumer<IBoneAnimationQueue> visitor);

    /**
     * 这个参数是为了处理一个 YSM 遗留问题
     * <p>
     * 曾经某个版本的 YSM 加入了并行动画的混合功能，但是仅混合动画旋转数值
     *
     * @return 如果是 CodedAnimationController，那么仅并行动画返回 true
     * <p>
     * 如果使用动画控制器，那么将永远返回 false
     */
    @Deprecated
    default boolean blendRotation() {
        return false;
    }

    void clear();
}
