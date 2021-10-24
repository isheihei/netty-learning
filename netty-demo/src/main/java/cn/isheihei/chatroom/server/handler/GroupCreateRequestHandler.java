package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.ChatResponseMessage;
import cn.isheihei.chatroom.message.GroupCreateRequestMessage;
import cn.isheihei.chatroom.message.GroupCreateResponseMessage;
import cn.isheihei.chatroom.server.session.Group;
import cn.isheihei.chatroom.server.session.GroupSession;
import cn.isheihei.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            //发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            //发送拉群消息给被拉入群中的客户端
            List<Channel> membersChannels = groupSession.getMembersChannel(groupName);
            for (Channel membersChannel : membersChannels) {
                membersChannel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + groupName));
            }
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已经存在"));
        }
    }
}
