package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.builder;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public enum LoopType {
    LOOP,
    PLAY_ONCE,
    HOLD_ON_LAST_FRAME;

    /**
     * 从动画文件读取播放类型
     *
     * @param json json 文件
     * @return 播放类型
     */
    public static LoopType fromJson(JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return PLAY_ONCE;
        }
        JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
        }
        if (primitive.isString()) {
            String string = primitive.getAsString();
            if ("false".equalsIgnoreCase(string)) {
                return PLAY_ONCE;
            }
            if ("true".equalsIgnoreCase(string)) {
                return LOOP;
            }
            try {
                return valueOf(string.toUpperCase(Locale.ROOT));
            } catch (Exception ignore) {
            }
        }
        return PLAY_ONCE;
    }
}
