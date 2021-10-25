package cn.isheihei.chatroom.protocol;

import cn.isheihei.chatroom.config.Config;
import cn.isheihei.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 必须和 LengthFiledBasedFrameDecoder 一起使用 确保接收到的 ByteBuf 的消息是完整的
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecShareable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> outList) throws Exception {
        ByteBuf out = channelHandlerContext.alloc().buffer();
        //1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 版本
        out.writeByte(1);
        //3. 1 字节的序列化方式 jdk:0, json:1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        //4. 1 字节指令类型
        out.writeByte(message.getMessageType());
        //5. 4 字节的指令序号
        out.writeInt(message.getSequenceId());
        //6. 对齐填充字节
        out.writeByte(0xff);
        //6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serializer(message);
        //7. 长度和序列化后的内容写入
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte(); //0 或 1
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int contentLength = in.readInt();
        byte[] bytes = new byte[contentLength];
        ByteBuf content = in.readBytes(bytes, 0, contentLength);

        //找到具体的序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];

        //确定具体的消息类型
        Class messageClass = Message.getMessageClass(messageType);
        Object deserialize = algorithm.deserialize(messageClass, bytes);
        out.add(deserialize);
    }
}
