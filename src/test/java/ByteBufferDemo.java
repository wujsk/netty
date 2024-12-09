import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.cyy.test.util.ByteBufferUtil.debugAll;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-26 08:05
 **/
public class ByteBufferDemo {
    @Test
    public void test() {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,World\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you\n".getBytes());
        split(source);
    }
    public void split(ByteBuffer source) {
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
