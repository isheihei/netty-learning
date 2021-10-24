package cn.isheihei.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 7878));
        sc.write(Charset.defaultCharset().encode("0123456789abc\ndefghijklmnop\n222"));
        sc.write(Charset.defaultCharset().encode("012\ndefghijklmnop\n"));
        System.in.read();
        System.out.println("waiting...");
    }
}
