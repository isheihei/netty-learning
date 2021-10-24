package cn.isheihei.nettyplus.chatroom;

import cn.isheihei.chatroom.protocol.MessageCodec;
import cn.isheihei.chatroom.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(),
                //解码器不能被多线程之间共享
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec());

        // encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123");
        //embeddedChannel.writeOutbound(loginRequestMessage);

        //decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, loginRequestMessage, buf);
        //出站
        //embeddedChannel.writeInbound(buf);

        //模拟半包现象
        ByteBuf slice1 = buf.slice(0, 100);
        ByteBuf slice2 = buf.slice(100, buf.readableBytes() - 100);
        //增加引用计数
        slice1.retain();
        // 写入 channel 后会自动 release
        embeddedChannel.writeInbound(slice1);
        embeddedChannel.writeInbound(slice2);
    }
}
