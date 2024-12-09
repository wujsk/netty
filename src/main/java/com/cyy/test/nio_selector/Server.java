package com.cyy.test.nio_selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.cyy.test.util.ByteBufferUtil.debugAll;
import static com.cyy.test.util.ByteBufferUtil.debugRead;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-26 23:06
 **/
public class Server {
    public static void main(String[] args) {
        /**
         * 如果此时客户端被强制断开连接
         * Exception in thread "main" java.lang.RuntimeException: java.net.SocketException: Connection reset
         */
        try {
            // 1.创建selector，管理多个channel
            Selector selector = Selector.open();
            // 创建ServerSocketChannel
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 创建ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(4);
            ssc.configureBlocking(false); // 非阻塞模式
            // 监听端口
            ssc.bind(new InetSocketAddress("localhost", 8080));
            // 2.建立selector和channel之间的联系（注册）
            // SelectionKey就是将来事件发生之后，通过它就知道事件和哪个channel的事件
            SelectionKey sscKey = ssc.register(selector, 0, null);
            // 设置sscKey只关注accept事件
            sscKey.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println(sscKey);
            while (true) {
                // 3.select方法，没有事件发生，线程阻塞，有事件，线程才继续运行,事件完成之后selector会去掉
                selector.select(); // 事件如果没有处理，不会阻塞，事件发生后，要么处理，要么取消，不能置之不理
                // 4.获得到SelectionKey迭代器，包含了所有发生的事件
                // selectedKeys只会加 事件完成之后，不会去掉
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove(); // 切记一定在SelectionKey要删除，否则下一次还会处理事件
                    System.out.println(key);
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept(); // accept相当于处理事件
                        System.out.println(sc);
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, 0, buffer);
                        scKey.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        try {
                            // 正常断开后，客户端会向服务端发送一个read事件，事件也没有处理掉
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                            int len = channel.read(byteBuffer);
                            if (len == -1) {
                                key.cancel();
                            } else {
                                // 如果长度超过4，其余内容会分多次传过来
                                if (byteBuffer.position() == byteBuffer.limit()) {
                                    key.attach(ByteBuffer.allocate(byteBuffer.capacity() * 2));
                                }
                                split(byteBuffer);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            key.cancel(); // 如果客户端强制断开后，因此需要将key取消掉，就是从selector中删除掉
                        }
                    }
                    // key.cancel(); // cancel也相当于处理事件
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
