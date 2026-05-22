package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.data;

import java.nio.ByteBuffer;

public record SoundData(ByteBuffer byteBuffer, SoundFormat soundFormat) {
    public SoundData(ByteBuffer byteBuffer, SoundFormat soundFormat) {
        this.byteBuffer = byteBuffer.duplicate();
        this.soundFormat = soundFormat;
    }
}