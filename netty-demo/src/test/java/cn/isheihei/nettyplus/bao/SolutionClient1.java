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

/**
 * 解决方案1 ：短链接 Client
 * 只能解决黏包问题 不能解决半包
 * 如果服务器端缓冲区或滑动窗口比较小 也会产生半包问题
 */
@Slf4j
public class SolutionClient1 {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            //循环建立连接和发送
            send();
        }
        log.debug("finish");
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
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                //每次发送18个字节
                                ByteBuf buffer = ctx.alloc().buffer(16);
                                buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
                                ctx.writeAndFlush(buffer);
                                //每次发完消息就断开
                                ctx.channel().close();
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
