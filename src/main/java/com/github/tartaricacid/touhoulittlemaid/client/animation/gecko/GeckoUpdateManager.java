package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.RenderContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderContextManager;
import net.minecraft.client.Minecraft;

import java.util.WeakHashMap;

public class GeckoUpdateManager {
    private static final Object PLACEHOLDER = new Object();

    private static WeakHashMap<AnimatableEntity<?>, Object> SCHEDULED_ENTITY_LIST = new WeakHashMap<>(64);
    private static WeakHashMap<AnimatableEntity<?>, Object> TICKED_ENTITY_LIST = new WeakHashMap<>(64);

    // 添加需要长期保持更新的实体
    public static void add(AnimatableEntity<?> instance) {
        SCHEDULED_ENTITY_LIST.put(instance, PLACEHOLDER);
    }

    // 记录更新任务
    public static void recordUpdate(AnimatableEntity<?> animatable, RenderContext ctx) {
        if (!ctx.offScreen()) {
            if (ctx.immutable() && SCHEDULED_ENTITY_LIST.remove(animatable) != null) {
                TICKED_ENTITY_LIST.put(animatable, PLACEHOLDER);
            }
        }
    }

    // extractLevel 结束后更新剩余实体
    public static void updateRemaining(float partialTick) {
        // 开始更新此帧剩余实体
        RenderContextManager.setOffScreen(true);
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        for (var animatable : SCHEDULED_ENTITY_LIST.keySet()) {
            if (animatable.isActive()) {
                if (animatable.isModelPresent()) {
                    dispatcher.extractEntity(animatable.getEntity(), partialTick);
                }
                TICKED_ENTITY_LIST.put(animatable, PLACEHOLDER);
            }
        }
        SCHEDULED_ENTITY_LIST.clear();
        RenderContextManager.setOffScreen(false);
    }

    // 当前帧结束后等待所有实体更新完成
    public static void finalizeFrame() {
        if (!SCHEDULED_ENTITY_LIST.isEmpty()) {
            updateRemaining(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
        }

        // 等待
        for (var animatable : TICKED_ENTITY_LIST.keySet()) {
            animatable.waitForAsyncUpdate();
        }

        // 交换列表
        SCHEDULED_ENTITY_LIST.clear();
        var newList = TICKED_ENTITY_LIST;
        TICKED_ENTITY_LIST = SCHEDULED_ENTITY_LIST;
        SCHEDULED_ENTITY_LIST = newList;
    }
}
