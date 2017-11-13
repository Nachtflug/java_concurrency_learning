package concurrency;

import java.util.concurrent.*;

public class ThreadPoolExecutorCancel {

    public static void main(String[] args) throws InterruptedException {

        Callable<String> task = () -> {
            while (true) {
                System.out.printf("Task: Test\n");
                Thread.sleep(100);
            }
        };

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        System.out.printf("Main: Executing the task\n");
        Future<String> rslt = executor.submit(task);

        TimeUnit.SECONDS.sleep(2);
        System.out.printf("Main: Canceling the task\n");
        rslt.cancel(true);
        System.out.printf("Main: Cancelled: %s, Done: %s\n", rslt.isCancelled(), rslt.isDone());
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");

    }

}
