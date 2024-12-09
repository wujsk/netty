package com.cyy.test.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cyy.test.util.ByteBufferUtil.debugRead;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-26 23:06
 **/
public class Server {
    public static void main(String[] args) {
        // 使用nio理解阻塞模式 单线程
        try {
            // 创建ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 创建服务器
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false); // 非阻塞模式
            // 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));
            // 连接集合
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // accept 建立与客户端的连接，SocketChannel用来与客户端之间通信
                // System.out.println("connect...");
                // 阻塞方法，线程停止运行
                SocketChannel accept = ssc.accept(); // 本是阻塞的，如果是非阻塞模式，线程会继续运行，但是返回的accept为null
                if (!Objects.isNull(accept)) {
                    System.out.println("connect:" + accept);
                    accept.configureBlocking(false);
                    channels.add(accept);
                }
                channels.forEach(channel -> {
                    try {
                        // System.out.println("before read..." + channel);
                        // read也会阻塞
                        int read = channel.read(buffer);// 本是阻塞的，如果是非阻塞模式，线程会继续运行，但是返回的结果为0
                        buffer.flip();
                        if (read != 0) {
                            debugRead(buffer);
                        }
                        buffer.clear();
                        // System.out.println("read after..." + channel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
