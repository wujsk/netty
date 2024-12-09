package com.cyy.test.nio_selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.cyy.test.util.ByteBufferUtil.debugAll;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-29 14:19
 **/
public class WriteServer {
    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ByteBuffer buffer = ByteBuffer.allocate(4);
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress("localhost", 8080));
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // selector是一个容器 SelectionKey也是一个容器当事件完成之后，事件不会被移除，需要手动移除
                    iterator.remove(); // 切记一定要remove -.-
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);
                        String str = "a".repeat(30000000);
                        // write代表实际写入的字节数
                        ByteBuffer byteBuffer = Charset.defaultCharset().encode(str);
                        int write = sc.write(byteBuffer);
                        System.out.println(write);
                        // 判断是否有数据
                        if (byteBuffer.hasRemaining()) {
                            // 如果还有数据，让它的事件变为读事件
                            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                            // 将未写完的数据，挂到scKey上
                            scKey.attach(byteBuffer);
                        }
                    } else if (key.isReadable()) {
                        try {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                            int read = sc.read(byteBuffer);
                            if (read == -1) {
                                key.cancel();
                            } else {
                                split(byteBuffer);
                                if (byteBuffer.position() == byteBuffer.limit()) {
                                    key.attach(ByteBuffer.allocate(byteBuffer.capacity() * 2));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            key.cancel();
                        }
                    } else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        int write = sc.write(byteBuffer);
                        System.out.println(write);
                        if (!byteBuffer.hasRemaining()) {
                            // 如果没有数据后，将attach设为null
                            key.attach(null);
                            // 无需关注写事件
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        }

                    }
                }
            }
        } catch (IOException e) {
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
