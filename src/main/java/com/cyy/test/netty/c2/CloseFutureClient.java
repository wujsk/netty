package com.cyy.test.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-11 09:55
 **/
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                // .group(new NioEventLoopGroup())
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    channel.close(); // close异步操作 1s 之后
                    // System.out.println(Thread.currentThread().getName() + "处理关闭后的操作"); //不能在这里善后
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();

        // 获取closeFuture对象 1.同步处理关闭 2。异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        System.out.println("wait close...");
        /**
         * 10:55:46.887 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x1a404308, L:/127.0.0.1:50744 - R:localhost/127.0.0.1:8080] CLOSE
         * main处理关闭后的操作
         * 10:55:46.890 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x1a404308, L:/127.0.0.1:50744 ! R:localhost/127.0.0.1:8080] INACTIVE
         * 10:55:46.890 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x1a404308, L:/127.0.0.1:50744 ! R:localhost/127.0.0.1:8080] UNREGISTERED
         * 同步关闭
         * closeFuture.sync();
         * System.out.println(Thread.currentThread().getName() + "处理关闭后的操作");
         */
        /**
         * 10:58:09.422 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x7771f669, L:/127.0.0.1:50796 - R:localhost/127.0.0.1:8080] CLOSE
         * nioEventLoopGroup-2-1处理关闭后的操作
         * 10:58:09.422 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x7771f669, L:/127.0.0.1:50796 ! R:localhost/127.0.0.1:8080] INACTIVE
         * 10:58:09.422 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x7771f669, L:/127.0.0.1:50796 ! R:localhost/127.0.0.1:8080] UNREGISTERED
         */
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                group.shutdownGracefully();
                System.out.println(Thread.currentThread().getName() + "处理关闭后的操作");
            }
        });
        /*
         * 但是同步关闭和异步关闭，整个程序却没有关闭(如何优雅的关闭)
         * 1.EventLoopGroup提出来
         * 2.在异步关闭当中加上group.shutdownGracefully();
         */
    }
}
