package cn.isheihei.nettyplus.bao;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 解决方案4：LengthFieldBasedFrameDecoder 解析器
 * 预发送长度字段 再进行解析
 */
public class Solution4 {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        //4 个字节的内容长度 然后发实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "hello world!");
        send(buffer, "hi!");
        embeddedChannel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String content) {
        //实际的内容
        byte[] bytes = content.getBytes();
        //实际的内容的长度
        int length = bytes.length;
        buffer.writeInt(length);
        buffer.writeBytes(bytes);
    }
}
