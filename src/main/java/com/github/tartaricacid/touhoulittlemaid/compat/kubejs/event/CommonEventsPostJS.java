package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.*;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common.*;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class CommonEventsPostJS {
    public static ResourceKey<Item> getItemKey(Item item) {
        Identifier key = BuiltInRegistries.ITEM.getKey(item);
        return ResourceKey.create(Registries.ITEM, key);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void interactMaid(InteractMaidEvent event) {
        if (MaidEventsJS.INTERACT_MAID.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Item item = event.getStack().getItem();
            EventResult result = MaidEventsJS.INTERACT_MAID.post(scriptType, getItemKey(item), new InteractMaidEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidAfterEat(MaidAfterEatEvent event) {
        if (MaidEventsJS.MAID_AFTER_EAT.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Item item = event.getFoodAfterEat().getItem();
            MaidEventsJS.MAID_AFTER_EAT.post(scriptType, getItemKey(item), new MaidAfterEatEventJS(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidAttack(MaidAttackEvent event) {
        if (MaidEventsJS.MAID_ATTACK.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Identifier id = event.getSource().typeHolder().unwrapKey().map(ResourceKey::location).orElse(null);
            EventResult result = MaidEventsJS.MAID_ATTACK.post(scriptType, id, new MaidAttackEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidDamage(MaidDamageEvent event) {
        if (MaidEventsJS.MAID_DAMAGE.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Identifier id = event.getSource().typeHolder().unwrapKey().map(ResourceKey::location).orElse(null);
            EventResult result = MaidEventsJS.MAID_DAMAGE.post(scriptType, id, new MaidDamageEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidDeath(MaidDeathEvent event) {
        if (MaidEventsJS.MAID_DEATH.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Identifier id = event.getSource().typeHolder().unwrapKey().map(ResourceKey::location).orElse(null);
            EventResult result = MaidEventsJS.MAID_DEATH.post(scriptType, id, new MaidDeathEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidEquip(MaidEquipEvent event) {
        if (MaidEventsJS.MAID_EQUIP.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Item item = event.getStack().getItem();
            MaidEventsJS.MAID_EQUIP.post(scriptType, getItemKey(item), new MaidEquipEventJS(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidFish(MaidFishedEvent event) {
        if (MaidEventsJS.MAID_FISHED.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            MaidEventsJS.MAID_FISHED.post(scriptType, new MaidFishedEventJS(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidHurt(MaidHurtEvent event) {
        if (MaidEventsJS.MAID_HURT.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Identifier id = event.getSource().typeHolder().unwrapKey().map(ResourceKey::location).orElse(null);
            EventResult result = MaidEventsJS.MAID_HURT.post(scriptType, id, new MaidHurtEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPickupItemResultPre(MaidPickupEvent.ItemResultPre event) {
        if (MaidEventsJS.MAID_PICKUP_ITEM_RESULT_PRE.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_PICKUP_ITEM_RESULT_PRE.post(scriptType, new MaidPickupEventJS.ItemResultPre(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPickupItemResultPost(MaidPickupEvent.ItemResultPost event) {
        if (MaidEventsJS.MAID_PICKUP_ITEM_RESULT_POST.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            MaidEventsJS.MAID_PICKUP_ITEM_RESULT_POST.post(scriptType, new MaidPickupEventJS.ItemResultPost(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPickupExperienceResult(MaidPickupEvent.ExperienceResult event) {
        if (MaidEventsJS.MAID_PICKUP_EXPERIENCE_RESULT.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_PICKUP_EXPERIENCE_RESULT.post(scriptType, new MaidPickupEventJS.ExperienceResult(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPickupArrowResult(MaidPickupEvent.ArrowResult event) {
        if (MaidEventsJS.MAID_PICKUP_ARROW_RESULT.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_PICKUP_ARROW_RESULT.post(scriptType, new MaidPickupEventJS.ArrowResult(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPickupPowerPointResult(MaidPickupEvent.PowerPointResult event) {
        if (MaidEventsJS.MAID_PICKUP_POWER_POINT_RESULT.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_PICKUP_POWER_POINT_RESULT.post(scriptType, new MaidPickupEventJS.PowerPointResult(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidPlaySound(MaidPlaySoundEvent event) {
        if (MaidEventsJS.MAID_PLAY_SOUND.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_PLAY_SOUND.post(scriptType, new MaidPlaySoundEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidTick(MaidTickEvent event) {
        if (MaidEventsJS.MAID_TICK.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            EventResult result = MaidEventsJS.MAID_TICK.post(scriptType, new MaidTickEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidTaskEnable(MaidTaskEnableEvent event) {
        if (MaidEventsJS.MAID_TASK_ENABLE.hasListeners()) {
            ScriptType scriptType = event.getEntityMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            Identifier uid = event.getTargetTask().getUid();
            EventResult result = MaidEventsJS.MAID_TASK_ENABLE.post(scriptType, uid, new MaidTaskEnableEventJS(event));
            if (result.interruptFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void maidTamed(MaidTamedEvent event) {
        if (MaidEventsJS.MAID_TAMED.hasListeners()) {
            ScriptType scriptType = event.getMaid().level.isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            MaidEventsJS.MAID_TAMED.post(scriptType, new MaidTamedEventJS(event));
        }
    }
}
