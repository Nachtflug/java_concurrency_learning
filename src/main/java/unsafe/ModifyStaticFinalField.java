package unsafe;

import sun.misc.Unsafe;
import util.UnsafeUtils;

import java.lang.reflect.Field;

public class ModifyStaticFinalField {

    private static final int a = new Integer(1);
//    private static final int a = 1; //cannot modify const in constant pool

    public static void main(String[] args) throws NoSuchFieldException {

        //System.out.println(a);

        Field field = ModifyStaticFinalField.class.getDeclaredField("a");
        Unsafe u = UnsafeUtils.get();
        long offset = u.staticFieldOffset(field);
        Object base = u.staticFieldBase(field);
        System.out.printf("base: %s, offset: %d\n", base, offset);
        System.out.println(u.getInt(ModifyStaticFinalField.class, offset));
        u.putInt(base, offset, 15);
        System.out.println(a);
        System.out.println(u.getInt(ModifyStaticFinalField.class, offset));


    }

}
