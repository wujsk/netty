import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.cyy.test.util.ByteBufferUtil.debugAll;

/**
 * @program: netty
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-25 22:54
 **/
public class TestByteBufferReadWrite {
    @Test
    public void test() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        debugAll(buffer);
    }
}
