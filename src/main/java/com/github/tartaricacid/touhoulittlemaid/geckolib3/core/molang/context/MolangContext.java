package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.*;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.model.provider.data.EntityModelData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MolangContext<TEntity> implements IContext<TEntity> {
    protected final TEntity entity;
    protected final AnimatableEntity<?> animatableEntity;
    protected final AnimationEvent<?> animationEvent;
    protected final EntityModelData data;

    protected AnimationContext animationContext;
    protected ControllerContext controllerContext;
    protected SoundInstanceManager globalSoundManager;
    protected RandomSource random;
    protected MolangMemory memory;
    private DebugSource debugSource;
    private boolean allowEmitting;

    public MolangContext(TEntity entity, AnimatableEntity<?> animatableEntity, AnimationEvent<?> animationEvent, EntityModelData data) {
        this.entity = entity;
        this.animatableEntity = animatableEntity;
        this.animationEvent = animationEvent;
        this.data = data;
    }

    private MolangContext(TEntity entity, MolangContext<?> context) {
        this.entity = entity;
        this.animatableEntity = context.animatableEntity;
        this.animationEvent = context.animationEvent;
        this.data = context.data;
        this.animationContext = context.animationContext;
        this.random = context.random;
        this.memory = context.memory;
        this.globalSoundManager = context.globalSoundManager;
    }

    @Override
    public AnimationEvent<?> animationEvent() {
        return animationEvent;
    }

    @Override
    public AnimatableEntity<?> animatableEntity() {
        return animatableEntity;
    }

    @Override
    public EntityModelData data() {
        return data;
    }

    @Override
    public AnimationContext animationContext() {
        return animationContext;
    }

    @Override
    public ControllerContext controllerContext() {
        return controllerContext;
    }

    @Override
    public RandomSource random() {
        return random;
    }

    @Override
    public TEntity entity() {
        return entity;
    }

    @Override
    public Minecraft mc() {
        return Minecraft.getInstance();
    }

    @Override
    public ClientLevel level() {
        Minecraft mc = mc();
        if (mc != null) {
            return mc.level;
        } else {
            return null;
        }
    }

    // FIXME: 需要同时更新 animatable 和 entity 两个属性，再加上源属性
    @Override
    public <TChild> IContext<TChild> createChild(TChild child) {
        return new MolangContext<>(child, this);
    }

    @Override
    public ITempVariableStorage tempStorage() {
        return memory.getStackMemory();
    }

    @Override
    public IScopedVariableStorage scopedStorage() {
        return memory;
    }

    @Override
    public @Nullable IContextVariableStorage contextStorage() {
        return animationContext;
    }

    @Override
    public @Nullable IValue getUserFunction(String name) {
        return animatableEntity.getUserFunction(name);
    }

    @Override
    public Object callUserFunction(ExecutionContext<?> ctx, IValue value, List<?> args) {
        if (this.memory.getStackMemory().push(args)) {
            try {
                return value.eval((ExpressionEvaluator<?>) ctx);
            } finally {
                this.memory.getStackMemory().pop();
            }
        }
        return null;
    }

    @Override
    public Object callUserFunction(ExecutionContext<?> ctx, IValue value, Function.ArgumentCollection args) {
        if (this.memory.getStackMemory().push(ctx, args)) {
            try {
                return value.eval((ExpressionEvaluator<?>) ctx);
            } finally {
                this.memory.getStackMemory().pop();
            }
        }
        return null;
    }

    @Override
    public List<?> userFunctionArgs() {
        return memory.getStackMemory().argsAccessor();
    }

    @Override
    public boolean isDebugEnabled() {
        return debugSource != null;
    }

    @Override
    public boolean allowEmitting() {
        return allowEmitting;
    }

    public void setAllowEmitting(boolean value) {
        allowEmitting = value;
    }

    @Override
    public void debugPrint(String message, Object... args) {
        if (isDebugEnabled()) {
            debugSource.print(message, args);
        }
    }

    @Override
    public void debugPrint(Component message) {
        if (isDebugEnabled()) {
            debugSource.print(message);
        }
    }

    @Override
    @Nullable
    public SoundInstanceManager getSoundManager(boolean global) {
        if (!global) {
            if (animationContext != null) {
                var soundManager = animationContext.soundManager();
                if (soundManager != null) {
                    return soundManager;
                }
            }
            if (controllerContext != null) {
                var soundManager = controllerContext.soundManager();
                if (soundManager != null) {
                    return soundManager;
                }
            }
        }
        return this.globalSoundManager;
    }

    public void setGlobalSoundManager(SoundInstanceManager globalSoundManager) {
        this.globalSoundManager = globalSoundManager;
    }

    public void setAnimationContext(AnimationContext ctx) {
        this.animationContext = ctx;
    }

    public void setControllerContext(ControllerContext ctx) {
        this.controllerContext = ctx;
    }

    public void setMemory(MolangMemory storage) {
        this.memory = storage;
    }

    public void setRandom(RandomSource random) {
        this.random = random;
    }

    public void setDebugSource(DebugSource source) {
        this.debugSource = source;
    }
}
