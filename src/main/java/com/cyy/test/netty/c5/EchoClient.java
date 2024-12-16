package com.cyy.test.netty.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-16 14:02
 **/
public class EchoClient {
    public static void main(String[] args) {
        try {
            Channel channel = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = msg instanceof ByteBuf ? (ByteBuf) msg : null;
                                    if (byteBuf != null) {
                                        System.out.println(byteBuf.toString(Charset.defaultCharset()));
                                    }
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 8080))
                    .sync()
                    .channel();
            new Thread(() -> {
                while(true) {
                    Scanner scanner = new Scanner(System.in);
                    String str = scanner.nextLine();
                    if ("q".equals(str)) {
                        channel.close();
                        break;
                    }
                    channel.writeAndFlush(str);
                }
            }).start();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
