package concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.IterableUtils;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorCustomPriority {

    @AllArgsConstructor @Getter
    static class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {

        private int priority;
        private String name;

        @Override
        public int compareTo(MyPriorityTask o) {
            return (int) Math.signum(o.priority - this.priority);
        }

        @Override
        public void run() {
            System.out.printf("MyPriorityTask: %s priority : %d\n", name, priority);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS
                            , new PriorityBlockingQueue<>());
        IterableUtils.genIdxList(0, 4).stream()
                .map(i -> new MyPriorityTask(i, "Task " + i))
                .forEach(executor::execute);
        TimeUnit.SECONDS.sleep(1);
        IterableUtils.genIdxList(4, 8).stream()
                .map(i -> new MyPriorityTask(i, "Task " + i))
                .forEach(executor::execute);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Main: End of the program");

    }

}
