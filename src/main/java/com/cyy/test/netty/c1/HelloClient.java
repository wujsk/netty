package com.cyy.test.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-09 14:36
 **/
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1.启动器
        new Bootstrap()
                // 2.添加eventGroup
                .group(new NioEventLoopGroup())
                // 3.选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        nsc.pipeline().addLast(new StringEncoder()); // 把发送的数据转换为ByteBuf
                    }
                })
                // 5.连接服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync() // 阻塞方法，等待连接建立
                .channel() // 得到channel对象，连接对象
                // 6.向服务器发送数据
                .writeAndFlush("hello,world"); // 发送数据
    }
}
