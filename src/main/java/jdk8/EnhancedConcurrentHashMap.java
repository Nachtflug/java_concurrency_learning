package jdk8;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public class EnhancedConcurrentHashMap {

    public static void main(String[] args) {

        ConcurrentMap<String, Long> map = Maps.newConcurrentMap();

        // replace old value, +1
        Long oldValue, newValue;
        String key = "key";
        map.putIfAbsent(key, 0L);
        do {
            oldValue = map.get(key);
            newValue = oldValue == null ? 1 : oldValue + 1;
        } while (map.replace(key, oldValue, newValue));

        // or use LongAdder
        ConcurrentMap<String, LongAdder> map2 = Maps.newConcurrentMap();
        map2.putIfAbsent(key, new LongAdder()).increment();

        // another atomic way
        map.compute(key, (k, v) -> v == null ? 1 : v + 1);
        map2.computeIfAbsent(key, k -> new LongAdder()).increment();

        // or only the value to combine
        map.merge(key, 1L, Long::sum);


    }

}
