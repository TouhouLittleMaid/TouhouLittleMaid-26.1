package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream;

import net.minecraft.client.sounds.AudioStream;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LoopingAudioStream implements CustomAudioStream {
    private static final ByteBuffer EMPTY_BUFFER = BufferUtils.createByteBuffer(0);

    private final AudioStreamProvider provider;
    private final AudioFormat format;
    private AudioStream stream;
    private volatile boolean closed;

    public LoopingAudioStream(AudioStreamProvider provider) throws UnsupportedAudioFileException, IOException {
        this.provider = provider;
        try {
            this.stream = provider.openStream();
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
            throw e;
        }
        this.format = this.stream.getFormat();
    }

    @Override
    public @NotNull AudioFormat getFormat() {
        return format;
    }

    @Override
    public @NotNull ByteBuffer read(int size) throws IOException {
        if (stream != null) {
            var buf = stream.read(size);
            if (buf.remaining() == 0) {
                stream = null;
                try {
                    clear();
                    stream = provider.openStream();
                } catch (Throwable e) {
                    e.printStackTrace();
                    return EMPTY_BUFFER;
                }
                buf = stream.read(size);
                if (buf.remaining() == 0) {
                    clear();
                    return EMPTY_BUFFER;
                }
            }
            return buf;
        }
        return EMPTY_BUFFER;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        clear();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public void clear() throws IOException {
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }
}
