package com.cyy.test.netty.c3;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-11 22:54
 **/
public class TestNettyPromise {
    public static void main(String[] args) {
        // 1.需要一个EventLoop对象
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 2. 可以主动创建promise
        DefaultPromise<Integer> promise = new DefaultPromise<>(group.next());
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "执行计算");
            // 模拟花费时间
            try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {throw new RuntimeException(e);}
            // 填充成功的结果
            try {
                // int i = 1 / 0;
                promise.setSuccess(100);
            } catch (Exception e) {
                // 处理错误结果
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();
        System.out.println(Thread.currentThread().getName() + "等待结果");

        // 同步得到结果
        try {
            Integer result = promise.get();
            System.out.println(Thread.currentThread().getName() + "获取结果：" + result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 异步得到结果
        // promise.addListener(new GenericFutureListener<Future<? super Integer>>() {
        //     @Override
        //     public void operationComplete(Future<? super Integer> future) throws Exception {
        //         Integer result = (Integer) future.get();
        //         System.out.println(Thread.currentThread().getName() + "获取结果：" + result);
        //     }
        // });
    }
}
