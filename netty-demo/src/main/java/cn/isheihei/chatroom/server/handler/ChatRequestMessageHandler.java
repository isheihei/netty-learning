package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.ChatRequestMessage;
import cn.isheihei.chatroom.message.ChatResponseMessage;
import cn.isheihei.chatroom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        //在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        //不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方不存在或不在线"));
        }


    }
}
