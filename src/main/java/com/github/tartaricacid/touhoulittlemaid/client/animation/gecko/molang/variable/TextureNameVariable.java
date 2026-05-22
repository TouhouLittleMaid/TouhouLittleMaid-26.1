package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.variable;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.IValueEvaluator;
import net.minecraft.world.entity.player.Player;

public class TextureNameVariable implements IValueEvaluator<String, IContext<Player>> {
    @Override
    public String eval(IContext<Player> ctx) {
        return ctx.animatableEntity().getTextureLocation().toString();
    }
}
