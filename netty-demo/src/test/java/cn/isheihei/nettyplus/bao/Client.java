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
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 测试黏包和半包
 */
@Slf4j
public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        /**
                         * 会在连接建立之后出发 active 事件
                         * @param ctx
                         * @throws Exception
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //粘包现象
                            for (int i = 0; i < 10; i++) {
                                ByteBuf buffer = ctx.alloc().buffer(16);
                                buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                ctx.writeAndFlush(buffer);
                            }
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
