package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream;

import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@FunctionalInterface
public interface AudioStreamProvider {
    @NotNull
    CustomAudioStream openStream() throws IOException, UnsupportedAudioFileException;
}
