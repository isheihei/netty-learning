package com.isheihei.nio.c1;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.isheihei.nio.c1.ByteBufferUtil.debugAll;


/**
 * 黏包 和 半包 的处理
 */

public class TestByteBufferExam {
    public static void main(String[] args) {

        ByteBuffer source = ByteBuffer.allocate(32);
        source.put(("Hello,world\nI'm zhangsan\nHo").getBytes());
        split(source);
        source.put(("w are you?\n").getBytes());
        split(source);
    }

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
}
