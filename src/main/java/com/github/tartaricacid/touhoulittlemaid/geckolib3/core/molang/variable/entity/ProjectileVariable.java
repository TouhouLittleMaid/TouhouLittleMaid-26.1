package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.entity;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.IValueEvaluator;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.LambdaVariable;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileVariable extends LambdaVariable<Projectile> {
    public ProjectileVariable(IValueEvaluator<?, IContext<Projectile>> evaluator) {
        super(evaluator);
    }

    @Override
    protected boolean validateContext(IContext<?> context) {
        return context.entity() instanceof Projectile;
    }
}
