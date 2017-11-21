package jdk8;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamCreation {

    public static void main(String[] args) {
        Stream<String> empty = Stream.empty();
        Stream<String> s1 = Stream.of("ha");
        Stream<String> s2 = Stream.of("ha", "ma");
        Stream<Double> genStream = Stream.generate(Math::random);
        Stream<Integer> iterStream = Stream.iterate(0, i -> ++i);

        Stream<String> fromList = Lists.<String>newArrayList().stream();
        IntStream intStream = Arrays.stream(new int[10]);

    }


}
