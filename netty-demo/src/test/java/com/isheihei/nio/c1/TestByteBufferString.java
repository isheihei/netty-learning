package com.isheihei.nio.c1;

import com.sun.corba.se.impl.orbutil.ObjectStreamClassUtil_1_3;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.isheihei.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        //字符串转为ByteBuffer
        // 1. getBytes()
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        //2. charSet
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");    //创建后自动切换为读模式
        debugAll(buffer1);

        //3. wrap
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());   //创建后自动切换为读模式
        debugAll(buffer2);

        //ByteBuffer转换为字符串：
        String str = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str);

        //如果使用第一种方法转成Bytebuffer，需要手动切换到读模式才可以进行ByteBuffer -> 字符串的转换
        buffer.flip();
        String str1 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(str1);
    }
}
