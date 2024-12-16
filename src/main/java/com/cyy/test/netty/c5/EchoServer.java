package com.cyy.test.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-16 13:53
 **/
public class EchoServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = msg instanceof ByteBuf ? ((ByteBuf) msg) : null;
                                if (buf != null) {{
                                    System.out.println(buf.toString(Charset.defaultCharset()));
                                    ByteBuf response = ctx.alloc().buffer();
                                    response.writeBytes(buf);
                                    ch.writeAndFlush(response);
                                }}
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                ctx.write(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
