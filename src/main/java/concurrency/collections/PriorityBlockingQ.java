package concurrency.collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PriorityBlockingQ {

    @AllArgsConstructor @Getter
    static class Event implements Comparable<Event> {
        private int thread;
        private int priority;

        @Override
        public int compareTo(Event o) {
            return (int) Math.signum(o.priority - priority);
        }
    }

    @AllArgsConstructor
    static class Task implements Runnable {
        private int id;
        private java.util.concurrent.PriorityBlockingQueue<Event> queue;

        @Override
        public void run() {
            IterableUtils.genIdxList(0, 1000)
                    .map(idx -> new Event(id, idx))
                    .forEach(queue::add);
        }
    }

    public static void main(String[] args) {
        java.util.concurrent.PriorityBlockingQueue<Event> queue = new java.util.concurrent.PriorityBlockingQueue<>();
        List<Thread> threads = IterableUtils.genIdxList(0, 5)
                .map(idx -> new Task(idx, queue))
                .map(Thread::new)
                .collect(Collectors.toList());

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("Main: Queue Size: %d\n", queue.size());
        Arrays.stream(new int[threads.size() * 1000])
                .mapToObj(ig -> queue.poll())
                .forEach(e -> System.out.printf("Thread %s: Priority %d\n",
                        e.getThread(), e.getPriority()));
        System.out.printf("Main: Queue Size: %d\n", queue.size());
    }

}
