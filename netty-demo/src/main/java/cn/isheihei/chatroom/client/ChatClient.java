package cn.isheihei.chatroom.client;

import cn.isheihei.chatroom.message.*;
import cn.isheihei.chatroom.protocol.MessageCodecShareable;
import cn.isheihei.chatroom.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecShareable MESSAGE_CODEC = new MessageCodecShareable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);


        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    //用来判断是不是 读空闲时间过长， 或 写事件空闲过长
                    //3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITE_IDLE  事件
                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    //ChannelDuplexHandler 可以同时作为入站和出站处理器
                    socketChannel.pipeline().addLast(new ChannelDuplexHandler() {
                        //用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            //触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
//                                log.debug("3s 没有写数据了 自动发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    socketChannel.pipeline().addLast("client Handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // 如果登录成功
                                    LOGIN.set(true);
                                }
                                // 唤醒 system in 线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        /**
                         * 在连接建立后出发 active 事件
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //负责接收用户在控制台的输入，负责想用户服务器发送各种消息
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String userName = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                //构造消息对象
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(userName, password);
                                //发送消息
                                ctx.writeAndFlush(loginRequestMessage);
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //  如果登陆失败
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("gremove [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(userName, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(userName, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            //加入自己
                                            set.add(userName);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(userName, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(userName, s[1]));
                                            break;
                                        case "gremove":
                                            ctx.writeAndFlush(new GroupRemoveRequestMessage(s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }

                            }, "system in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
