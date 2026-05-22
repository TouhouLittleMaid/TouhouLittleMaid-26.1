package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage.IContextVariableStorage;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public class AnimationContext implements IContextVariableStorage {
    private SoundInstanceManager soundManager;
    private float animTime;
    private Int2ObjectOpenHashMap<Object> variableStorage;
    private int deferCount = 0;
    private ReferenceArrayList<ReferenceArrayList<Object>> deferFunctionArgs;

    public void setAnimTime(float animTime) {
        this.animTime = animTime;
    }

    public float animTime() {
        return animTime;
    }

    public SoundInstanceManager soundManager() {
        if (soundManager == null) {
            soundManager = new SoundInstanceManager();
        }
        return soundManager;
    }

    @Override
    public Object getContext(int name) {
        if (variableStorage != null) {
            return variableStorage.get(name);
        }
        return null;
    }

    @Override
    public void setContext(int name, Object value) {
        if (variableStorage == null) {
            variableStorage = new Int2ObjectOpenHashMap<>();
        }
        variableStorage.put(name, value);
    }

    public void defer(ExecutionContext<?> ctx, int name, Function.ArgumentCollection args, int argsOffset) {
        if (deferFunctionArgs == null) {
            deferFunctionArgs = new ReferenceArrayList<>();
        }
        var index = deferCount++;
        ReferenceArrayList<Object> argValues;
        if (deferFunctionArgs.size() <= index) {
            argValues = new ReferenceArrayList<>(args.size() - argsOffset);
            deferFunctionArgs.add(argValues);
        } else {
            argValues = deferFunctionArgs.get(index);
        }
        argValues.size(args.size() - argsOffset);
        for (var i = argsOffset; i < args.size(); i++) {
            argValues.set(i - argsOffset, args.getValue(ctx, i));
        }
    }

    public void reset(ExpressionEvaluator<MolangContext<?>> evaluator) {
        if (deferCount > 0) {
            var ctx = evaluator.entity();
            var deferHandler = ctx.animatableEntity.getMolangDeferHandler();
            if (deferHandler != null) {
                ctx.setAllowEmitting(true);
                for (var i = deferCount - 1; i >= 0; i--) {
                    for (var func : deferHandler) {
                        var args = deferFunctionArgs.get(i);
                        ctx.callUserFunction(evaluator, func, args);
                    }
                }
                ctx.setAllowEmitting(false);
            }
            deferCount = 0;
        }
        if (variableStorage != null) {
            variableStorage.clear();
        }
    }
}
