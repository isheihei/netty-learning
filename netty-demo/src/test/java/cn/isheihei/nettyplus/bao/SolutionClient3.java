package cn.isheihei.nettyplus.bao;

import com.sun.javafx.util.Logging;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 解决方案3 ：client
 * 服务器端基于换行符解码
 */
@Slf4j
public class SolutionClient3 {
    public static void main(String[] args) {
            send();
        log.debug("finish");
    }

    public static StringBuilder makeString(char c, int len) {
        StringBuilder stringBuilder = new StringBuilder(len + 2);
        for (int i = 0; i < len; i++) {
            stringBuilder.append(c);
        }
        stringBuilder.append("\n");
        return stringBuilder;
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            char c = 'c';
                            Random random = new Random();
                            for (int i = 0; i < 10; i++) {
                                StringBuilder stringBuilder = makeString(c, random.nextInt(256));
                                c++;
                                buffer.writeBytes(stringBuilder.toString().getBytes());
                            }
                            ctx.writeAndFlush(buffer);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        }finally {
            worker.shutdownGracefully();
        }
    }
}
