package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupJoinResponseMessage;
import cn.isheihei.chatroom.message.GroupQuitRequestMessage;
import cn.isheihei.chatroom.message.GroupQuitResponseMessage;
import cn.isheihei.chatroom.server.session.Group;
import cn.isheihei.chatroom.server.session.GroupSession;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestHandler  extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String username = msg.getUsername();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeMember(groupName, username);
        if (group != null) {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, username + "退出" + "组" + groupName + "成功"));
        } else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false, "组不存在"));
        }
    }
}
