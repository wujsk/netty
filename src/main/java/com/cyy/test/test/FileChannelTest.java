package com.cyy.test.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-26 22:13
 **/
public class FileChannelTest {
    public static void main(String[] args) {
        try (FileChannel source = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel()
            ) {
            // 效率高，比io流高，底层利用操作系统零拷贝进行优化，一次最多传输2g数据
            long size = source.size();
            for (long len = size; len > 0;) {
                len -= source.transferTo((size - len), source.size(), to);
            }
        } catch (IOException e) {
        }
    }
}
