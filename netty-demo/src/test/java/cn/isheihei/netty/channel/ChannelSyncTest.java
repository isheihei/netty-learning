package cn.isheihei.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ChannelSyncTest {
    /**
     * 测试客户端非阻塞连接，服务器端使用 com.isheihei.netty.eventloop.EvenLoopServer
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // 带有Future ， Promise 的类型都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //1. 连接到服务器
                //异步非阻塞方法，main发起了调用，真正执行 connect 的是 nio线程
                .connect(new InetSocketAddress("localhost", 8080));

        // 2.1 使用 sync 方法 同步处理结果
        channelFuture.sync(); //阻塞住当前线程 知道 nio 线程连接建立完毕

        // 2.2 使用 addListener 方法异步处理结果
/*        channelFuture.addListener(new ChannelFutureListener() {
            *//**
             * 在 nio 线程连接建立好之后 会调用 operationComplete
             * @param channelFuture
             * @throws Exception
             *//*
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
            }
        });*/

        //2.3 无阻塞向下执行 获取 channel
        Channel channel = channelFuture.channel();
        log.debug("{}", channel);
        //向服务器发送数据
        channel.writeAndFlush("hello world!");
    }
}
