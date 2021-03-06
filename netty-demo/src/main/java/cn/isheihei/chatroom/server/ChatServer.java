package cn.isheihei.chatroom.server;

import cn.isheihei.chatroom.message.GroupCreateRequestMessage;
import cn.isheihei.chatroom.protocol.MessageCodecShareable;
import cn.isheihei.chatroom.protocol.ProtocolFrameDecoder;
import cn.isheihei.chatroom.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecShareable MESSAGE_CODEC = new MessageCodecShareable();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestHandler GROUP_CREATE_HANDLER = new GroupCreateRequestHandler();
        GroupJoinRequestHandler GROUP_JOIN_HANDLER = new GroupJoinRequestHandler();
        GroupMembersRequestHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestHandler();
        GroupQuitRequestHandler GROUP_QUIT_HANDLER = new GroupQuitRequestHandler();
        GroupRemoveRequestHandler GROUP_REMOVE_HANDLER = new GroupRemoveRequestHandler();
        GroupChatRequestHandler GROUP_CHAT_HANDLER = new GroupChatRequestHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //????????????????????? ???????????????????????? ??? ?????????????????????
                    //5s ????????????????????? channel ??????????????????????????? IdleState#READER_IDLE  ??????
                    socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    //ChannelDuplexHandler ??????????????????????????????????????????
                    socketChannel.pipeline().addLast(new ChannelDuplexHandler() {
                        //????????????????????????
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            //????????????????????????
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("?????? 5s ????????????????????????");
                                ctx.channel().close();
                            }
                        }
                    });
                    socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
                    socketChannel.pipeline().addLast(LOGGING_HANDLER);
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    socketChannel.pipeline().addLast(LOGIN_HANDLER);
                    socketChannel.pipeline().addLast(CHAT_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_CREATE_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_JOIN_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_QUIT_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_REMOVE_HANDLER);
                    socketChannel.pipeline().addLast(GROUP_CHAT_HANDLER);
                    socketChannel.pipeline().addLast(QUIT_HANDLER);
                }
            });
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error");
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
