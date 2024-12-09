package com.cyy.test.test;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-25 22:15
 **/
public class TestByteBuffer {
    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("src/data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 从channel读取数据，向buffer写入
            channel.read(buffer);
            // 打印buffer的内容
            buffer.flip(); // 切换至读模式
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                System.out.println((char) b);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
