/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.util.json;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.Animation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder.LoopType;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.BoneAnimation;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.BoneKeyFrameProcessor;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.bone.RawBoneKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.event.EventKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.MolangParser;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.AnimationUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.server.ChainedJsonException;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonAnimationUtils {
    public static Set<Map.Entry<String, JsonElement>> getAnimations(JsonObject json) {
        if (json.has("animations")) {
            return json.getAsJsonObject("animations").entrySet();
        }
        return ImmutableSet.of();
    }

    public static List<Map.Entry<String, JsonElement>> getBones(JsonObject json) {
        JsonObject bones = json.getAsJsonObject("bones");
        return bones == null ? List.of() : new ArrayList<>(bones.entrySet());
    }

    public static List<Map.Entry<String, JsonElement>> getSoundKeyFrames(JsonObject json) {
        JsonObject sounds = json.getAsJsonObject("sound_effects");
        return sounds == null ? List.of() : new ArrayList<>(sounds.entrySet());
    }

    public static List<Map.Entry<String, JsonElement>> getCustomInstructionKeyFrames(JsonObject json) {
        JsonObject customInstructions = json.getAsJsonObject("timeline");
        return customInstructions == null ? List.of() : new ArrayList<>(customInstructions.entrySet());
    }

    private static JsonElement getObjectByKey(Set<Map.Entry<String, JsonElement>> json, String key)
            throws ChainedJsonException {
        for (Map.Entry<String, JsonElement> entry : json) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        throw new ChainedJsonException("Could not find key: " + key);
    }

    public static Map.Entry<String, JsonElement> getAnimation(JsonObject animationFile, String animationName)
            throws ChainedJsonException {
        return new AbstractMap.SimpleEntry(animationName, getObjectByKey(getAnimations(animationFile), animationName));
    }

    public static Animation deserializeJsonToAnimation(Map.Entry<String, JsonElement> element, MolangParser parser)
            throws ClassCastException, IllegalStateException {
        JsonObject animationJsonObject = element.getValue().getAsJsonObject();

        // 设置有关动画的一些 metadata
        var animationName = element.getKey();
        JsonElement animationLength = animationJsonObject.get("animation_length");
        var animationLengthTicks = animationLength == null ? -1
                : AnimationUtils.convertSecondsToTicks(animationLength.getAsFloat());

        var loop = LoopType.fromJson(animationJsonObject.get("loop"));
        var blendWeightNode = animationJsonObject.get("blend_weight");
        IValue blendWeight = null;
        if (blendWeightNode != null && blendWeightNode.isJsonPrimitive()) {
            var primitive = blendWeightNode.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                blendWeight = parser.getConstant(primitive.getAsNumber().floatValue());
            } else if (primitive.isString()) {
                blendWeight = parser.parseExpression(primitive.getAsString(), false);
            }
        }

        var boneAnimations = new ReferenceArrayList<BoneAnimation>();
        var soundKeyframes = new ReferenceArrayList<EventKeyFrame<String>>();
        var customInstructionKeyframes = new ReferenceArrayList<EventKeyFrame<IValue[]>>();

        // 处理音频关键帧
        for (Map.Entry<String, JsonElement> keyFrame : getSoundKeyFrames(animationJsonObject)) {
            float startTick = Float.parseFloat(keyFrame.getKey()) * 20;
            String value = getSoundEffectValue(keyFrame.getValue());
            soundKeyframes.add(new EventKeyFrame<>(startTick, value));
        }
        // 排序，因为 json 读取是乱序的
        soundKeyframes.sort(Comparator.comparingDouble(EventKeyFrame::getStartTick));

        // 处理自定义指令关键帧
        for (Map.Entry<String, JsonElement> keyFrame : getCustomInstructionKeyFrames(animationJsonObject)) {
            float startTick = Float.parseFloat(keyFrame.getKey()) * 20;
            JsonElement value = keyFrame.getValue();
            if (value.isJsonArray()) {
                JsonArray array = value.getAsJsonArray();
                IValue[] values = new IValue[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    String parserText = array.get(i).getAsString();
                    values[i] = parser.parseExpression(parserText, false);
                }
                customInstructionKeyframes.add(new EventKeyFrame<>(startTick, values));
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                IValue[] values = new IValue[]{parser.parseExpression(value.getAsString(), false)};
                customInstructionKeyframes.add(new EventKeyFrame<>(startTick, values));
            }
        }
        // 排序，因为 json 读取是乱序的
        customInstructionKeyframes.sort(Comparator.comparingDouble(EventKeyFrame::getStartTick));


        // 此动画中使用的所有骨骼的列表
        for (Map.Entry<String, JsonElement> bone : getBones(animationJsonObject)) {

            List<RawBoneKeyFrame> rotationKeyFrames = Lists.newArrayList();
            List<RawBoneKeyFrame> positionKeyFrames = Lists.newArrayList();
            List<RawBoneKeyFrame> scaleKeyFrames = Lists.newArrayList();
            JsonObject boneJsonObj = bone.getValue().getAsJsonObject();

            JsonKeyFrameUtils.getKeyFrames(scaleKeyFrames, boneJsonObj.get("scale"), parser);
            JsonKeyFrameUtils.getKeyFrames(positionKeyFrames, boneJsonObj.get("position"), parser);
            JsonKeyFrameUtils.getKeyFrames(rotationKeyFrames, boneJsonObj.get("rotation"), parser);


            boneAnimations.add(new BoneAnimation(bone.getKey(),
                    BoneKeyFrameProcessor.process(rotationKeyFrames, true),
                    BoneKeyFrameProcessor.process(positionKeyFrames, false),
                    BoneKeyFrameProcessor.process(scaleKeyFrames, false)));
        }
        if (animationLengthTicks == -1) {
            animationLengthTicks = calculateLength(boneAnimations);
        }
        return new Animation(animationName, animationLengthTicks, loop, blendWeight, boneAnimations, soundKeyframes, customInstructionKeyframes);
    }

    private static String getSoundEffectValue(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsString();
        }
        if (element.isJsonObject()) {
            JsonElement effect = element.getAsJsonObject().get("effect");
            if (effect != null && effect.isJsonPrimitive() && effect.getAsJsonPrimitive().isString()) {
                return effect.getAsString();
            }
        }
        throw new IllegalStateException("Unsupported sound_effects keyframe value, expected a string or an object with a string 'effect' field: " + element);
    }

    private static float calculateLength(List<BoneAnimation> boneAnimations) {
        float longestLength = 0;
        for (BoneAnimation animation : boneAnimations) {
            float xKeyframeTime = calculateKeyFrameListLength(animation.rotationKeyFrames);
            float yKeyframeTime = calculateKeyFrameListLength(animation.positionKeyFrames);
            float zKeyframeTime = calculateKeyFrameListLength(animation.scaleKeyFrames);
            longestLength = maxAll(longestLength, xKeyframeTime, yKeyframeTime, zKeyframeTime);
        }
        return longestLength == 0 ? Float.MAX_VALUE : longestLength;
    }

    private static float calculateKeyFrameListLength(List<BoneKeyFrame> boneKeyFrames) {
        if (boneKeyFrames.isEmpty()) {
            return 0;
        }
        return boneKeyFrames.getLast().getStartTick();
    }

    public static float maxAll(float... values) {
        float max = 0;
        for (float value : values) {
            max = Math.max(value, max);
        }
        return max;
    }
}
