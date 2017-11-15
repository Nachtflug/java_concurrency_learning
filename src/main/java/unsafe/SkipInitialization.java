package unsafe;

import lombok.Getter;
import sun.misc.Unsafe;
import util.UnsafeUtils;

public class SkipInitialization {

    @Getter
    static class A {
        private long a;
        private A() {
            a = 233;
        }
    }

    public static void main(String[] args) throws InstantiationException {
        Unsafe u = UnsafeUtils.get();
        A a = (A) u.allocateInstance(A.class);
        System.out.println(a.getA());
        System.out.println(new A().getA());

    }

}
