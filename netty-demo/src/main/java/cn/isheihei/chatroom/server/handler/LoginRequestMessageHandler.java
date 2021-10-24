package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.message.LoginRequestMessage;
import cn.isheihei.chatroom.message.LoginResponseMessage;
import cn.isheihei.chatroom.server.service.UserService;
import cn.isheihei.chatroom.server.service.UserServiceFactory;
import cn.isheihei.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage message) throws Exception {
        String username = message.getUsername();
        String password = message.getPassword();
        UserService userService = UserServiceFactory.getUserService();
        LoginResponseMessage response;
        boolean login = userService.login(username, password);
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            response = new LoginResponseMessage(true, "登录成功");
        } else {
            response = new LoginResponseMessage(false, "登录校验失败");
        }
        ctx.writeAndFlush(response);
    }
}
