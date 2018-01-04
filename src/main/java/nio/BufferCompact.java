package nio;

import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;

public class BufferCompact {

    public static void main(String[] args) {
        CharBuffer buffer = CharBuffer.allocate(20);
        buffer.put('a').put('b').put('c').put('d').put('e');
        buffer.flip();
        buffer.get();
        buffer.get();
        buffer.compact();
        System.out.printf("limit %d, pos %d\n", buffer.limit(), buffer.position());
//        buffer.flip();
        System.out.println(buffer.get());
        System.out.println(ByteOrder.nativeOrder());
    }

}
