package unsafe;

import sun.misc.Unsafe;

import static util.UnsafeUtils.get;
import static util.UnsafeUtils.normalize;

public class MultipleInheritanceCOOPs {

    public static void main(String[] args) {

        Unsafe unsafe = get();
        long intClassAddr = normalize(unsafe.getInt(0, 8L)) << 3;
        long strClassAddr = normalize(unsafe.getInt("", 8L)) << 3;
        unsafe.putAddress(intClassAddr + 48, strClassAddr); // oop not compressed in klass

        Object nerdString = 666;
        System.out.println(nerdString instanceof String);
    }

}
