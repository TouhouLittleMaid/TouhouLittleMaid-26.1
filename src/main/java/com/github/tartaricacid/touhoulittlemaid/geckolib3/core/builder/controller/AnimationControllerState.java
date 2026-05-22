package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.controller;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.controller.transition.IBlendTransition;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import it.unimi.dsi.fastutil.ints.IntReferenceImmutablePair;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;


public class AnimationControllerState {
    private static final int BUILTIN_NAME = StringPool.computeIfAbsent("ysm-builtin");
    private static final String SUB_ENTRY_PREFIX = "ysm-entry-";

    private final String name;
    private final int pooledName;
    private final boolean builtin;
    @Nullable
    private final String subEntryName;
    private final List<Pair<String, @Nullable IValue>> animations;
    private final List<IntReferenceImmutablePair<IValue>> transitions;
    private final List<String> soundEffects;
    private final List<IValue> onEntry;
    private final List<IValue> onExit;
    private final IBlendTransition blendTransition;
    private final boolean blendViaShortestPath;

    
    @SuppressWarnings("unchecked")
    public AnimationControllerState(String name, Pair<String, IValue>[] animations, Pair<String, IValue>[] transitions, String[] soundEffects, IValue[] onEntry, IValue[] onExit, IBlendTransition blendTransition, boolean blendViaShortestPath) {
        this.name = name;
        this.pooledName = StringPool.computeIfAbsent(name);
        this.builtin = pooledName == BUILTIN_NAME;
        this.subEntryName = name.startsWith(SUB_ENTRY_PREFIX) ? name.substring(SUB_ENTRY_PREFIX.length()) : null;
        this.animations = ReferenceLists.unmodifiable(ReferenceArrayList.wrap(animations));
        this.transitions = ReferenceLists.unmodifiable((ReferenceList<IntReferenceImmutablePair<IValue>>) (Object) ReferenceArrayList.wrap(Arrays.stream(transitions)
                .map(p -> new IntReferenceImmutablePair<>(StringPool.computeIfAbsent(p.getKey()), p.getValue()))
                .toArray(IntReferenceImmutablePair[]::new)));
        this.soundEffects = ReferenceLists.unmodifiable(ReferenceArrayList.wrap(soundEffects));
        this.onEntry = ReferenceLists.unmodifiable(ReferenceArrayList.wrap(onEntry));
        this.onExit = ReferenceLists.unmodifiable(ReferenceArrayList.wrap(onExit));
        this.blendTransition = blendTransition;
        this.blendViaShortestPath = blendViaShortestPath;
    }

    public String name() {
        return name;
    }

    public int pooledName() {
        return pooledName;
    }

    public boolean isBuiltin() {
        return builtin;
    }

    @Nullable
    public String subEntryName() {
        return subEntryName;
    }

    public List<Pair<String, @Nullable IValue>> animations() {
        return animations;
    }

    public List<IntReferenceImmutablePair<IValue>> transitions() {
        return transitions;
    }

    public List<String> soundEffects() {
        return soundEffects;
    }

    public List<IValue> onEntry() {
        return onEntry;
    }

    public List<IValue> onExit() {
        return onExit;
    }

    public IBlendTransition blendTransition() {
        return blendTransition;
    }

    public boolean blendViaShortestPath() {
        return blendViaShortestPath;
    }
}
