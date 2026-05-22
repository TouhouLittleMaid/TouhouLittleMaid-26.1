package com.github.tartaricacid.touhoulittlemaid.geckolib3.util;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;

public class MolangUtils {
    private static final HashMap<String, EquipmentSlot> SLOT_MAP;
    // 改成 -5 ~ 5 之间，防止有人传入过大的值有人用此做矿透
    private static final int MAX_RELATIVE_BLOCK_POS = 5;

    static {
        SLOT_MAP = new HashMap<>();
        SLOT_MAP.put("chest", EquipmentSlot.CHEST);
        SLOT_MAP.put("feet", EquipmentSlot.FEET);
        SLOT_MAP.put("head", EquipmentSlot.HEAD);
        SLOT_MAP.put("legs", EquipmentSlot.LEGS);
        SLOT_MAP.put("mainhand", EquipmentSlot.MAINHAND);
        SLOT_MAP.put("offhand", EquipmentSlot.OFFHAND);
    }

    public static float normalizeTime(long timestamp) {
        return ((float) (timestamp + 6000L) / 24000) % 1;
    }

    @Nullable
    public static BlockState getRelativeBlock(ExecutionContext<IContext<Entity>> ctx, Function.ArgumentCollection args) {
        return getRelativeBlock(ctx, args, 0);
    }

    @Nullable
    @SuppressWarnings("resource")
    public static BlockState getRelativeBlock(ExecutionContext<IContext<Entity>> ctx, Function.ArgumentCollection args, int argsOffset) {
        double offsetX = args.getAsDouble(ctx, argsOffset);
        double offsetY = args.getAsDouble(ctx, argsOffset + 1);
        double offsetZ = args.getAsDouble(ctx, argsOffset + 2);
        if (Math.abs(offsetX) > MAX_RELATIVE_BLOCK_POS || Math.abs(offsetY) > MAX_RELATIVE_BLOCK_POS || Math.abs(offsetZ) > MAX_RELATIVE_BLOCK_POS) {
            return null;
        }
        var entity = ctx.entity().entity();
        BlockPos pos = new BlockPos((int) Math.round(entity.getX() + offsetX - 0.5d),
                (int) Math.round(entity.getY() + offsetY - 0.5d),
                (int) Math.round(entity.getZ() + offsetZ - 0.5d));
        return entity.level().getBlockState(pos);
    }

    public static EquipmentSlot parseSlotType(IContext<?> context, String value) {
        if (value == null) {
            return null;
        }
        EquipmentSlot slot = SLOT_MAP.get(value.toLowerCase(Locale.ENGLISH));
        if (slot == null) {
            context.debugPrint("Illegal slot type: %s.", value);
        }
        return slot;
    }
}
