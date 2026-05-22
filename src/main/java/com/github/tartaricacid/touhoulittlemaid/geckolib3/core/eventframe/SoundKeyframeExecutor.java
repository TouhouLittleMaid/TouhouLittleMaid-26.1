/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.eventframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.AnimatableEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.event.EventKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.instance.SoundInstanceManager;
import net.minecraft.util.StringUtil;

import java.util.List;

public class SoundKeyframeExecutor {
    private final List<EventKeyFrame<String>> list;
    private final SoundInstanceManager soundManager;
    private int nextIndex = 0;

    public SoundKeyframeExecutor(List<EventKeyFrame<String>> list, SoundInstanceManager soundManager) {
        this.list = list;
        this.soundManager = soundManager;
    }

    public void executeTo(AnimatableEntity<?> animatable, float currentTick, boolean allowEmitting) {
        while (!reachEnd()) {
            EventKeyFrame<String> keyFrame = list.get(nextIndex);
            if (keyFrame.getStartTick() > currentTick) {
                return;
            }
            nextIndex++;
            if (allowEmitting && !StringUtil.isNullOrEmpty(keyFrame.getEventData())) {
                soundManager.playSound(animatable, 0, keyFrame.getEventData(), false, null);
            }
        }
    }

    public void reset() {
        nextIndex = 0;
        soundManager.stopAllPlayingSounds();
    }

    public void stopAll() {
        soundManager.stopAllPlayingSounds();
    }

    public boolean reachEnd() {
        return nextIndex >= list.size();
    }
}
