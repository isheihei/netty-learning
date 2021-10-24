package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupMembersRequestMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupMembersRequestHandler  extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMembersRequestMessage groupMembersRequestMessage) throws Exception {

    }
}
