package com.cyy.test.netty.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.cyy.test.util.ByteBufferUtil.log;

/**
 * @program: netty
 * @description: 零拷贝
 * @author: cyy
 * @create: 2024-12-16 13:17
 * slice零拷贝的体现之一，对原始ByteBuf进行切片成多个ByteBuf，切片后的ByteBuf并没有发生内存复制，还是使用
 * 原始ByteBuf的内存，切片后的ByteBuf维护独立的read、write指针
 *
 * duplicate是零拷贝ByteBuf整个空间，共用同一个内存空间
 *
 * copy是直接复制数据，不工用同一个内存空间
 **/
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        log(byteBuf);

        ByteBuf byteBuf1 = byteBuf.slice(0, 5);
        byteBuf1.retain();
        ByteBuf byteBuf2 = byteBuf.slice(5, 5);
        log(byteBuf1);
        log(byteBuf2);

        System.out.println("=============");
        byteBuf1.setByte(0, 10);
        log(byteBuf1);
        log(byteBuf);

        // 不允许向切片后的ByteBuf中添加数据
        // byteBuf1.writeByte(1);

        // 释放ByteBuf空间，对切片后的ByteBuf有影响
        // 可以让切片调用retain()
        byteBuf.release();
        log(byteBuf1);
        byteBuf1.release();
    }
}
