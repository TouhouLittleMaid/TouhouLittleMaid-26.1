package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationUpdateTask;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderContextManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

import java.lang.invoke.VarHandle;
import java.util.WeakHashMap;

public class AnimationUpdateManager {
    private static final ContextKey<AnimationUpdateTask> KEY = new ContextKey<>(Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "gecko_render_state"));

    private static final Object PLACEHOLDER = new Object();

    private static WeakHashMap<AnimatableEntity<?>, Object> SCHEDULED_ANIMATABLE_LIST = new WeakHashMap<>(64);
    private static WeakHashMap<AnimatableEntity<?>, Object> TICKED_ANIMATABLE_LIST = new WeakHashMap<>(64);

    // 添加需要长期保持更新的实体
    public static void add(AnimatableEntity<?> instance) {
        SCHEDULED_ANIMATABLE_LIST.put(instance, PLACEHOLDER);
    }

    // 记录更新任务
    public static AnimationUpdateTask createTask(AnimatableEntity<?> animatable, EntityRenderState state) {
        var ctx = RenderContextManager.extract(true);
        if (!animatable.isImmutableRender(ctx)) {
            ctx = RenderContextManager.extract(false);
        }
        var task = animatable.createUpdateTask(state, ctx);
        if (!ctx.offScreen()) {
            if (ctx.immutable() && SCHEDULED_ANIMATABLE_LIST.remove(animatable) != null) {
                TICKED_ANIMATABLE_LIST.put(animatable, PLACEHOLDER);
            }
        }
        state.setRenderData(KEY, task);
        VarHandle.releaseFence();
        return task;
    }

    public static AnimationEvent<?> getResult(EntityRenderState state) {
        VarHandle.acquireFence();
        var task = state.getRenderData(KEY);
        if (task != null) {
            return task.getResult();
        }
        return null;
    }

    public static boolean start(EntityRenderState state) {
        VarHandle.acquireFence();
        var task = state.getRenderData(KEY);
        if (task != null) {
            task.start();
            return true;
        }
        return false;
    }

    // extractLevel 结束后更新剩余实体
    public static void updateRemaining(float partialTick) {
        // 开始更新此帧剩余实体
        RenderContextManager.setOffScreen(true);
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        for (var animatable : SCHEDULED_ANIMATABLE_LIST.keySet()) {
            if (animatable.isActive()) {
                if (animatable.isModelPresent()) {
                    dispatcher.extractEntity(animatable.getEntity(), partialTick);
                }
                TICKED_ANIMATABLE_LIST.put(animatable, PLACEHOLDER);
            }
        }
        SCHEDULED_ANIMATABLE_LIST.clear();
        RenderContextManager.setOffScreen(false);
    }

    // 当前帧结束后等待所有实体更新完成
    public static void finalizeFrame() {
        if (!SCHEDULED_ANIMATABLE_LIST.isEmpty()) {
            updateRemaining(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
        }

        // 等待
        for (var animatable : TICKED_ANIMATABLE_LIST.keySet()) {
            animatable.waitForAsyncUpdate();
        }

        // 交换列表
        SCHEDULED_ANIMATABLE_LIST.clear();
        var newList = TICKED_ANIMATABLE_LIST;
        TICKED_ANIMATABLE_LIST = SCHEDULED_ANIMATABLE_LIST;
        SCHEDULED_ANIMATABLE_LIST = newList;
    }
}
