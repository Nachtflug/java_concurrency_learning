package unsafe;

import sun.misc.Unsafe;

import static util.UnsafeUtils.get;

public class MultipleInheritance {

    public static void main(String[] args) {

        cheat();
        Object nerdString = 666;
        System.out.println(nerdString instanceof String); //just kidding
    }

    private static void cheat() {
        Unsafe unsafe = get();
        long intClassAddr = unsafe.getLong(0, 8L);
        long strClassAddr = unsafe.getLong("", 8L);
        unsafe.putAddress(intClassAddr + 48, strClassAddr);
    }

}
