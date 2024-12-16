package com.cyy.test.netty.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.cyy.test.util.ByteBufferUtil.log;
import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-16 10:52
 *
 * ByteBuf优势:
 * 1.池化 - 可以重用池中 ByteBuf 实例，更节约内存，减少内存溢出的可能
 * 2.读写指针分离，不需要像 ByteBuffer 一样切换读写模式
 * 3.可以自动扩容
 * 4.支持链式调用，使用更流畅
 * 5.很多地方体现零拷贝，例如 slice、duplicate、CompositeByteBuf
 **/
public class TestByteBuf {
    public static void main(String[] args) {
        /**
         * ByteBuf默认使用直接内存，而不是使用堆内存
         * ByteByf默认池化
         *
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 300, cap: 512)
         *
         * 不超过最大容量，ByteBuf会根据写入的内容扩展
         */
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        // ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(); //堆内存
        System.out.println(buffer.toString());
        // class io.netty.buffer.PooledUnsafeDirectByteBuf 池化+直接内存
        // 非池化 加上-Dio.netty.allocator.type=unpooled
        System.out.println(buffer.getClass());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append("a");
        }
        buffer.writeBytes(sb.toString().getBytes());
        System.out.println(buffer);
        log(buffer);
    }
}
