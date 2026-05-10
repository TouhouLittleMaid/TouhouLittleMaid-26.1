package com.github.tartaricacid.touhoulittlemaid.client.sound.data;

import com.github.tartaricacid.touhoulittlemaid.util.OpusDecoderUtil;
import io.github.jaredmdobson.concentus.OpusException;
import net.minecraft.client.sounds.AudioStream;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class OpusAudioStream implements AudioStream {
    private final InputStream stream;
    private final AudioFormat format;
    private final int frameSize;
    private final byte[] frame;

    public OpusAudioStream(byte[] data) throws IOException, OpusException {
        Pair<AudioFormat, byte[]> decoded = OpusDecoderUtil.decode(data);
        this.stream = new ByteArrayInputStream(decoded.getRight());
        this.format = decoded.getLeft();
        this.frameSize = this.format.getFrameSize();
        this.frame = new byte[this.frameSize];
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    /**
     * 从流中读取音频数据，并返回一个最多包含指定字节数的字节缓冲区。
     * 该方法从流中读取音频帧并将其添加到输出缓冲区，直到缓冲区至少
     * 包含指定数量的字节或到达流的末尾。
     *
     * @param size 要读取的最大字节数
     * @return 字节缓冲区，最多包含要读取的指定字节数
     * @throws IOException 如果在读取音频数据时发生I/O错误
     */
    @Override
    public ByteBuffer read(int size) throws IOException {
        // 创建指定大小的ByteBuffer
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(size);
        int bytesRead = 0, count = 0;
        // 循环读取数据直到达到指定大小或输入流结束
        do {
            // 读取下一部分数据
            count = this.stream.read(frame);
            // 将读取的数据写入ByteBuffer
            if (count != -1) {
                byteBuffer.put(frame);
            }
        } while (count != -1 && (bytesRead += frameSize) < size);
        // 翻转ByteBuffer，准备进行读取操作
        byteBuffer.flip();
        // 返回包含读取数据的ByteBuffer
        return byteBuffer;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
