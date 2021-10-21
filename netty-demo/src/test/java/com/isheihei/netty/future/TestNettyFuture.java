package com.isheihei.netty.future;

import com.sun.org.apache.bcel.internal.generic.FSUB;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(4);

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算中");
                Thread.sleep(1000);
                return 60;
            }
        });

        //主线程拿到结果
        //Integer result = future.get();

        //执行线程 nioEventLoopGroup-2-1 拿到结果
        future.addListener(future1 -> log.debug("接收结果：{}", future1.getNow()));
//        log.debug("等待结果");
//        log.debug("结果是{}", result);

    }
}
