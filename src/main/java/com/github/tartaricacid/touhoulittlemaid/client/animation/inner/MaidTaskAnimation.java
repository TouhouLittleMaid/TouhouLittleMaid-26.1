package com.github.tartaricacid.touhoulittlemaid.client.animation.inner;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.state.EntityMaidRenderState;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation.INNER_ANIMATION;

public final class MaidTaskAnimation {
    public static void init() {
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/attack.js"), getTaskAttack());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/danmaku_attack.js"), getTaskDanmakuAttack());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/farm.js"), getTaskFarm());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/feed_animal.js"), getTaskFeedAnimal());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/idle.js"), getTaskIdle());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/milk.js"), getTaskMilk());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/shears.js"), getTaskShears());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/sugar_cane.js"), getTaskSugarCane());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/cocoa.js"), getTaskCocoa());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/extinguishing.js"), getTaskExtinguishing());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/feed.js"), getTaskFeed());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/grass.js"), getTaskGrass());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/melon.js"), getTaskMelon());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/ranged_attack.js"), getTaskRangedAttack());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/snow.js"), getTaskSnow());
        INNER_ANIMATION.put(Identifier.parse("touhou_little_maid:animation/maid/default/task/torch.js"), getTaskTorch());
    }

    public static IAnimation<EntityMaidRenderState> getTaskAttack() {
        return createTaskVisibility("attack", "attackHidden", "attackShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskDanmakuAttack() {
        return createTaskVisibility("danmaku_attack", "danmakuAttackHidden", "danmakuAttackShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskFarm() {
        return createTaskVisibility("farm", "farmHidden", "farmShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskFeedAnimal() {
        return createTaskVisibility("feed_animal", "feedAnimalHidden", "feedAnimalShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskIdle() {
        return createTaskVisibility("idle", "idleHidden", "idleShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskMilk() {
        return createTaskVisibility("milk", "milkHidden", "milkShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskShears() {
        return createTaskVisibility("shears", "shearsHidden", "shearsShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskSugarCane() {
        return createTaskVisibility("sugar_cane", "sugarCaneHidden", "sugarCaneShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskCocoa() {
        return createTaskVisibility("cocoa", "cocoaHidden", "cocoaShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskExtinguishing() {
        return createTaskVisibility("extinguishing", "extinguishingHidden", "extinguishingShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskFeed() {
        return createTaskVisibility("feed", "feedHidden", "feedShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskGrass() {
        return createTaskVisibility("grass", "grassHidden", "grassShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskMelon() {
        return createTaskVisibility("melon", "melonHidden", "melonShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskRangedAttack() {
        return createTaskVisibility("ranged_attack", "rangedAttackHidden", "rangedAttackShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskSnow() {
        return createTaskVisibility("snow", "snowHidden", "snowShow");
    }

    public static IAnimation<EntityMaidRenderState> getTaskTorch() {
        return createTaskVisibility("torch", "torchHidden", "torchShow");
    }

    private static IAnimation<EntityMaidRenderState> createTaskVisibility(String taskId, String hiddenName, String showName) {
        return (state, models) -> {
            boolean matches = taskId.equals(state.taskId);
            setVisible(models.get(hiddenName), !matches);
            setVisible(models.get(showName), matches);
        };
    }


    private static void setVisible(BedrockPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }
}
