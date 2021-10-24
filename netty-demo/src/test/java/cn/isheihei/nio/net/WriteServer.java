package cn.isheihei.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey scKey = ssc.register(selector, 0, null);
        scKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    //1. 向客户端发送大量的数据
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        stringBuilder.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());

                    //2. 返回值代表实际写入的字节数
                    int write = sc.write(buffer);
                    System.out.println(write);

                    //3. 判断是否还有剩余内容
                    if (buffer.hasRemaining()) {
                        //4. 关注一个可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        //scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        //5.  把未写完的数据挂到 scKey 上
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    //6. 清理操作
                    if (!buffer.hasRemaining()) {
                        key.attach(null);   // 如果内容都写完了 需要清楚 buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE); //不需要关注可写事件
                    }
                }
            }
        }

    }
}
