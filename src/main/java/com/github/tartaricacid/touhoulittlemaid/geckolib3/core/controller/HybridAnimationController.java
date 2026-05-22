package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.controller.IAnimationPredicate;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;

import java.util.List;
import java.util.function.Consumer;

public class HybridAnimationController<T extends AnimatableEntity<?>> implements IAnimationController<T> {
    private final String name;
    private final T animatableEntity;
    private final CodedAnimationController<T> codedAnimationController;
    private final BedrockAnimationController<T> bedrockAnimationController;

    private boolean isBedrock;
    private IAnimationController<T> activeController;

    public HybridAnimationController(T animatableEntity, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate) {
        this(animatableEntity, name, transitionLengthTicks, animationPredicate, false);
    }

    @Deprecated
    public HybridAnimationController(T animatableEntity, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate, boolean blendRotation) {
        this.name = name;
        this.animatableEntity = animatableEntity;
        this.codedAnimationController = new CodedAnimationController<>(animatableEntity, name, transitionLengthTicks, animationPredicate, blendRotation);
        this.bedrockAnimationController = new BedrockAnimationController<>(animatableEntity, name, transitionLengthTicks);
        this.activeController = this.codedAnimationController;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getStateName() {
        if (isBedrock) {
            return this.bedrockAnimationController.isBuiltinState() ? ("[builtin] " + this.codedAnimationController.getStateName()) : this.bedrockAnimationController.getStateName();
        } else {
            return this.codedAnimationController.getStateName();
        }
    }

    @Override
    public void updateModel(List<BoneTopLevelSnapshot> modelBones, Object2ReferenceMap<String, List<IValue>> eventHandlers) {
        var animationControllerData = animatableEntity.getAnimationControllerData(this.name);
        if (animationControllerData != null) {
            this.isBedrock = true;
            this.bedrockAnimationController.updateModel(modelBones, animationControllerData);
            this.codedAnimationController.updateModel(modelBones, eventHandlers);
            this.activeController = this.bedrockAnimationController;
        } else {
            this.isBedrock = false;
            this.codedAnimationController.updateModel(modelBones, eventHandlers);
            this.bedrockAnimationController.clear();
            this.activeController = this.codedAnimationController;
        }
    }

    @Override
    public void process(AnimationEvent<T> event, ExpressionEvaluator<MolangContext<?>> evaluator, boolean allowEmitting) {
        if (this.isBedrock) {
            this.bedrockAnimationController.process(event, evaluator, allowEmitting);
            if (this.bedrockAnimationController.isBuiltinState()) {
                if (this.activeController != this.codedAnimationController) {
                    this.codedAnimationController.setBeginningTransition(this.bedrockAnimationController.getStateData().blendTransition().startNew());
                    this.activeController = this.codedAnimationController;
                }
                this.codedAnimationController.process(event, evaluator, allowEmitting);
            } else if (this.activeController != this.bedrockAnimationController) {
                this.codedAnimationController.finalizeAnimationContext(evaluator);
                this.codedAnimationController.reset();
                this.activeController = this.bedrockAnimationController;
            }
        } else {
            this.codedAnimationController.process(event, evaluator, allowEmitting);
        }
    }

    @Override
    public void visitBoneAnimationQueues(Consumer<IBoneAnimationQueue> visitor) {
        this.activeController.visitBoneAnimationQueues(visitor);
    }

    @Override
    public boolean blendRotation() {
        return this.activeController.blendRotation();
    }

    @Override
    public void clear() {
        if (isBedrock) {
            this.bedrockAnimationController.clear();
            this.codedAnimationController.clear();
        } else {
            this.codedAnimationController.clear();
        }
    }
}
