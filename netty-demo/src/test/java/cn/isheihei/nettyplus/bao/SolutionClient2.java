package cn.isheihei.nettyplus.bao;

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
 * 解决方案2 ：client
 * 服务器端定长解码
 */
@Slf4j
public class SolutionClient2 {
    public static void main(String[] args) {
        send();
        log.debug("finished");
    }
    //填充字节 使得返回结果为固定10个长度
    public static byte[] fillBytes(char c, int len) {
        byte[] bytes = new byte[10];
        for (int i = 0; i < 10; i++){
            if (len > 0) {
                bytes[i] = (byte) c;
                len--;
            } else {
                bytes[i] = '_';
            }
        }
        return bytes;
    }
    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel)  {
                    socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            Random random = new Random();
                            char c = '0';
                            for (int i = 0; i < 10; i++) {
                                //随机的填充字符数
                                byte[] bytes = fillBytes(c, random.nextInt(10) + 1);
                                c++;
                                buffer.writeBytes(bytes);
                            }
                            ctx.writeAndFlush(buffer);
                        }
                    });
                }

            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        }finally {
            worker.shutdownGracefully();
        }
    }
}
