package util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

    private UnsafeUtils(){}

    public static Unsafe get() {
        try {
            Field singleton = Unsafe.class.getDeclaredField("theUnsafe");
            singleton.setAccessible(true);
            return (Unsafe) singleton.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

}
