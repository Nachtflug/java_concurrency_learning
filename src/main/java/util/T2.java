package util;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.function.Function;

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

    public <N> T2<N, Y> map1(Function<? super X, ? extends N> f) {
        return set1(f.apply(x));
    }

    public <N> T2<X, N> map2(Function<? super Y, ? extends N> f) {
        return set2(f.apply(y));
    }

    public <Z> T3<X, Y, Z> add(Z z) {
        return T3.of(x, y, z);
    }

    @Override
    public Iterator<Object> iterator() {
        return Lists.newArrayList(x, y).iterator();
    }
}
