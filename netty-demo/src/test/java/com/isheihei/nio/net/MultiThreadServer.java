package com.isheihei.nio.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.isheihei.nio.ByteBuffer.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        //1. 创建固定数量的 worker 并初始化为 CPU 核心数， 但是 docker 下还是会取到物理机的实际核心数 而不是给 docker 分配的核心数
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();

        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}", sc.getRemoteAddress());
                    //2. 关联 selector
                    log.debug("before register...{}", sc.getRemoteAddress());
                    //round robin 轮询
                    workers[index.getAndIncrement() % workers.length].init(sc);
                    log.debug("after register...{}", sc.getRemoteAddress());
                }
            }
        }
    }

    static class  Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean hasStarted = false;//标记是否已经初始化
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        /**
         * 初始化线程和 selector
         */
        public void init(SocketChannel sc) throws IOException {
            if (!hasStarted) {
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                hasStarted = true;
            }
            //向队列添加任务 但是这个任务并没有立即执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();  // 唤醒 select 方法，在没有任何channel注册的时候 selector.select() 会阻塞，需要唤醒才能进行取出注册任务
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select(); //worker0 阻塞
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run(); //执行了   sc.register(selector, SelectionKey.OP_READ, null);
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
