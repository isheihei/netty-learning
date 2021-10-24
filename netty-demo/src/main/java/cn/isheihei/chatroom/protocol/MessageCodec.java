package cn.isheihei.chatroom.protocol;

import cn.isheihei.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 自定义协议
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    public void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
        //1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 版本
        out.writeByte(1);
        //3. 1 字节的序列化方式 jdk:0, json:1
        out.writeByte(0);
        //4. 1 字节指令类型
        out.writeByte(message.getMessageType());
        //5. 4 字节的指令序号
        out.writeInt(message.getSequenceId());
        //6. 对齐填充字节
        out.writeByte(0xff);
        //6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes = bos.toByteArray();
        //7. 长度和序列化后的内容写入
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }

    @Override
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int contentLength = in.readInt();
        byte[] bytes = new byte[contentLength];
        ByteBuf content = in.readBytes(bytes, 0, contentLength);
        if (serializerType == 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Message message = (Message) ois.readObject();
            log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, contentLength);
            log.debug("{}", message);
            out.add(message);
        }
    }
}
