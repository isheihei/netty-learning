package com.isheihei.nio.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.isheihei.nio.bytebuffer.ByteBufferUtil.debugAll;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用 nio 来理解阻塞模式
        //0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        //1. 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //切换成非阻塞模式
        ssc.configureBlocking(false);
        //2. 绑定监听端口
        ssc.bind(new InetSocketAddress(7878));

        //3. 连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //4. accept 建立客与客户端的连接, SocketChannel 用来与客户端进行通信
            //log.debug("try connecting...");
            SocketChannel sc = ssc.accept();    //阻塞方法，线程停止运行   非阻塞模式下，如果没有建立连接，但是 sc 为 null
            if (sc != null) {
                log.debug("connected... {}", sc);
                channels.add(sc);
            }
            //5. 接受客户端发送的数据
            for (SocketChannel channel : channels) {
                //log.debug("before read ... {}", channel);
                int read = channel.read(buffer);// 阻塞方法，线程停止运行  非阻塞模式下，线程会继续运行，如果没读到数据 read 会返回 0
                if (read > 0) {
                    buffer.flip();
                    debugAll(buffer);
                    buffer.clear();
                    log.debug("after read ...{}", channel);
                }
            }
        }

    }
}
