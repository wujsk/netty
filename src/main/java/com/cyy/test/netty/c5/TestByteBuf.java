package com.cyy.test.netty.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @program: netty
 * @description:
 * @author: cyy
 * @create: 2024-12-16 10:52
 **/
public class TestByteBuf {
    public static void main(String[] args) {
        /**
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 300, cap: 512)
         */
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buffer.toString());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append("a");
        }
        buffer.writeBytes(sb.toString().getBytes());
        System.out.println(buffer);
        log(buffer);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
