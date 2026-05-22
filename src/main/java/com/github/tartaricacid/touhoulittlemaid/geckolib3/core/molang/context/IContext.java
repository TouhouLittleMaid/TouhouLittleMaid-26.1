package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.event.AnimationEvent;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.IContextVariableStorage;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.IScopedVariableStorage;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.ITempVariableStorage;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.model.provider.data.EntityModelData;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IContext<TEntity> {
    TEntity entity();

    AnimatableEntity<?> animatableEntity();

    Minecraft mc();

    ClientLevel level();

    AnimationEvent<?> animationEvent();

    EntityModelData data();

    @Nullable
    AnimationContext animationContext();

    @Nullable
    ControllerContext controllerContext();

    RandomSource random();

    <TChild> IContext<TChild> createChild(TChild child);

    ITempVariableStorage tempStorage();

    IScopedVariableStorage scopedStorage();

    @Nullable
    IContextVariableStorage contextStorage();

    @Nullable
    IValue getUserFunction(String name);

    Object callUserFunction(ExecutionContext<?> context, IValue value, List<?> args);

    Object callUserFunction(ExecutionContext<?> ctx, IValue value, Function.ArgumentCollection args);

    List<?> userFunctionArgs();

    boolean isDebugEnabled();

    /**
     * 是否允许生成行为（粒子、音效、骨骼变色、骨骼发光、相机变换等）
     */
    boolean allowEmitting();

    void debugPrint(String message, Object... args);

    void debugPrint(Component message);

    SoundInstanceManager getSoundManager(boolean global);
}
