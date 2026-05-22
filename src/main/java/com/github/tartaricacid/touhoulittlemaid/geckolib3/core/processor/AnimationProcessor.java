package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.IAnimationController;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.manager.AnimationData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.MolangMemory;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.snapshot.BoneTopLevelSnapshot;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.util.MathUtil;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Struct;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMaps;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class AnimationProcessor<TEntity extends Entity> {
    private static final int ROAMING_STRUCT_NAME = StringPool.computeIfAbsent("roaming");

    private final AnimatableEntity<TEntity> animatable;
    private final ReferenceArrayList<BoneTopLevelSnapshot> modelBones = new ReferenceArrayList<>();
    private Object2ReferenceMap<String, List<IValue>> eventHandlers = Object2ReferenceMaps.emptyMap();
    private final Int2ReferenceOpenHashMap<BoneTopLevelSnapshot> modelBonesMap = new Int2ReferenceOpenHashMap<>();
    private final ArrayListDeque<BoneTopLevelSnapshot> activeModelBones = new ArrayListDeque<>();

    private final MolangMemory molangMemory = new MolangMemory();
    private final SoundInstanceManager globalSoundManager = new SoundInstanceManager();
    private final RandomSource random = new XoroshiroRandomSource(RandomSupport.generateUniqueSeed());
    // molang 执行任务的生产和消费可能在不同线程上
    private final ConcurrentLinkedQueue<MolangExecutionTask> pendingMolangTask = new ConcurrentLinkedQueue<>();

    private float lastTrimTime = 0;
    private boolean modelDirty = false;

    public AnimationProcessor(AnimatableEntity<TEntity> animatable) {
        this.animatable = animatable;
    }

    @SuppressWarnings("unchecked")
    public void tickAnimation(AnimationEvent<AnimatableEntity<TEntity>> event, MolangContext<?> ctx, boolean shouldTick, boolean allowEmitting) {
        ctx.setMemory(this.molangMemory);
        ctx.setRandom(this.random);
        ctx.setGlobalSoundManager(this.globalSoundManager);

        ExpressionEvaluator<MolangContext<?>> evaluator = ExpressionEvaluator.evaluator(ctx);
        var renderTicks = event.getRenderTicks();

        if (renderTicks - lastTrimTime >= 1200) {
            globalSoundManager.trim();
            lastTrimTime = renderTicks;
        } else if (lastTrimTime > renderTicks) {
            lastTrimTime = renderTicks;
        }

        preProcess(evaluator);

        AnimationData manager = this.animatable.getAnimationData();
        for (IAnimationController<AnimatableEntity<TEntity>> controller : manager.getAnimationControllers()) {
            if (this.modelDirty) {
                controller.updateModel(this.modelBones, eventHandlers);
            }
            if (shouldTick) {
                // 将当前控制器设置为动画测试事件
                // 处理动画并向点队列添加新值
                controller.process(event, evaluator, allowEmitting);
            }
            // 解决一个历史遗留问题而保留的动画混合
            @Deprecated boolean blendRotation = controller.blendRotation();
            // 遍历每个骨骼，并对属性进行插值计算
            controller.visitBoneAnimationQueues(boneAnimation -> {
                BoneTopLevelSnapshot snapshot = boneAnimation.getSnapshot();
                if (!snapshot.hasAnimation) {
                    snapshot.hasAnimation = true;
                    activeModelBones.add(snapshot);
                }

                boneAnimation.pollRotationPoint(evaluator).ifPresent(rot -> {
                    @Deprecated var pointData = snapshot.cachedPointData;
                    if (!snapshot.isCurrentlyRunningRotationAnimation) {
                        snapshot.isCurrentlyRunningRotationAnimation = true;
                        snapshot.rotation.set(0, 0, 0);
                    }
                    snapshot.lastRotationUpdateTime = renderTicks;
                    if (blendRotation) {
                        // 此处假设旧版 blendRotation 只有高并行动画在用，并且该类型动画永不结束，所以尾过渡进度永远为 0。实际上也理应如此
                        pointData.add(rot);
                        snapshot.rotation.set(pointData);
                    } else {
                        rot.applyRotation(snapshot.rotation, boneAnimation.getSnapshot().bone.getInitialRotation());
                        pointData.set(snapshot.rotation);
                    }
                });

                boneAnimation.pollPositionPoint(evaluator).ifPresent(position -> {
                    if (!snapshot.isCurrentlyRunningPositionAnimation) {
                        snapshot.isCurrentlyRunningPositionAnimation = true;
                        snapshot.position.set(0, 0, 0);
                    }
                    snapshot.lastPositionUpdateTime = renderTicks;
                    position.apply(snapshot.position);
                });

                boneAnimation.pollScalePoint(evaluator).ifPresent(scale -> {
                    if (!snapshot.isCurrentlyRunningScaleAnimation) {
                        snapshot.isCurrentlyRunningScaleAnimation = true;
                        snapshot.scale.set(1, 1, 1);
                    }
                    snapshot.lastScaleUpdateTime = renderTicks;
                    scale.apply(snapshot.scale);
                });
            });
        }

        this.modelDirty = false;

        // 追踪哪些骨骼应用了动画，并最终将没有动画的骨骼过渡到默认值
        // 反向遍历降低更新开销
        var activeBoneIterator = activeModelBones.listIterator(activeModelBones.size());
        while (activeBoneIterator.hasPrevious()) {
            var snapshot = activeBoneIterator.previous();
            var active = false;

            // 处理旋转尾过渡
            if (snapshot.isCurrentlyRunningRotationAnimation) {
                active = true;
                snapshot.isCurrentlyRunningRotationAnimation = false;
                snapshot.rotationOffset = null;
            } else {
                if (snapshot.rotationOffset == null) {
                    snapshot.rotationOffset = new Vector3f(snapshot.rotation);
                }
                var progress = (renderTicks - snapshot.lastRotationUpdateTime) / manager.getResetSpeed();
                if (progress < 1f) {
                    active = true;
                    MathUtil.lerpRotationValues(progress, snapshot.rotationOffset, MathUtil.ZERO, snapshot.bone.getInitialRotation(), snapshot.rotation);
                } else {
                    snapshot.rotation.set(MathUtil.ZERO);
                }
            }

            // 处理位移尾过渡
            if (snapshot.isCurrentlyRunningPositionAnimation) {
                active = true;
                snapshot.isCurrentlyRunningPositionAnimation = false;
                snapshot.positionOffset = null;
            } else {
                if (snapshot.positionOffset == null) {
                    snapshot.positionOffset = new Vector3f(snapshot.position);
                }
                var progress = (renderTicks - snapshot.lastPositionUpdateTime) / manager.getResetSpeed();
                if (progress < 1f) {
                    active = true;
                    MathUtil.lerpValues(progress, snapshot.positionOffset, MathUtil.ZERO, snapshot.position);
                } else {
                    snapshot.position.set(0, 0, 0);
                }
            }

            // 处理缩放尾过渡，终点为 1,1,1
            if (snapshot.isCurrentlyRunningScaleAnimation) {
                active = true;
                snapshot.isCurrentlyRunningScaleAnimation = false;
                snapshot.scaleOffset = null;
            } else {
                if (snapshot.scaleOffset == null) {
                    snapshot.scaleOffset = new Vector3f(snapshot.scale);
                }
                var progress = (renderTicks - snapshot.lastScaleUpdateTime) / manager.getResetSpeed();
                if (progress < 1f) {
                    active = true;
                    MathUtil.lerpValues(progress, snapshot.scaleOffset, MathUtil.ONE, snapshot.scale);
                } else {
                    snapshot.scale.set(1, 1, 1);
                }
            }

            snapshot.commit();
            if (!active) {
                snapshot.hasAnimation = false;
                activeBoneIterator.remove();
            }
        }

        ctx.setControllerContext(null);
        ctx.setAnimationContext(null);
        postProcess(evaluator);
    }

    @Nullable
    public AnimatedGeoBone getBone(int boneName) {
        BoneTopLevelSnapshot bone = modelBonesMap.get(boneName);
        return bone != null ? bone.bone : null;
    }

    public void clearModel() {
        this.modelBonesMap.clear();
        this.activeModelBones.clear();
        this.modelBones.clear();
        this.molangMemory.initialize();
        this.eventHandlers = Object2ReferenceMaps.emptyMap();
        this.pendingMolangTask.clear();
        this.globalSoundManager.stopAllPlayingSounds();
    }

    public void loadModel(AnimatedGeoModel model, Object2ReferenceMap<String, List<IValue>> eventHandlers) {
        clearModel();
        if (!model.boneMap().isEmpty()) {
            this.modelBones.ensureCapacity(model.boneMap().size());
            this.modelBones.add(null);
            var rootName = model.geoModel().flatBoneList().getFirst().pooledName();
            // 确保 root 组在第一个
            Int2ReferenceMaps.fastForEach(model.boneMap(), entry -> {
                BoneTopLevelSnapshot snapshot = new BoneTopLevelSnapshot(entry.getValue());
                this.modelBonesMap.put(entry.getValue().getPooledName(), snapshot);
                if (entry.getIntKey() == rootName) {
                    this.modelBones.set(0, snapshot);
                } else {
                    this.modelBones.add(snapshot);
                }
            });
        }
        this.modelDirty = true;
        this.eventHandlers = eventHandlers;
    }

    public void putRemoteStruct(@Nullable Struct remoteStruct) {
        if (remoteStruct != null) {
            molangMemory.setScoped(ROAMING_STRUCT_NAME, remoteStruct);
        }
    }

    public boolean isModelEmpty() {
        return modelBones.isEmpty();
    }

    private void preProcess(ExpressionEvaluator<MolangContext<?>> evaluator) {
        for (var iter = pendingMolangTask.iterator(); iter.hasNext(); ) {
            var task = iter.next();
            if (task.pre) {
                executeMolangTask(task, evaluator);
                iter.remove();
            }
        }
    }

    private void postProcess(ExpressionEvaluator<MolangContext<?>> evaluator) {
        for (var iter = pendingMolangTask.iterator(); iter.hasNext(); ) {
            var task = iter.next();
            if (!task.pre) {
                executeMolangTask(task, evaluator);
                iter.remove();
            }
        }
    }

    private void executeMolangTask(MolangExecutionTask task, ExpressionEvaluator<MolangContext<?>> evaluator) {
        String result;
        try {
            evaluator.entity().setAllowEmitting(task.allowEmitting());
            var ret = task.exp().eval(evaluator);
            if (task.resultCallback() == null) {
                return;
            }
            if (ret == null) {
                result = "null";
            } else if (ret instanceof String) {
                result = "'" + ret + "'";
            } else {
                result = ret.toString();
            }
        } catch (Throwable e) {
            result = "Error: " + e.getMessage();
        } finally {
            evaluator.entity().setAllowEmitting(false);
        }
        task.resultCallback().accept(result);
    }

    public void enqueueMolangTask(IValue value, boolean allowEmitting, boolean pre, @Nullable Consumer<String> resultConsumer) {
        pendingMolangTask.add(new MolangExecutionTask(value, allowEmitting, pre, resultConsumer));
    }

    public void visitScopedVariableNames(Consumer<String> visitor) {
        this.molangMemory.visitScopedVariableNames(visitor);
    }

    private record MolangExecutionTask(IValue exp, boolean allowEmitting, boolean pre,
                                       Consumer<String> resultCallback) {
    }
}
