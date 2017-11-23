package concurrency.collections;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DelayQ {

    @AllArgsConstructor
    static class Event implements Delayed {

        private Date startDate;

        @Override
        public long getDelay(TimeUnit unit) {
            Date now = new Date();
            long diff = startDate.getTime() - now.getTime();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {   //object not available in delay queue if < 0
            long delta = getDelay(TimeUnit.NANOSECONDS) -
                    o.getDelay(TimeUnit.NANOSECONDS);
            return (int) Math.signum(delta);
        }
    }

    @AllArgsConstructor
    static class Task implements Runnable {
        private int id;
        private DelayQueue<Event> queue;

        @Override
        public void run() {
            Date now = new Date();
            Date delay = new Date();
            delay.setTime(now.getTime() + (id * 1000));
            System.out.printf("Thread %s: %s\n", id, delay);
            Arrays.stream(new int[100])
                    .mapToObj(ig -> new Event(delay))
                    .forEach(queue::add);

        }


    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<Event> queue = new DelayQueue<>();
        List<Thread> threads = IterableUtils.genIdxList(0, 5)
                .map(idx -> new Task(idx + 1, queue))
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
        int counter = 0;
        do {
            Event event = queue.poll();
            while (event != null) {
                counter ++;
                event = queue.poll();
            }
            System.out.printf("At %s you have read %d events\n", new Date(), counter);
            TimeUnit.MILLISECONDS.sleep(500);
        } while (queue.size() > 0);
    }

}
