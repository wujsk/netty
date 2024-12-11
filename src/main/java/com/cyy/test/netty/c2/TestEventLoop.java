package com.cyy.test.netty.c2;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-10 20:33
 **/
public class TestEventLoop {
    public static void main(String[] args) {
        // 1.创建一个EventLoop对象 构造函数参数为线程数 默认为cpu核数*2
        EventLoopGroup eventExecutors = new NioEventLoopGroup(2); // io事件，普通任务，定时任务
        // EventLoopGroup eventExecutors1 = new DefaultEventLoop(); //普通任务，定时任务
        // i 为 cpu核数
        // int i = NettyRuntime.availableProcessors();
        // System.out.println(i);
        // 2，获取下一个事件循环对象
        // io.netty.channel.nio.NioEventLoop@27c20538
        // io.netty.channel.nio.NioEventLoop@72d818d1
        // io.netty.channel.nio.NioEventLoop@27c20538
        System.out.println(eventExecutors.next());
        System.out.println(eventExecutors.next());
        System.out.println(eventExecutors.next());
        // 3.执行普通任务
        eventExecutors.next().submit(() -> System.out.println(Thread.currentThread().getName())); // nioEventLoopGroup-2-1
        System.out.println(Thread.currentThread().getName()); //main
        // 4.执行定时任务
        // 第一个参数使线程，第二个参数是设置初始延迟，0就是立即执行，1就是1秒后，第三个参数是间隔几秒，第四个参数是时间单位
        eventExecutors.next().scheduleAtFixedRate(() -> System.out.println("这是定时任务"), 0, 1, TimeUnit.SECONDS);
    }
}
