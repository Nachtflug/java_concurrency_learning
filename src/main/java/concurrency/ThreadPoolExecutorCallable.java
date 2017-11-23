package concurrency;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadPoolExecutorCallable {

    @AllArgsConstructor
    static class FactorialCalculator implements Callable<Integer> {

        private Integer number;

        @Override
        public Integer call() throws Exception {
            System.out.printf("Task: %d\n", number);
            if (number <= 1)
                return 1;
            return IterableUtils.genIdxList(2, number + 1)
                    .reduce(1, (x, y) -> x * y);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Random r = new Random();
        List<Future<Integer>> futures = Arrays.stream(new int[10])
                .map(ignore -> r.nextInt(10))
                .mapToObj(FactorialCalculator::new)
                .map(executor::submit)
                .collect(Collectors.toList());

        do {
            System.out.printf("Main: Number of Completed Tasks: %d\n", executor.getCompletedTaskCount());
            Thread.sleep(100);
        } while (executor.getCompletedTaskCount() < futures.size());

        System.out.printf("Main: Results\n");
        futures.forEach(fu -> {
            try {
                System.out.printf("Main: %d\n", fu.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();

    }

}
