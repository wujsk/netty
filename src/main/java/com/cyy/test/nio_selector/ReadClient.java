package com.cyy.test.nio_selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.cyy.test.util.ByteBufferUtil.debugAll;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-29 14:46
 **/
public class ReadClient {
    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            int count = 0;
            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
                count += sc.read(byteBuffer);
                System.out.println(count);
                byteBuffer.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
