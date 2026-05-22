package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.IValueEvaluator;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.LambdaVariable;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.block.AbstractBlockVariable;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.block.BlockStateVariable;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.block.BlockVariable;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.entity.*;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.item.ItemStackVariable;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.variable.item.ItemVariable;
import com.github.tartaricacid.touhoulittlemaid.molang.parser.ast.StringExpression;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ObjectBinding;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class ContextBinding implements ObjectBinding {
    protected final Object2ReferenceOpenHashMap<String, Object> bindings = new Object2ReferenceOpenHashMap<>();

    @Override
    public Object getProperty(String name) {
        return bindings.get(name);
    }

    public Set<String> getAllName() {
        return bindings.keySet();
    }

    public void function(String name, Function function) {
        bindings.put(name, function);
    }

    public void constValue(String name, Object value) {
        if (value instanceof String str) {
            bindings.put(name, new StringExpression(str));
        } else if (value instanceof Number num) {
            bindings.put(name, num.floatValue());
        } else if (value instanceof Boolean b) {
            bindings.put(name, b ? 1f : 0f);
        } else {
            bindings.put(name, value);
        }
    }

    public void var(String name, IValueEvaluator<?, IContext<Object>> evaluator) {
        bindings.put(name, new LambdaVariable<>(evaluator));
    }

    public void entityVar(String name, IValueEvaluator<?, IContext<Entity>> evaluator) {
        bindings.put(name, new EntityVariable(evaluator));
    }

    public void livingEntityVar(String name, IValueEvaluator<?, IContext<LivingEntity>> evaluator) {
        bindings.put(name, new LivingEntityVariable(evaluator));
    }

    public void mobEntityVar(String name, IValueEvaluator<?, IContext<Mob>> evaluator) {
        bindings.put(name, new MobEntityVariable(evaluator));
    }

    public void tamableEntityVar(String name, IValueEvaluator<?, IContext<TamableAnimal>> evaluator) {
        bindings.put(name, new TamableEntityVariable(evaluator));
    }

    public void maidEntityVar(String name, IValueEvaluator<?, IContext<EntityMaid>> evaluator) {
        bindings.put(name, new MaidEntityVariable(evaluator));
    }

    public void playerVar(String name, IValueEvaluator<?, IContext<Player>> evaluator) {
        bindings.put(name, new PlayerVariable(evaluator));
    }

    public void avatarVar(String name, IValueEvaluator<?, IContext<ClientAvatarEntity>> evaluator) {
        bindings.put(name, new AvatarVariable(evaluator));
    }

    public void clientPlayerVar(String name, IValueEvaluator<?, IContext<AbstractClientPlayer>> evaluator) {
        bindings.put(name, new ClientPlayerVariable(evaluator));
    }

    public void localPlayerVar(String name, IValueEvaluator<?, IContext<LocalPlayer>> evaluator) {
        bindings.put(name, new LocalPlayerVariable(evaluator));
    }

    public void projectileVar(String name, IValueEvaluator<?, IContext<Projectile>> evaluator) {
        bindings.put(name, new ProjectileVariable(evaluator));
    }

    public void throwableItemProjectileVar(String name, IValueEvaluator<?, IContext<ThrowableItemProjectile>> evaluator) {
        bindings.put(name, new ThrowableItemProjectileVariable(evaluator));
    }

    public void fishingHookVar(String name, IValueEvaluator<?, IContext<FishingHook>> evaluator) {
        bindings.put(name, new FishingHookVariable(evaluator));
    }

    public void abstractArrowVar(String name, IValueEvaluator<?, IContext<AbstractArrow>> evaluator) {
        bindings.put(name, new AbstractArrowVariable(evaluator));
    }

    public void arrowVar(String name, IValueEvaluator<?, IContext<Arrow>> evaluator) {
        bindings.put(name, new ArrowVariable(evaluator));
    }

    public void itemVar(String name, IValueEvaluator<?, IContext<Item>> evaluator) {
        bindings.put(name, new ItemVariable(evaluator));
    }

    public void itemStackVar(String name, IValueEvaluator<?, IContext<ItemStack>> evaluator) {
        bindings.put(name, new ItemStackVariable(evaluator));
    }

    public void blockStateVar(String name, IValueEvaluator<?, IContext<BlockState>> evaluator) {
        bindings.put(name, new BlockStateVariable(evaluator));
    }

    public void blockVar(String name, IValueEvaluator<?, IContext<Block>> evaluator) {
        bindings.put(name, new BlockVariable(evaluator));
    }

    public void abstractBlockVar(String name, IValueEvaluator<?, IContext<BlockBehaviour>> evaluator) {
        bindings.put(name, new AbstractBlockVariable(evaluator));
    }
}
