package concurrency.collections;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRdm {

    static class TaskLocalRandom implements Runnable {

        public TaskLocalRandom() {
            ThreadLocalRandom.current(); // create random for current thread.
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            Arrays.stream(new int[10])
                    .forEach(i -> System.out.printf("%s: %d\n", name,
                            ThreadLocalRandom.current().nextInt(10)));
        }
    }

    public static void main(String[] args) {
        Arrays.stream(new Object[5])
                .map(o -> new TaskLocalRandom())
                .map(Thread::new)
                .forEach(Thread::start);
    }

}
