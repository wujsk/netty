import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-25 22:18
 **/
public class TestByteBuffer {
    @Test
    public void test() {
        // FileChannel
        // 1.输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 从channel读取数据，向buffer写入
            // 打印buffer的内容
            int len = 0;
            while ((len = channel.read(buffer)) != -1) {
                buffer.flip(); // 切换至读模式
                while (buffer.hasRemaining()) {
                    System.out.println((char) buffer.get());
                }
                buffer.clear(); // 切换至写模式
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
