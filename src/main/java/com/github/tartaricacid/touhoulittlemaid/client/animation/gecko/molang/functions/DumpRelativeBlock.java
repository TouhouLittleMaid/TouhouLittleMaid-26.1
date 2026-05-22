package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.EntityFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class DumpRelativeBlock extends EntityFunction {
    @Override
    protected Object eval(ExecutionContext<IContext<Entity>> ctx, ArgumentCollection arguments) {
        if (!ctx.entity().isDebugEnabled()) {
            return null;
        }

        var block = MolangUtils.getRelativeBlock(ctx, arguments);
        if (block == null) {
            return null;
        }
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block.getBlock());
        if (blockId == null) {
            return null;
        }
        ctx.entity().debugPrint(Component.literal("Display ").append(ComponentUtils.copyOnClickText(block.getBlock().getName().getString(99))));
        ctx.entity().debugPrint(Component.literal("Name ").append(ComponentUtils.copyOnClickText(blockId.toString())));
        block.tags().forEach(key -> {
            ctx.entity().debugPrint(Component.literal("Tag ").append(ComponentUtils.copyOnClickText(key.location().toString())));
        });

        return null;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 3;
    }
}
