package com.isheihei.netty.eventloop;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        //1. 创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup(4); //io 事件， 普通任务， 定时任务
        //DefaultEventLoop group1 = new DefaultEventLoop();//普通任务，定时任务
        //2. 获取下一个事件循环对象
        EventLoop eventLoop = group.next();
        //3. 执行普通任务
        eventLoop.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });

        //4. 执行定时任务
        EventLoop eventLoop1 = group.next();
        eventLoop1.scheduleAtFixedRate(() -> {
            log.debug("schedule task");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main thread");
    }
}
