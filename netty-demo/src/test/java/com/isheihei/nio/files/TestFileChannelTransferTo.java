package com.isheihei.nio.files;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("netty-demo/data.txt").getChannel();
                FileChannel to = new FileOutputStream("netty-demo/to.txt").getChannel();
        ) {
            //比用文件输入输出流效率高，底层会利用操作系统的零拷贝进行优化，最多传递 2g 数据
            long size = from.size();
            //left 代表还剩余多少字节
            for (long left = size; left > 0; ) {
                System.out.println("position : " + (size - left) + " left: " + left);
                left -= from.transferTo(size - left, from.size(), to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
