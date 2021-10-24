package cn.isheihei.chatroom.server.handler;

import cn.isheihei.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {
    //连接断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{}  已经正常断开", ctx.channel());
    }

    //捕捉到异常时出发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{}  异常断开 异常信息：{}", ctx.channel(), cause.getMessage());
    }
}
