package cn.isheihei.nio.bytebuffer;

import java.nio.ByteBuffer;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        ByteBufferUtil.debugAll(buffer);
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        buffer.get();
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x66});
        ByteBufferUtil.debugAll(buffer);
    }
}
