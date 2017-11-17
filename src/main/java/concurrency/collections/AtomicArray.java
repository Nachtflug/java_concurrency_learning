package concurrency.collections;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;

public class AtomicArray {

    @AllArgsConstructor
    static class Incrementer implements Runnable {

        private AtomicIntegerArray vector;

        @Override
        public void run() {
            IterableUtils.genIdxList(0, vector.length())
                    .forEach(vector::getAndIncrement);
        }
    }

    public static void main(String[] args) {
        AtomicIntegerArray array = new AtomicIntegerArray(100);
        Incrementer incrementer = new Incrementer(array);
        List<Thread> threads = Arrays.stream(new int[10])
                .mapToObj(ig -> new Thread(incrementer))
                .collect(Collectors.toList());
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        IterableUtils.genIdxList(0, array.length()).stream()
                .map(array::get)
                .forEach(System.out::println);

    }

}
