package com.cyy.test.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @program: netty
 * @description: 基于netty的服务端
 * @author: cyy
 * @create: 2024-12-09 14:11
 **/
public class HelloServer {
    public static void main(String[] args) {
        // 1.启动器
        new ServerBootstrap()
                // 2.BossEventLoop,WorkerEventLoop(selector, thread) group 组
                // 由某个EventLoopGroup处理read事件，接收到ByteBuf
                .group(new NioEventLoopGroup()) //accept read
                // 3.选择 服务端NioServerSocketChannel 的实现
                .channel(NioServerSocketChannel.class)
                // 4.boss负责连接 worker（child）负责读写，决定了worker（child）能执行哪些操作
                .childHandler(
                    // 5.channel代表和客户端进行数据读写的通道lInitializer初始化，负责添加别的handler
                    new ChannelInitializer<NioSocketChannel>() {
                    // 连接建立后调用初始化方法
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 6.添加具体的handler
                        nsc.pipeline().addLast(new StringDecoder()); // 将ByteBuf转换为字符串
                        nsc.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler
                            @Override // 读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 执行read方法，打印上一步转换好的字符串
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
