package util;

import com.google.common.collect.Lists;

import java.util.Iterator;

public class T2<X, Y> implements Iterable<Object> {

    X x;
    Y y;

    T2(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public static <X, Y> T2<X, Y> of(X x, Y y) {
        return new T2<>(x, y);
    }

    public X _1() {
        return x;
    }

    public Y _2() {
        return y;
    }

    public <N> T2<N, Y> set1(N n) {
        return new T2<>(n, y);
    }

    public <N> T2<X, N> set2(N n) {
        return new T2<>(x, n);
    }

    public <Z> T3<X, Y, Z> add(Z z) {
        return T3.of(x, y, z);
    }

    @Override
    public Iterator<Object> iterator() {
        return Lists.newArrayList(x, y).iterator();
    }
}
