package com.isheihei.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        //Filechannel
        //1.输入输出流  2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("netty-demo/data.txt").getChannel()) {
            //准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //从channel读取数据,写到buffer
                int len = channel.read(buffer);
                log.debug("读取到的字节数：{}", len);
                if (len == -1) {
                    break;
                }
                //打印buffer的内容
                buffer.flip();//切换至读模式
                while (buffer.hasRemaining()) { //是否还有剩余未读的数据
                    byte b = buffer.get();
                    log.debug("读取到的字节：{}", (char)b);
                }
                buffer.clear();//切换为写模式
            }

        } catch (IOException e) {
        }
    }
}



