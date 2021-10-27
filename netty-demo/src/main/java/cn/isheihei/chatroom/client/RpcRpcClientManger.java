package cn.isheihei.chatroom.client;

import cn.isheihei.chatroom.message.RpcRequestMessage;
import cn.isheihei.chatroom.protocol.MessageCodecShareable;
import cn.isheihei.chatroom.protocol.ProtocolFrameDecoder;
import cn.isheihei.chatroom.protocol.SequenceIdGenerator;
import cn.isheihei.chatroom.client.handler.RpcResponseMessageHandler;
import cn.isheihei.chatroom.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcRpcClientManger {
    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        proxyService.sayHello("zhangsan");
        proxyService.sayHello("lisi");
        proxyService.sayHello("wangwu");
    }
    private static Channel channel = null;
    private static final Object LOCK = new Object();

    //获取唯一的 channel 对象
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK){
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    //创建代理类
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        //                                                           sayHello  "zhangsan"
        Object o = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            //1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            //2. 将消息对象发送出去
            getChannel().writeAndFlush(msg);
            //3. 准备一个空的 Promise 对象，来接收结果           指定 promise 对象异步接收结果的线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);
            //等待 promise 的结果
            promise.await();
            if (promise.isSuccess()) {
                //正常调用
                return promise.getNow();
            } else {
                //调用失败
                throw new RuntimeException(promise.cause());
            }

        }));
        return (T) o;
    }

    //初始化channel
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecShareable MESSAGE_CODEC = new MessageCodecShareable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8000).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}