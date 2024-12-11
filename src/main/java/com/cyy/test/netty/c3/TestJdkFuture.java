package com.cyy.test.netty.c3;

import java.util.concurrent.*;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-11 22:28
 **/
public class TestJdkFuture {
    public static void main(String[] args) {
        // 1.线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        // 2.提交任务
        Future<Integer> future = threadPool.submit(() -> {
            System.out.println(Thread.currentThread().getName() + "执行计算");
            // 模拟花费时间
            TimeUnit.SECONDS.sleep(1);
            // 返回值
            return 50;
        });
        // 主线程可以通过future获取结果
        try {
            // 同步等待 阻塞方法
            System.out.println(Thread.currentThread().getName() + "等待结果");
            Integer result = future.get();
            System.out.println(Thread.currentThread().getName() + "获取结果：" + result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
