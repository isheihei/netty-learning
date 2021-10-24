package cn.isheihei.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

public class TestByteBuf {
    public static void main(String[] args) {
        //自动扩容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        System.out.println(buf.getClass());
        ByteBufUtil.log(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        buf.readByte();
        ByteBufUtil.log(buf);


    }


}
