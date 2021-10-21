package com.isheihei.nio.bytebuffer;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {
        //class java.nio.HeapByteBuffer：java 堆内存（读写效率比较低）会收到垃圾回收GC的影响
        System.out.println(ByteBuffer.allocate(16).getClass());
        //class java.nio.DirectByteBuffer：直接内存（读写效率比较高，因为少一次拷贝），使用的是系统内存不会受到 GC 的影响
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }



}
