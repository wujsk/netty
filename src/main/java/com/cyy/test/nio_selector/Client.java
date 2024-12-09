package com.cyy.test.nio_selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-26 23:17
 **/
public class Client {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 8080));
            System.out.println("waiting...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
