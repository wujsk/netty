package com.cyy.test.netty.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.cyy.test.util.ByteBufferUtil.log;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-16 13:36
 **/
public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        byteBuf1.retain();

        ByteBuf byteBuf2 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // 进行两次拷贝
        // byteBuf.writeBytes(byteBuf1).writeBytes(byteBuf2);
        // log(byteBuf);

        // 逻辑复制
        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf.retain();
        // read index:0 write index:0 capacity:0 没有调整写指针
        // buf.addComponents(byteBuf1, byteBuf2);
        // 调整了
        buf.addComponents(true, byteBuf1, byteBuf2);
        byteBuf1.release();
        log(buf);
        buf.release();
    }
}
