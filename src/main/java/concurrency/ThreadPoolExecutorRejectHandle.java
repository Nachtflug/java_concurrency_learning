package concurrency;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorRejectHandle {

    static class RejectedTaskController implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.printf("RejectedTaskController: The task %s has been rejected\n", r.toString());
            System.out.printf("RejectedTaskController: Executor is %s\n", executor.toString());
            System.out.printf("RejectedTaskController: Terminating %s\n", executor.isTerminating());
            System.out.printf("RejectedTaskController: Terminated %s\n", executor.isTerminated());
        }
    }

    @AllArgsConstructor
    static class Task implements Runnable {

        private String name;

        @Override
        public void run() {
            System.out.printf("Task %s: Starting\n", name);
            long duration = (long) (Math.random() * 10);
            System.out.printf("Task %s: ReportGenerator: Generating a report during %d seconds\n",
                    name, duration);
            try {
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Task %s: Ends\n", name);
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static void main(String[] args) {
        RejectedTaskController controller = new RejectedTaskController();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setRejectedExecutionHandler(controller);
        System.out.printf("Main: Starting\n");
        IterableUtils.genIdxList(0, 3).stream()
                .map(i -> new Task("Task " + i))
                .forEach(executor::submit);
        System.out.printf("Main: Shutting down the executor\n");
        executor.shutdown();
        System.out.printf("Main: Sending another task to executor\n");
        Task task = new Task("Rejected Task");
        executor.submit(task);
        System.out.println("Main: Ends");
    }

}
