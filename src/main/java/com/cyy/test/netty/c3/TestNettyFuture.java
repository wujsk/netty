package com.cyy.test.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-11 22:36
 * jdk自带的只能同步获取数据，而netty支持异步获取数据
 **/
public class TestNettyFuture {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(() -> {
            System.out.println(Thread.currentThread().getName() + "执行计算");
            // 模拟花费时间
            TimeUnit.SECONDS.sleep(1);
            // 返回值
            return 70;
        });
        // 主线程可以通过future获取结果
        try {
            System.out.println(Thread.currentThread().getName() + "等待结果");
            // 同步等待 阻塞方法
            // Integer result = future.get();
            // 异步获取
            future.addListener(new GenericFutureListener<Future<? super Integer>>() {
                @Override
                public void operationComplete(Future<? super Integer> future) throws Exception {
                    Integer result = (Integer) future.get();
                    System.out.println(Thread.currentThread().getName() + "获取结果：" + result);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
