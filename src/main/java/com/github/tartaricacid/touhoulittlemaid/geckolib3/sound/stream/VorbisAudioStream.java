package com.github.tartaricacid.touhoulittlemaid.geckolib3.sound.stream;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.sounds.JOrbisAudioStream;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VorbisAudioStream implements CustomAudioStream {
    private final static ByteBuffer EMPTY_BUFFER = BufferUtils.createByteBuffer(0);

    private final JOrbisAudioStream oggAudioStream;
    private final AudioFormat audioFormat;
    private volatile boolean closed;
    private boolean eof;

    public VorbisAudioStream(ByteBuffer byteBuffer) throws IOException, UnsupportedAudioFileException {
        this.oggAudioStream = new JOrbisAudioStream(new ByteBufInputStream(Unpooled.wrappedBuffer(byteBuffer)));
        if (oggAudioStream.getFormat().getChannels() != 1 && oggAudioStream.getFormat().getChannels() != 2) {
            throw new UnsupportedAudioFileException();
        }

        this.audioFormat = new AudioFormat(oggAudioStream.getFormat().getSampleRate(), 16, 1, true, false);
    }

    @Override
    public @NotNull AudioFormat getFormat() {
        return audioFormat;
    }

    @Override
    public @NotNull ByteBuffer read(int size) throws IOException {
        if (eof || closed) {
            return EMPTY_BUFFER;
        }
        var byteBuffer = oggAudioStream.read(oggAudioStream.getFormat().getChannels() * size);
        if (!byteBuffer.hasRemaining()) {
            eof = true;
            return byteBuffer;
        }
        if (oggAudioStream.getFormat().getChannels() == 2) {
            var src = byteBuffer.duplicate().order(ByteOrder.nativeOrder());
            ByteBuffer dst;
            if (!byteBuffer.isReadOnly()) {
                dst = byteBuffer.duplicate().order(ByteOrder.nativeOrder()).limit(src.remaining() / 2);
            } else {
                dst = BufferUtils.createByteBuffer(src.remaining() / 2);
            }
            byteBuffer = dst.slice();
            do {
                var l = src.getShort();
                var r = src.getShort();
                dst.putShort((short) Math.round(((float) l + (float) r) / 2.0f));
            } while (src.hasRemaining());
        }
        return byteBuffer;
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            oggAudioStream.close();
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
