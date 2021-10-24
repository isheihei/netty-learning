package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupChatRequestMessage;
import cn.isheihei.chatroom.message.GroupChatResponseMessage;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import cn.isheihei.chatroom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestHandler  extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> membersChannels = GroupSessionFactory.getGroupSession()
                .getMembersChannel(msg.getGroupName());
        for (Channel membersChannel : membersChannels) {
            membersChannel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }


    }
}
