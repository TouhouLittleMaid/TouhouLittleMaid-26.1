package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.data;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class SoundData {
    @Nullable
    private final ByteBuffer byteBuffer;
    private final SoundFormat soundFormat;
    private final int sampleRate;
    private final long samples;

    public SoundData(@Nullable ByteBuffer byteBuffer, int soundFormat, int sampleRate, long samples) {
        if (byteBuffer != null) {
            if (soundFormat == 2) {
                this.byteBuffer = ByteBuffer.allocateDirect(byteBuffer.remaining());
            } else {
                this.byteBuffer = ByteBuffer.allocate(byteBuffer.remaining());
            }
            this.byteBuffer.duplicate().put(byteBuffer.duplicate());
        } else {
            this.byteBuffer = null;
        }
        this.soundFormat = switch (soundFormat) {
            case 1 -> SoundFormat.VORBIS;
            case 2 -> SoundFormat.OPUS;
            default -> SoundFormat.UNDEFINED;
        };
        this.sampleRate = sampleRate;
        this.samples = samples;
    }

    public long samples() {
        return samples;
    }

    public int sampleRate() {
        return sampleRate;
    }

    public SoundFormat soundFormat() {
        return soundFormat;
    }

    @Nullable
    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }
}