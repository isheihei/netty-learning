package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupJoinRequestMessage;
import cn.isheihei.chatroom.message.GroupJoinResponseMessage;
import cn.isheihei.chatroom.server.session.Group;
import cn.isheihei.chatroom.server.session.GroupSession;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupJoinRequestHandler  extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String username = msg.getUsername();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(groupName, username);////如果组不存在返回 null, 否则返回组对象
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, groupName + "添加成员" + username + "成功"));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, "组不存在，添加成员失败"));
        }

    }
}
