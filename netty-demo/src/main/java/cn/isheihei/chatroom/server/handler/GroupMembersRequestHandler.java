package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupMembersRequestMessage;
import cn.isheihei.chatroom.message.GroupMembersResponseMessage;
import cn.isheihei.chatroom.server.session.GroupSession;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestHandler  extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(groupName);
        if (members.isEmpty()) {
            ctx.writeAndFlush(new GroupMembersResponseMessage(false, groupName + "组不存在或组为空", members));
        } else {
            ctx.writeAndFlush(new GroupMembersResponseMessage(true, groupName + "组成员查询成功", members));

        }
    }
}
