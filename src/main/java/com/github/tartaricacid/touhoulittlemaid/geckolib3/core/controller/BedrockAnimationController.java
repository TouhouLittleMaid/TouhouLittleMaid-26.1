package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller.AnimationControllerData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller.AnimationControllerState;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.*;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point.BeginningTransitionPoint;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.point.EndingTransitionPoint;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.ControllerContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.MathUtil;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class BedrockAnimationController<T extends AnimatableEntity<?>> implements IAnimationController<T> {
    private static final int MAX_HIERARCHY_DEPTH = 5;

    private final T animatableEntity;
    private final String name;
    private final float initTransitionLengthTicks;
    /**
     * 与动画控制器相关的 molang 上下文
     */
    private final ControllerContext ctx;

    @Nullable
    private List<BoneTopLevelSnapshot> modelBones;
    @Nullable
    private AnimationControllerData data;
    @Nullable
    private AnimationControllerState state;
    @Nullable
    private String stateName;

    private final ReferenceArrayList<AnimationPlayerHolder> animationPlayers = new ReferenceArrayList<>(8);
    private int activeAnimationPlayerSize = 0;

    private final Int2ReferenceOpenHashMap<BlendBoneAnimationQueue> blendAnimationQueues = new Int2ReferenceOpenHashMap<>(64);
    private final ReferenceArrayList<BlendBoneAnimationQueue> activeBlendAnimationQueues = new ReferenceArrayList<>(16);
    private boolean isActiveQueuesDirty = false;

    @Nullable
    private String hierarchy;
    private int hierarchyDepth;
    @Nullable
    private BedrockAnimationController<T> subController;
    private final IntOpenHashSet skipPathSet = new IntOpenHashSet(4);

    /**
     * 实例化基岩版动画控制器 <br>
     * 你可以为一个实体附加多个动画控制器 <br>
     * 比如一个控制器控制实体大小，另一个控制移动，攻击等等
     *
     * @param animatableEntity      实体
     * @param name                  动画控制器名称
     * @param transitionLengthTicks 默认动画过渡时间（tick）
     */
    public BedrockAnimationController(T animatableEntity, String name, float transitionLengthTicks) {
        this.animatableEntity = animatableEntity;
        this.name = name;
        this.initTransitionLengthTicks = transitionLengthTicks;
        this.hierarchy = null;
        this.hierarchyDepth = 1;
        this.ctx = new ControllerContext(true);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void process(AnimationEvent<T> event, ExpressionEvaluator<MolangContext<?>> evaluator, boolean allowEmitting) {
        if (this.data == null) {
            return;
        }

        evaluator.entity().setAnimationContext(null);
        evaluator.entity().setControllerContext(ctx);
        var renderTicks = event.getRenderTicks();

        // 转换到空状态时连续跳转
        skipPathSet.clear();
        var update = false;
        while (true) {
            if (updateState(evaluator)) {
                update = true;
                if (this.activeAnimationPlayerSize == 0) {
                    continue;
                }
            }
            break;
        }

        if (state != null && state.subEntryName() != null && hierarchyDepth != MAX_HIERARCHY_DEPTH) {
            if (update) {
                var subHierarchy = hierarchyDepth > 1 ? String.format("%s.%s", hierarchy, state.subEntryName()) : state.subEntryName();
                var subData = this.animatableEntity.getAnimationControllerData(String.format("%s.%s", this.name, subHierarchy));
                if (subData != null) {
                    if (subController == null) {
                        subController = new BedrockAnimationController<>(this.animatableEntity, name, initTransitionLengthTicks);
                        subController.updateModel(modelBones, subData);
                    } else {
                        subController.updateControllerData(subData);
                    }
                    subController.setHierarchy(subHierarchy, hierarchyDepth + 1);
                }
            }
            if (subController != null) {
                subController.process(event, evaluator, allowEmitting);
            }
            return;
        }

        // 更新动画
        for (var i = 0; i < this.activeAnimationPlayerSize; i++) {
            var holder = this.animationPlayers.get(i);
            holder.conditionHolder().evaluateApplyCondition(evaluator);
            holder.animationPlayer().process(renderTicks, evaluator, allowEmitting && holder.conditionHolder().shouldApply());
        }

        if (isActiveQueuesDirty) {
            for (var i = 0; i < this.activeAnimationPlayerSize; i++) {
                var holder = this.animationPlayers.get(i);
                for (var queue : holder.animationPlayer.getActiveBoneAnimQueues()) {
                    var blendQueue = blendAnimationQueues.get(queue.topLevelSnapshot.name);
                    if (!blendQueue.isActive()) {
                        blendQueue.setActive();
                        activeBlendAnimationQueues.add(blendQueue);
                    }
                    blendQueue.addUnderlyingQueue(holder.conditionHolder, queue);
                }
            }
            isActiveQueuesDirty = false;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getStateName() {
        if (state != null) {
            if (state.subEntryName() != null && subController != null) {
                return subController.getStateName();
            }
            return stateName;
        }
        return "(null)";
    }

    private void setStateName(String name) {
        if (hierarchyDepth > 1) {
            stateName = String.format("[%s] %s", hierarchy, name);
        } else {
            stateName = name;
        }
    }

    @Nullable
    public AnimationControllerState getStateData() {
        return this.state;
    }

    public boolean isBuiltinState() {
        return state != null && state.isBuiltin();
    }

    @Override
    public void updateModel(List<BoneTopLevelSnapshot> modelBones, Object2ReferenceMap<String, List<IValue>> eventHandlers) {
        var data = this.animatableEntity.getAnimationControllerData(this.name);
        if (data != null) {
            this.updateModel(modelBones, data);
        } else {
            this.clear();
        }
    }

    public void updateModel(List<BoneTopLevelSnapshot> modelBones, @NotNull AnimationControllerData animationControllerData) {
        this.clear();

        this.data = animationControllerData;
        for (var bone : modelBones) {
            this.blendAnimationQueues.put(bone.name, new BlendBoneAnimationQueue(bone));
        }
        this.modelBones = modelBones;
    }

    private void updateControllerData(AnimationControllerData animationControllerData) {
        this.data = animationControllerData;
    }

    private void setHierarchy(String hierarchy, int hierarchyDepth) {
        this.hierarchy = hierarchy;
        this.hierarchyDepth = hierarchyDepth;
    }

    private boolean updateState(ExpressionEvaluator<MolangContext<?>> evaluator) {
        if (this.state == null) {
            // 初始化默认状态
            var stateName = this.data.initialState();
            var initialState = this.data.states().get(stateName);
            if (initialState == null) {
                return false;
            }
            skipPathSet.add(initialState.pooledName());
            setStateName(initialState.name());
            transition(initialState, evaluator);
            return true;
        } else {
            // 更新当前状态
            int appliedController = 0;
            ctx.setAnyAnimationFinished(false);
            ctx.setAllAnimationsFinished(true);
            for (var i = 0; i < this.activeAnimationPlayerSize; i++) {
                var holder = this.animationPlayers.get(i);
                if (holder.conditionHolder.shouldApply()) {
                    appliedController++;
                    if (holder.animationPlayer.currentAnimFinished()) {
                        ctx.setAnyAnimationFinished(true);
                    } else {
                        ctx.setAllAnimationsFinished(false);
                    }
                }
            }
            if (appliedController == 0) {
                ctx.setAnyAnimationFinished(true);
            }

            for (var transition : state.transitions()) {
                if (!transition.right().evalAsBoolean(evaluator)) {
                    continue;
                }
                var stateName = transition.leftInt();
                var newState = this.data.states().get(stateName);
                if (newState == null || !skipPathSet.add(newState.pooledName())) {
                    return false;
                }
                setStateName(newState.name());
                transition(newState, evaluator);
                return true;
            }
        }
        return false;
    }

    private void transition(@Nullable AnimationControllerState newState, ExpressionEvaluator<MolangContext<?>> evaluator) {
        if (newState == null && this.state == null) {
            return;
        }
        ctx.soundManager().stopAllPlayingSounds();

        evaluator.entity().setAllowEmitting(true);
        if (this.state != null) {
            if (this.state.subEntryName() != null && this.subController != null) {
                this.subController.transition(null, evaluator);
            }
            for (var exp : this.state.onExit()) {
                exp.eval(evaluator);
            }
        }
        if (newState != null) {
            for (var exp : newState.onEntry()) {
                exp.eval(evaluator);
            }
            for (var se : newState.soundEffects()) {
                if (!StringUtils.isNoneBlank(se)) {
                    ctx.soundManager().playSound(evaluator.entity().animatableEntity(), 0, se, false, null);
                }
            }
        }
        evaluator.entity().setAllowEmitting(false);
        this.state = newState;
        for (var queue : this.activeBlendAnimationQueues) {
            queue.setInactive();
        }
        this.activeBlendAnimationQueues.clear();
        this.isActiveQueuesDirty = true;

        var animationSize = newState == null || newState.isBuiltin() || newState.subEntryName() != null ? 0 : newState.animations().size();

        // 扩容动画播放器列表
        for (var i = this.animationPlayers.size(); i < animationSize; i++) {
            this.animationPlayers.add(new AnimationPlayerHolder(this.animatableEntity, this.initTransitionLengthTicks));
        }
        // 停用多余的动画播放器
        for (var i = animationSize; i < this.activeAnimationPlayerSize; i++) {
            var player = this.animationPlayers.get(i).animationPlayer;
            player.finalizeAnimationContext(evaluator);
            player.reset();
        }
        // 初始化动画播放器
        this.activeAnimationPlayerSize = animationSize;
        for (var i = 0; i < animationSize; i++) {
            var holder = this.animationPlayers.get(i);
            var animPair = newState.animations().get(i);

            if (holder.isDirty()) {
                holder.animationPlayer().updateModel(this.modelBones);
                holder.clearDirty();
            }

            holder.conditionHolder().setApplyCondition(animPair.getRight());
            holder.animationPlayer().finalizeAnimationContext(evaluator);
            holder.animationPlayer().setBeginningTransition(newState.blendTransition().startNew());
            holder.animationPlayer().indicateReload();
            holder.animationPlayer().setAnimation(animPair.getLeft());
        }
    }

    @Override
    public void visitBoneAnimationQueues(Consumer<IBoneAnimationQueue> visitor) {
        if (this.state != null) {
            if (this.state.subEntryName() != null && this.subController != null) {
                this.subController.visitBoneAnimationQueues(visitor);
            } else {
                for (var queue : this.activeBlendAnimationQueues) {
                    if (queue.shouldApply()) {
                        visitor.accept(queue);
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        this.modelBones = ReferenceLists.emptyList();
        this.data = null;
        this.state = null;
        this.activeAnimationPlayerSize = 0;
        this.activeBlendAnimationQueues.clear();
        this.blendAnimationQueues.clear();
        if (this.hierarchyDepth == 1) {
            this.subController = null;
        }
        for (var holder : this.animationPlayers) {
            holder.animationPlayer.clear();
        }
        this.animationPlayers.clear();
        this.ctx.soundManager().stopAllPlayingSounds();
    }

    private static class AnimationPlayerHolder {
        private final ConditionHolder conditionHolder;
        private final AnimationPlayer animationPlayer;
        private boolean dirty;

        private AnimationPlayerHolder(AnimatableEntity<?> animatableEntity, float transitionLengthTicks) {
            conditionHolder = new ConditionHolder();
            animationPlayer = new AnimationPlayer(animatableEntity, transitionLengthTicks);
            dirty = true;
        }

        public ConditionHolder conditionHolder() {
            return conditionHolder;
        }

        public AnimationPlayer animationPlayer() {
            return animationPlayer;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void markAsDirty() {
            dirty = true;
        }

        public void clearDirty() {
            dirty = false;
        }
    }

    private static class ConditionHolder {
        @Nullable
        private IValue applyCondition;
        private boolean result;

        public ConditionHolder() {
            result = true;
        }

        public void setApplyCondition(@Nullable IValue applyCondition) {
            this.applyCondition = applyCondition;
            if (applyCondition == null) {
                result = true;
            }
        }

        public void evaluateApplyCondition(ExpressionEvaluator<?> evaluator) {
            if (applyCondition != null) {
                result = applyCondition.evalAsBoolean(evaluator);
            }
        }

        public boolean shouldApply() {
            return result;
        }
    }

    /**
     * 在对过渡动画进行混合时假设所有活跃的动画播放器具有相同的过渡时间和起始点，并且同时开始过渡，
     * 实际上也理应如此。
     */
    private static class BlendBoneAnimationQueue implements IBoneAnimationQueue {
        private final BoneTopLevelSnapshot snapshot;
        private final ReferenceArrayList<Pair<ConditionHolder, BoneAnimationQueue>> underlyingQueues;
        private boolean active;

        public BlendBoneAnimationQueue(BoneTopLevelSnapshot snapshot) {
            this.snapshot = snapshot;
            this.underlyingQueues = new ReferenceArrayList<>(4);
        }

        public int boneName() {
            return snapshot.name;
        }

        public void addUnderlyingQueue(ConditionHolder conditionHolder, BoneAnimationQueue queue) {
            this.underlyingQueues.add(Pair.of(conditionHolder, queue));
        }

        public boolean shouldApply() {
            if (this.underlyingQueues.isEmpty()) {
                return false;
            }
            for (var pair : this.underlyingQueues) {
                if (pair.left().shouldApply()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive() {
            this.active = true;
        }

        public void setInactive() {
            active = false;
            underlyingQueues.clear();
        }

        @Override
        public BoneTopLevelSnapshot getSnapshot() {
            return snapshot;
        }

        @Override
        public Optional<AnimationVec3> pollRotationPoint(ExpressionEvaluator<MolangContext<?>> evaluator) {
            var target = new AnimationVec3();

            // 这一坨不要轻易改
            boolean active = false;
            boolean first = true;
            boolean isBeginningTransition = false;
            Vector3f offset = null;
            Vector3f initRot = null;
            float transitionPercentProgress = 0f;

            for (var pair : this.underlyingQueues) {
                if (!pair.left().shouldApply()) {
                    continue;
                }
                var queue = pair.right();
                if (!queue.isActive()) {
                    continue;
                }
                var point = queue.rotation;
                if (point == null) {
                    continue;
                }
                active = true;

                if (first) {
                    first = false;
                    if (point instanceof BeginningTransitionPoint transitionPoint) {
                        isBeginningTransition = true;
                        offset = transitionPoint.getTransitionOffset();
                        transitionPercentProgress = transitionPoint.getTransitionPercentProgress();
                        target.setEndingTransitionPercentProgressIfLess(0);
                        initRot = queue.topLevelSnapshot.bone.getInitialRotation();
                    }
                }

                if (!isBeginningTransition) {
                    var pointValue = point.getLerpPoint(evaluator);
                    var weight = pair.right().getBlendWeight();
                    if (point instanceof EndingTransitionPoint endingPoint) {
                        var progress = endingPoint.getPercentCompleted();
                        weight *= (1 - progress);
                        target.setEndingTransitionPercentProgressIfLess(progress);
                    } else {
                        target.setEndingTransitionPercentProgressIfLess(0);
                    }
                    target.fma(weight, pointValue);
                } else if (transitionPercentProgress <= -0.00001f || transitionPercentProgress >= 0.00001f) {
                    var transitionPoint = (BeginningTransitionPoint) point;
                    var dst = transitionPoint.getTransitionDst(evaluator);
                    target.fma(pair.right().getBlendWeight(), dst);
                } else {
                    target.set(offset);
                    return Optional.of(target);
                }
            }

            if (active) {
                if (isBeginningTransition) {
                    MathUtil.lerpRotationValues(transitionPercentProgress, offset, target, initRot, target);
                }
                return Optional.of(target);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional<AnimationVec3> pollPositionPoint(ExpressionEvaluator<MolangContext<?>> evaluator) {
            var target = new AnimationVec3();

            // 这一坨不要轻易改
            boolean active = false;
            boolean first = true;
            boolean isBeginningTransition = false;
            Vector3f offset = null;
            float transitionPercentProgress = 0f;

            for (var pair : this.underlyingQueues) {
                if (!pair.left().shouldApply()) {
                    continue;
                }
                var queue = pair.right();
                if (!queue.isActive()) {
                    continue;
                }
                var point = queue.position;
                if (point == null) {
                    continue;
                }
                active = true;

                if (first) {
                    first = false;
                    if (point instanceof BeginningTransitionPoint transitionPoint) {
                        isBeginningTransition = true;
                        offset = transitionPoint.getTransitionOffset();
                        transitionPercentProgress = transitionPoint.getTransitionPercentProgress();
                        target.setEndingTransitionPercentProgressIfLess(0);
                    }
                }

                if (!isBeginningTransition) {
                    var pointValue = point.getLerpPoint(evaluator);
                    var weight = pair.right().getBlendWeight();
                    if (point instanceof EndingTransitionPoint endingPoint) {
                        var progress = endingPoint.getPercentCompleted();
                        weight *= (1 - progress);
                        target.setEndingTransitionPercentProgressIfLess(progress);
                    } else {
                        target.setEndingTransitionPercentProgressIfLess(0);
                    }
                    target.fma(weight, pointValue);
                } else if (transitionPercentProgress <= -0.00001f || transitionPercentProgress >= 0.00001f) {
                    var transitionPoint = (BeginningTransitionPoint) point;
                    var dst = transitionPoint.getTransitionDst(evaluator);
                    target.fma(pair.right().getBlendWeight(), dst);
                } else {
                    target.set(offset);
                    return Optional.of(target);
                }
            }

            if (active) {
                if (isBeginningTransition) {
                    MathUtil.lerpValues(transitionPercentProgress, offset, target, target);
                }
                return Optional.of(target);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional<AnimationVec3> pollScalePoint(ExpressionEvaluator<MolangContext<?>> evaluator) {
            var target = new AnimationVec3(1, 1, 1);

            // 这一坨不要轻易改
            boolean active = false;
            boolean first = true;
            boolean isBeginningTransition = false;
            Vector3f offset = null;
            float transitionPercentProgress = 0f;

            for (var pair : this.underlyingQueues) {
                if (!pair.left().shouldApply()) {
                    continue;
                }
                var queue = pair.right();
                if (!queue.isActive()) {
                    continue;
                }
                var point = queue.scale;
                if (point == null) {
                    continue;
                }
                active = true;

                if (first) {
                    first = false;
                    if (point instanceof BeginningTransitionPoint transitionPoint) {
                        isBeginningTransition = true;
                        offset = transitionPoint.getTransitionOffset();
                        transitionPercentProgress = transitionPoint.getTransitionPercentProgress();
                        target.setEndingTransitionPercentProgressIfLess(0);
                    }
                }

                if (!isBeginningTransition) {
                    var pointValue = point.getLerpPoint(evaluator);
                    var weight = pair.right().getBlendWeight();
                    if (point instanceof EndingTransitionPoint endingPoint) {
                        var progress = endingPoint.getPercentCompleted();
                        weight *= (1 - progress);
                        target.setEndingTransitionPercentProgressIfLess(progress);
                    } else {
                        target.setEndingTransitionPercentProgressIfLess(0);
                    }
                    if (weight == 1f) {
                        target.mul(pointValue);
                    } else {
                        target.mul(MathUtil.computeWeightedScale(pointValue, weight));
                    }
                } else if (transitionPercentProgress <= -0.00001f || transitionPercentProgress >= 0.00001f) {
                    var transitionPoint = (BeginningTransitionPoint) point;
                    var dst = transitionPoint.getTransitionDst(evaluator);
                    target.mul(MathUtil.computeWeightedScale(dst, pair.right().getBlendWeight()));
                } else {
                    target.set(offset);
                    return Optional.of(target);
                }
            }

            if (active) {
                if (isBeginningTransition) {
                    MathUtil.lerpValues(transitionPercentProgress, offset, target, target);
                }
                return Optional.of(target);
            } else {
                return Optional.empty();
            }
        }
    }
}
