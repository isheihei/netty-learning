package cn.isheihei.nettyplus.chatroom;

import cn.isheihei.chatroom.config.Config;
import cn.isheihei.chatroom.message.LoginRequestMessage;
import cn.isheihei.chatroom.message.Message;
import cn.isheihei.chatroom.protocol.MessageCodec;
import cn.isheihei.chatroom.protocol.MessageCodecShareable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecShareable CODEC = new MessageCodecShareable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("张三", "123");
//        embeddedChannel.writeOutbound(message);
        ByteBuf buf = messageToBuf(message);
        embeddedChannel.writeInbound(buf);
    }


    public static ByteBuf messageToBuf(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        //1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 版本
        out.writeByte(1);
        //3. 1 字节的序列化方式 jdk:0, json:1
        out.writeByte(algorithm);
        //4. 1 字节指令类型
        out.writeByte(msg.getMessageType());
        //5. 4 字节的指令序号
        out.writeInt(msg.getSequenceId());
        //6. 对齐填充字节
        out.writeByte(0xff);
        //6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        //7. 长度和序列化后的内容写入
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;

    }
}
