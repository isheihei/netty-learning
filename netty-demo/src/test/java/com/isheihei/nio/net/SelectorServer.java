package com.isheihei.nio.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.isheihei.nio.bytebuffer.ByteBufferUtil.debugAll;

@Slf4j
public class SelectorServer {
    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //找到一条完整的消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                //把这条完整的消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                //从 source 去读取，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }

    public static void main(String[] args) throws IOException {
        //1. 创建 selector， 管理多个 channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 事件发生后，可以通过 SelectionKey 可以知道事件和哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(7878));
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //3. selector.select() 方法，没有事件发生，线程阻塞，有事件发生生，线程才会恢复运行
            selector.select();

            //4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //处理 key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iterator.remove();

                log.debug("key: {}", key);
                //5. 区分事件类型
                if (key.isAcceptable()) {   //如果是 accept 事件
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    //attachment 将一个 ByteBuffer 附件关联到 selectionKey 上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    //取消事件
                    //key.cancel();
                    log.debug("{}", sc);
                } else if (key.isReadable()) {  //如果是读事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();  // 拿到触发事件的channel
                        //获取 selectionKey 上关联的事件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);    //如果正常断开 sc.close() 返回值是 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        //因为客户端连接断开了，因此需要将 key 取消 （从 selector 中取消注册）
                        key.cancel();
                    }
                }

            }
        }

    }
}
