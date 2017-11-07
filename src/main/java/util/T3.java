package util;

import com.google.common.collect.Lists;

import java.util.Iterator;

public class T3<X, Y, Z> extends T2<X, Y> {

    Z z;

    private T3(X x, Y y, Z z) {
        super(x, y);
        this.z = z;
    }

    public Z _3() {
        return z;
    }

    @Override
    public <N> T3<N, Y, Z> set1(N n) {
        return new T3<>(n, y, z);
    }

    @Override
    public <N> T3<X, N, Z> set2(N n) {
        return new T3<>(x, n, z);
    }

    public <N> T3<X, Y, N> set3(N n) {
        return new T3<>(x, y, n);
    }

    public static <X, Y, Z> T3<X, Y, Z> of(X x, Y y, Z z) {
        return new T3<>(x, y, z);
    }

    @Override
    public Iterator<Object> iterator() {
        return Lists.newArrayList(x, y, z).iterator();
    }

}
