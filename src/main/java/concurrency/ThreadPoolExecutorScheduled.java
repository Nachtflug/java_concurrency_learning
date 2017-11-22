package concurrency;

import util.IterableUtils;
import util.T2;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorScheduled {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor =
                (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

        System.out.printf("Main: Starting at: %s\n", new Date());
        IterableUtils.genIdxList(0, 5).stream()
                .map(i -> {
                    Callable<String> task = () -> {

                        System.out.printf("%s: Starting at : %s\n", "Task " + i, new Date());
                        return "Hello, World!" + i;
                    };
                    return T2.of(i, task);
                }).forEach(t -> {
            executor.schedule(t._2(), t._1() + 1, TimeUnit.SECONDS); //1 second per task
        });
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: End at: %s\n", new Date());
    }

}
