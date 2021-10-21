package com.isheihei.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.isheihei.netty.bytebuf.ByteBufUtil.log;
import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestByteBuf {
    public static void main(String[] args) {
        //自动扩容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        System.out.println(buf.getClass());
        log(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        buf.readByte();
        log(buf);


    }


}
