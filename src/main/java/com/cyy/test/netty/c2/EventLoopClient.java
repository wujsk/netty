package com.cyy.test.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-10 21:01
 **/
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 2.带有Future，promise的类型都是和异步方法配套使用，用来处理结果
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                // 设置channel类型
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1.异步非阻塞，main发起调用。真正执行connect的是nio线程
                .connect(new InetSocketAddress("localhost", 8080))
                // 阻塞，等待连接，直到nio线程连接建立完毕
                // 2.1 使用sync()方法同步处理结果
                .sync() // 如果将其注释
                // 2.2 使用addListener(回调对象)方法异步处理结果
                // .addListener(new ChannelFutureListener() {
                //     @Override
                //     // 在nio线程建立连接完成后，会调用operationComplete
                //     public void operationComplete(ChannelFuture future) throws Exception {
                //         Channel channel1 = future.channel(); // 与下面一样
                //         // 建立成功 [id: 0xbb2ac082, L:/127.0.0.1:61495 - R:localhost/127.0.0.1:8080]
                //         System.out.println(channel1);
                //         System.out.println("");
                //     }
                // });
                // 非阻塞获取channel
                .channel();
        // channel.writeAndFlush() //写完数据并立即打出
        // channel.write() //写完数据不打出
        // channel.flush() //配合上一步
        // 不注释sync() [id: 0xe2d5f1e1, L:/127.0.0.1:60514 - R:localhost/127.0.0.1:8080]
        // 注释 [id: 0x344f6915] 为建立连接
        System.out.println(channel);
        System.out.println("");
    }
}
