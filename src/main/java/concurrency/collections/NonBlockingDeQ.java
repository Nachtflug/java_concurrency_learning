package concurrency.collections;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class NonBlockingDeQ {

    @AllArgsConstructor
    static class AddTask implements Runnable {

        private ConcurrentLinkedDeque<String> list;

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            IterableUtils.genIdxList(0, 10000).stream()
                    .map(idx -> name + ": Element " + idx)
                    .forEach(list::add);
        }
    }

    @AllArgsConstructor
    static class PollTask implements Runnable {

        private ConcurrentLinkedDeque<String> list;

        @Override
        public void run() {
            Arrays.stream(new int[5000])
                    .forEach(ignore -> {list.pollFirst(); list.pollLast();});
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
        List<Thread> threads = Arrays.stream(new int[100])
                .mapToObj(ignore -> new AddTask(list))
                .map(Thread::new)
                .collect(Collectors.toList());

        threads.forEach(Thread::start);
        System.out.printf("Main: %d AddTask threads have been launched\n", threads.size());
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.printf("Main: Size of the list: %d\n", list.size());

        List<Thread> poolThreads = Arrays.stream(new int[100])
                .mapToObj(ignore -> new PollTask(list))
                .map(Thread::new)
                .collect(Collectors.toList());

        poolThreads.forEach(Thread::start);
        System.out.printf("Main: %d PoolTask threads have been launched\n", threads.size());
        poolThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("Main: Size of the list: %d\n", list.size());
    }

}
