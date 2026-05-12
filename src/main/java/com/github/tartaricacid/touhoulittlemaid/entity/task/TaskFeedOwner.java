package com.github.tartaricacid.touhoulittlemaid.entity.task;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.task.IFeedTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidFeedOwnerTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

import javax.annotation.Nullable;
import java.util.List;

//FIXME EffectCures API removed, need to find replacement for milk cure check

public class TaskFeedOwner implements IFeedTask {
    public static final Identifier UID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "feed");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return Items.COOKED_BEEF.getDefaultInstance();
    }

    private boolean canRemoveEffect(List<ConsumeEffect> consumeEffects, MobEffectInstance effect) {
        for (ConsumeEffect consumeEffect : consumeEffects) {
            if (consumeEffect instanceof RemoveStatusEffectsConsumeEffect(var effects)) {
                if (effects.contains(effect.getEffect()))
                    return true;
            } else if (consumeEffect instanceof ClearAllStatusEffectsConsumeEffect c) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack, Player owner) {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        List<ConsumeEffect> el = consumable.onConsumeEffects();
        if (stack.getItem() == Items.MILK_BUCKET) {
            for (MobEffectInstance effect : owner.getActiveEffects()) {
                if (isHarmfulEffect(effect) && effect.getDuration() > 60 && canRemoveEffect(el, effect)) {
                    return true;
                }
            }
            return false;
        }
        if (stack.has(net.minecraft.core.component.DataComponents.FOOD)) {
            if (el
                    .stream()
                    .noneMatch(t ->
                            t instanceof ApplyStatusEffectsConsumeEffect a &&
                                    a.effects().stream().anyMatch(this::isHarmfulEffect)
                    ))
                return true;
        }
        return false;
    }

    @Override
    public Priority getPriority(ItemStack stack, Player owner) {
        if (stack.is(Items.MILK_BUCKET)) {
            return Priority.HIGH;
        }

        // 蜂蜜瓶可以清除中毒效果，所以当玩家拥有中毒效果时，应当优先使用
        if (stack.is(Items.HONEY_BOTTLE) && owner.hasEffect(MobEffects.POISON)) {
            return Priority.HIGH;
        }

        if (stack.is(Items.GOLDEN_APPLE)) {
            if (owner.getHealth() * 2 < owner.getMaxHealth()) {
                return Priority.HIGH;
            } else {
                return Priority.LOWEST;
            }
        }

        //FIXME getFoodProperties API changed
        if (stack.has(net.minecraft.core.component.DataComponents.FOOD)) {
            FoodData foodData = owner.getFoodData();
            if (!foodData.needsFood()) {
                return Priority.LOWEST;
            }
            FoodProperties food = stack.get(net.minecraft.core.component.DataComponents.FOOD);
            int heal = 0;
            if (food != null) {
                heal = food.nutrition();
            }
            int hunger = 20 - foodData.getFoodLevel();
            if (heal >= hunger) {
                return Priority.HIGH;
            } else {
                return Priority.LOW;
            }
        }

        return Priority.NONE;
    }

    @Override
    public ItemStack feed(ItemStack stack, Player owner) {
        //FIXME getUseAnimation and getDrinkingSound API changed
        //if (stack.getUseAnimation() == ItemUseAnimation.DRINK) {
        //    owner.level.playSound(null, owner, stack.getDrinkingSound(), SoundSource.NEUTRAL,
        //            0.5f, owner.level.getRandom().nextFloat() * 0.1f + 0.9f);
        //}
        return stack.getItem().finishUsingItem(stack, owner.level, owner);
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return SoundUtil.environmentSound(maid, InitSounds.MAID_FEED.get(), 0.3f);
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        return Lists.newArrayList(Pair.of(5, new MaidFeedOwnerTask(this, 2, 0.6f)));
    }

    private boolean isHarmfulEffect(MobEffectInstance effect) {
        return effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL;
    }

    @Override
    public String getMaidActionSummary() {
        return "Feed the user when they are hungry";
    }
}
