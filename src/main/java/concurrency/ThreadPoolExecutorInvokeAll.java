package concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.IterableUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadPoolExecutorInvokeAll {

    @Getter @Setter @AllArgsConstructor
    static class Result {
        private String name;
        private int value;
    }

    @AllArgsConstructor
    static class Task implements Callable<Result> {

        private String name;

        @Override
        public Result call() throws Exception {
            System.out.printf("%s: Starting.\n", this.name);

            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);

            return new Result(this.name, (int) (Math.random() * 100));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executors = Executors.newCachedThreadPool();
        List<Task> tasks = IterableUtils.genIdxList(0, 3)
                .map(i -> new Task(i + ""))
                .collect(Collectors.toList());

        List<Future<Result>> results = executors.invokeAll(tasks);
        executors.shutdown();
        results.forEach(f -> {
            try {
                Result r = f.get();
                System.out.printf("%s: %d\n", r.getName(), r.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
