package com.cyy.test.multi_thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cyy.test.util.ByteBufferUtil.debugAll;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-29 16:40
 **/
public class MultiThreadServer {
    public static void main(String[] args) {
        try {
            Thread.currentThread().setName("boss");
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress("localhost", 8080));
            ssc.configureBlocking(false);
            Selector boss = Selector.open();
            ssc.register(boss, SelectionKey.OP_ACCEPT, null);
            // 1.创建固定数量worker
            Worker worker1 = new Worker("worker-1");
            // worker1.register();
            Worker worker2 = new Worker("worker-2");
            Worker[] workers = new Worker[]{worker1, worker2};
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
                        System.out.println("connected..." + sc.getRemoteAddress());
                        // 2.关联
                        System.out.println("before" + sc.getRemoteAddress());
                        workers[index.getAndIncrement() % workers.length].register(sc);
                        System.out.println("after" + sc.getRemoteAddress());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Worker {
        private Thread thread;
        private Selector worker;
        private String threadName;
        private boolean start = false; // 未启动
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String threadName) {
            this.threadName = threadName;
        }

        // 初始化线程和selector
        public void register(SocketChannel sc) throws Exception {
            if (!start) {
                worker = Selector.open();
                thread = new Thread(() -> {
                    while (true) {
                        try {
                            worker.select();
                            // Runnable task = queue.poll();
                            // if (task != null) {
                            //     task.run(); // 执行了sc.register(worker, SelectionKey.OP_READ, null);
                            // }
                            Iterator<SelectionKey> iterator = worker.selectedKeys().iterator();
                            while (iterator.hasNext()) {
                                SelectionKey key = iterator.next();
                                iterator.remove();
                                if (key.isReadable()) {
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    try (SocketChannel socketChannel = (SocketChannel) key.channel()) {
                                        System.out.println("read..." + socketChannel.getRemoteAddress());
                                        socketChannel.read(buffer);
                                    }
                                    buffer.flip();
                                    debugAll(buffer);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, threadName);
                start = true;
            }
            // queue.add(() -> {
            //     try {
            //         sc.register(worker, SelectionKey.OP_READ, null);
            //     } catch (ClosedChannelException e) {
            //         throw new RuntimeException(e);
            //     }
            // });
            // worker.wakeup(); // 唤醒select方法
            worker.wakeup();
            sc.register(worker, SelectionKey.OP_READ, null);
        }
    }
}
