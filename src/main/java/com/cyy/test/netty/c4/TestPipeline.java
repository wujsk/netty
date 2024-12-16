package com.cyy.test.netty.c4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-11 23:08
 **/
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1.通过channel拿到pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        // 2.添加处理器 head -> tail addLast: head -> h1 -> tail 不是真的加在末尾 pipeline本身就有两个处理器
                        // head -> handler1 -> handler2 -> handler3 -> handler4 -> handler5 -> handler6 -> tail
                        // 入站是按照添加handler的顺序，出战就是反过来
                        // nioEventLoopGroup-2-2===1
                        // nioEventLoopGroup-2-2===2
                        // nioEventLoopGroup-2-2===3
                        // nioEventLoopGroup-2-2===6
                        // nioEventLoopGroup-2-2===5
                        // nioEventLoopGroup-2-2===4
                        pipeline.addLast("handler1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===1");
                                ByteBuf byteBuf = (ByteBuf) msg;
                                super.channelRead(ctx, byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                        pipeline.addLast("handler2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===2");
                                Student student = new Student(msg.toString());
                                // 将接收到的数据传递给下一个handler，等同于ctx.fireChannelRead(msg)，如果不传递，传递链会断开。
                                super.channelRead(ctx, student);
                            }
                        });
                        pipeline.addLast("handler4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("handler3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===3");
                                System.out.println(msg.getClass() + ":" + msg.toString());
                                super.channelRead(ctx, msg);
                                // 只有写，才能够触发出战handler
                                // ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes())); // 从尾部找出栈handler
                                ctx.write(ctx.alloc().buffer().writeBytes("server...".getBytes())); // 从当前位置向后查找出栈handler
                            }
                        });
                        pipeline.addLast("handler5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("handler6", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println(Thread.currentThread().getName() + "===6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }

    @Data
    @AllArgsConstructor
    static class Student {
        private String name;
    }
}
