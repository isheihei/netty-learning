package cn.isheihei.nio.bytebuffer;

import java.nio.ByteBuffer;

import static cn.isheihei.nio.bytebuffer.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        //从头开始读
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind();
//        System.out.println((char) buffer.get());
//        debugAll(buffer);

        //mark & reset
        //mark:做一个标记，记录 position 位置， reset 是将 position 重置到 mark 的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark();//mark 索引2的位置
        System.out.println((char) buffer.get());
        buffer.reset();//把索引恢复到索引2的位置
        System.out.println((char) buffer.get());
        debugAll(buffer);

        // buffer.get(i)后不会改变读索引的位置
        System.out.println((char) buffer.get(3));
        debugAll(buffer);


    }

}
