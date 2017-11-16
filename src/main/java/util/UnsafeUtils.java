package util;

import com.google.common.collect.Sets;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

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

    public static long sizeOf(Object o) {
        Unsafe unsafe = get();
        HashSet<Field> fields = Sets.newHashSet();
        Class c = o.getClass();
        while (c != Object.class) {
            Arrays.stream(c.getDeclaredFields())
                    .filter(f -> (f.getModifiers() & Modifier.STATIC) == 0)
                    .forEach(fields::add);
            c = c.getSuperclass();
        }
        long maxSize = fields.stream()
                .mapToLong(unsafe::objectFieldOffset)
                .max().getAsLong();   //last field offset
        return ((maxSize / 8) + 1) * 8;  // padding
    }


    /**
     * Convert signed int to unsigned long
     * @param value
     * @return
     */
    private static long normalize(int value) {
        return value >= 0 ? value : (~0L >>> 32) & value;
    }

}
