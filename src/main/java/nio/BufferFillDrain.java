package nio;

import java.nio.CharBuffer;

public class BufferFillDrain {

    private static int index = 0;
    private static String[] strings = {
            "123",
            "456",
            "1233",
            "4564",
    };

    public static void main(String[] args) {

        CharBuffer charBuffer = CharBuffer.allocate(100);
        while (fillBuffer(charBuffer)) {
            charBuffer.flip();
            drainBuffer(charBuffer);
            charBuffer.clear();
        }

    }

    private static void drainBuffer(CharBuffer charBuffer) {
        while (charBuffer.hasRemaining()) {
            System.out.print(charBuffer.get());
        }
        System.out.println();
    }

    private static boolean fillBuffer(CharBuffer charBuffer) {
        if (index >= strings.length)
            return false;
        String str = strings[index++];
        for (int i = 0; i < str.length(); i++) {
            charBuffer.put(str.charAt(i));
        }
        return true;
    }

}
