package com.github.tartaricacid.touhoulittlemaid.client.animation.special;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.MaidPackLoaderEvent;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 为了让所有的自定义女仆模型拥有一些新版本的动画，需要通过事件来强制塞入
 * <br>
 * 目前仅需要处理 Legacy 模型的游泳动画和三叉戟使用动画，后续如果有需要再添加
 */
@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public final class HardcodedAnimation {
    private static final SwimAnimation SWIM_ANIMATION = new SwimAnimation();
    private static final TridentAnimation TRIDENT_ANIMATION = new TridentAnimation();

    @SubscribeEvent
    public static void onMaidPackLoader(MaidPackLoaderEvent.Legacy event) {
        MaidModelInfo info = event.getInfo();
        String id = info.getModelId().toString();
        CustomPackLoader.MAID_MODELS.getAnimation(id).ifPresent(animations -> {
            // 游泳动画
            animations.add(SWIM_ANIMATION);
            // 三叉戟使用动画
            animations.add(TRIDENT_ANIMATION);
        });
    }
}
