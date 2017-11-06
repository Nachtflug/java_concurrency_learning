package concurrency;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ForkJoinPoolCancel {

    static class TaskManager {
        private List<ForkJoinTask<Integer>> tasks;

        public TaskManager() {
            tasks = Lists.newArrayList();
        }

        public void addTask(ForkJoinTask<Integer> task) {
            tasks.add(task);
        }

        public void cancelTasks(ForkJoinTask<Integer> cancelTask) {
            tasks.stream()
                    .filter(t -> t != cancelTask)
                    .forEach(t -> {
                        ((SearchNumberTask) t).writeCancelMessage();
                        t.cancel(true);
                    });

        }

    }

    @AllArgsConstructor
    static class SearchNumberTask extends RecursiveTask<Integer> {

        private int[] numbers;
        private int start, end;
        private int number;
        private TaskManager manager;
        private final static int NOT_FOUND = -1;


        @Override
        protected Integer compute() {
            System.out.println("Task: " + start + ":" + end);
            return end - start > 100 ? launchTasks() : lookForNumnber();
        }

        private int lookForNumnber() {
            Optional<Integer> intMaybe = IterableUtils.genIdxList(start, end).stream()
                    .filter(i -> numbers[i] == number)
                    .findFirst();
            intMaybe.ifPresent(i -> {
                System.out.printf("Task number %d found in position %d\n", number, i);
                manager.cancelTasks(this);
            });
            return intMaybe.orElse(NOT_FOUND);
        }

        private int launchTasks() {
            int mid = (start + end) / 2;
            SearchNumberTask task1 = new SearchNumberTask(numbers, start, mid, number, manager);
            SearchNumberTask task2 = new SearchNumberTask(numbers, mid, end, number, manager);
            manager.addTask(task1);
            manager.addTask(task2);
            task1.fork();
            task2.fork();
            int ret = task1.join();
            return ret == NOT_FOUND ? task2.join() : ret;
        }

        public void writeCancelMessage() {
            System.out.printf("Task: Cancelled task from %d to %d\n", start, end);
        }
    }

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Function<Integer, int[]> f = size -> {
            int[] ret = new int[size];
            Random r = new Random();
            Arrays.setAll(ret, i -> r.nextInt(500));
            return ret;
        };
        int[] arr =  f.apply(1000);
        ForkJoinPool fkPool = new ForkJoinPool();
        SearchNumberTask mainTask = new SearchNumberTask(arr, 0, 1000, 233, manager);
        fkPool.execute(mainTask);
        fkPool.shutdown();
        try {
            fkPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("main finished.");
        }


    }


}
