package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.ContextFunction;
import net.minecraft.world.entity.projectile.Projectile;

public abstract class ProjectileEntityFunction extends ContextFunction<Projectile> {
    @Override
    protected boolean validateContext(IContext<?> context) {
        return context.entity() instanceof Projectile;
    }
}
