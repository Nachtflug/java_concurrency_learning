package util;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IterableUtils {



    public static <T> Iterable<T> newIter(Supplier<Boolean> judger, Supplier<T> succeeder) {
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return judger.get();
            }

            @Override
            public T next() {
                return succeeder.get();
            }
        };
    }

    public static Stream<Integer> genIdxList(int start, int end) {
        int step = start > end ? -1 : 1;
        return genIdxList(start, end, step);

    }

    public static Stream<Integer> genIdxList(int start, int end, int step) {
        return Stream
                .iterate(start, i -> i + step)
                .limit(Math.abs((end -start) / step));
    }

}
