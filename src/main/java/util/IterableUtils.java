package util;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public class IterableUtils {

    @FunctionalInterface
    public interface HasNext {
        boolean hasNext();
    }

    @FunctionalInterface
    public interface Next<T> {
        T next();
    }


    public static <T> Iterable<T> newIter(HasNext judger, Next<T> succeeder) {
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return judger.hasNext();
            }

            @Override
            public T next() {
                return succeeder.next();
            }
        };
    }

    public static List<Integer> genIdxList(int start, int end) {
        int step = start > end ? -1 : 1;
        return genIdxList(start, end, step);

    }

    public static List<Integer> genIdxList(int start, int end, int step) {
        List<Integer> ls = Lists.newArrayList();
        for (; step > 0 ^ start >= end; start += step) {
            ls.add(start);
        }
        return ls;
    }

}
