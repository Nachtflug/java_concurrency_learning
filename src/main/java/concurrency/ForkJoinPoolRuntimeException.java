package concurrency;

import lombok.AllArgsConstructor;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

public class ForkJoinPoolRuntimeException {

    @AllArgsConstructor
    static class Task extends RecursiveTask<Integer> {
        private int[] array;
        private int start, end;

        @Override
        protected Integer compute() {
            System.out.printf("Task: Start from %d to %d\n", start, end);
            if (end - start < 10) {
                if (3 > start && 3 < end)
                    throw new RuntimeException(
                            "This task throws an Exception: Task from " + start + " to " + end);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                int mid = (end + start);
                mid /= 2;
                Task t1 = new Task(array, start, mid);
                Task t2 = new Task(array, mid, end);
                invokeAll(t1, t2);
            }
            System.out.printf("Task: End form %d to %d\n", start, end);
            return 0;
        }
    }

    public static void main(String[] args) {
        int[] array = new int[100];
        Task task = new Task(array, 0, 100);
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (task.isCompletedAbnormally()) {
            System.out.printf("Main: An exception has ocurred\n");
            System.out.printf("Main: %s\n", task.getException());
        }
        System.out.printf("Main: Result: %d", task.join());
    }

}
