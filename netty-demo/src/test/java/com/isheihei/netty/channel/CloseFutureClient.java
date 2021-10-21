package com.isheihei.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 测试关闭 Future 关闭， server 是 com.isheihei.netty.eventloop.EvenLoopServer
 */
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = channelFuture.sync().channel();

        new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String line = scanner.nextLine();
                        if ("q".equals(line)) {
                            channel.close(); //close 也是异步操作 执行到下面的代码时 可能还没有彻底关闭
                            break;
                        }
                        channel.writeAndFlush(line);
                    }
                }, "input").start();

        // 获取 ClosedFuture 对象， 1）同步处理关闭， 2）异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();

//        System.out.println("waiting close ... ");
//        closeFuture.sync(); //同步处理
//        log.debug("处理关闭之后的操作");

        //异步处理
        closeFuture.addListener((ChannelFutureListener) (future) -> {
            log.debug("处理关闭之后的操作");
            //优雅的把 group 里面的线程关闭
            group.shutdownGracefully();
        });
    }
}
