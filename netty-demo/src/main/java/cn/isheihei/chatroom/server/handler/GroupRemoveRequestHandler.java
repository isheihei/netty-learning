package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.GroupQuitRequestMessage;
import cn.isheihei.chatroom.message.GroupRemoveRequestMessage;
import cn.isheihei.chatroom.message.GroupRemoveResponseMessage;
import cn.isheihei.chatroom.server.session.Group;
import cn.isheihei.chatroom.server.session.GroupSession;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupRemoveRequestHandler extends SimpleChannelInboundHandler<GroupRemoveRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupRemoveRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeGroup(groupName);
        if (group != null) {
            ctx.writeAndFlush(new GroupRemoveResponseMessage(true, "删除组" + group.getName() + "成功"));
        } else {
            ctx.writeAndFlush(new GroupRemoveResponseMessage(false, "组不存在"));
        }

    }
}
