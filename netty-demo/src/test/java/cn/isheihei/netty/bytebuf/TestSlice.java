package cn.isheihei.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static cn.isheihei.netty.bytebuf.ByteBufUtil.log;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);

        //在切片过程中 没有发生数据的复制,切片后的ByteBuf不能增加容量
        ByteBuf sliceBuf1 = buf.slice(0, 5);
        ByteBuf sliceBuf2 = buf.slice(5, 5);

        log(sliceBuf1);
        log(sliceBuf2);
        //修改的是同一片内存
        sliceBuf1.setByte(0, 'b');
        log(sliceBuf1);
        log(buf);


    }
}
