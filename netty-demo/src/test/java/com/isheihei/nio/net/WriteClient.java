package com.isheihei.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        //3. 接收数据
        int count = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        while (true) {
            int read = sc.read(buffer);
            count += read;
            System.out.println(count);
            buffer.clear();
        }
    }
}
