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

    public static Object shallowCopy(Object o) {
        long size = sizeOf(o);
        long start = toAddress(o);
        long addr = get().allocateMemory(size);
        get().copyMemory(start, addr, size);
        return fromAddress(addr);
    }

    public static Object fromAddress(long addr) {
        Object[] monad = new Object[1];
        long baseOffset = get().arrayBaseOffset(Object[].class);
        get().putLong(monad, baseOffset, addr);
        return monad[0];
    }

    public static long toAddress(Object o) {
        Object[] monad = new Object[] {o};
        long baseOffset = get().arrayBaseOffset(Object[].class);
        return normalize(get().getInt(monad, baseOffset));
    }

    /**
     * Convert signed int to unsigned long
     * @param value
     * @return
     */
    public static long normalize(int value) {
        return value >= 0 ? value : (~0L >>> 32) & value;
    }

}
