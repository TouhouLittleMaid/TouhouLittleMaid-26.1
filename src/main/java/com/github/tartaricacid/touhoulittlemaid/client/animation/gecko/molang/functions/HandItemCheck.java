package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition.InnerClassify;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.LivingEntityFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class HandItemCheck extends LivingEntityFunction {
    private static final String ID_PREFIX = "$";
    private static final String TAG_PREFIX = "#";
    private static final String EXTRA_PREFIX = ":";
    private static final String EMPTY = "empty";
    private static final int FALSE = 0;
    private static final int TRUE = 1;

    private final Condition condition;

    private HandItemCheck(Condition condition) {
        this.condition = condition;
    }

    public static HandItemCheck holdCheck() {
        return new HandItemCheck((entity, hand) -> true);
    }

    public static HandItemCheck swingCheck() {
        return new HandItemCheck((entity, hand) -> entity.swinging && !entity.isSleeping());
    }

    public static HandItemCheck useCheck() {
        return new HandItemCheck((entity, hand) -> entity.isUsingItem() && !entity.isSleeping());
    }

    @Override
    protected Object eval(ExecutionContext<IContext<LivingEntity>> context, ArgumentCollection arguments) {
        EquipmentSlot slotType = MolangUtils.parseSlotType(context.entity(), arguments.getAsString(context, 0));
        if (slotType == null || slotType.isArmor()) {
            return FALSE;
        }

        String input = arguments.getAsString(context, 1);
        LivingEntity entity = context.entity().entity();

        if (StringUtils.isBlank(input)) {
            return FALSE;
        }

        ItemStack item = entity.getItemBySlot(slotType);
        InteractionHand hand = slotType == EquipmentSlot.OFFHAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

        if (!this.condition.test(entity, hand)) {
            return FALSE;
        }

        // 为空的特殊判断
        if (item.isEmpty() && input.equals(EMPTY)) {
            return TRUE;
        }

        String subInput = input.substring(1);
        if (input.startsWith(ID_PREFIX)) {
            Identifier registryName = BuiltInRegistries.ITEM.getKey(item.getItem());
            if (registryName == null) {
                return FALSE;
            }
            boolean equals = subInput.equals(registryName.toString());
            return equals ? TRUE : FALSE;
        }

        if (input.startsWith(TAG_PREFIX)) {
            Identifier res = Identifier.parse(subInput);
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, res);
            return item.is(tagKey) ? TRUE : FALSE;
        }

        if (input.startsWith(EXTRA_PREFIX)) {
            String innerName = InnerClassify.getClassify(item);
            if (StringUtils.isNotBlank(innerName) && innerName.equals(subInput)) {
                return TRUE;
            }
            String anim = item.getUseAnimation().name().toLowerCase(Locale.ENGLISH);
            if (anim.equals(subInput)) {
                return TRUE;
            }
            return FALSE;
        }

        return FALSE;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 2 || size == 3;
    }

    private interface Condition {
        boolean test(LivingEntity entity, InteractionHand hand);
    }
}
