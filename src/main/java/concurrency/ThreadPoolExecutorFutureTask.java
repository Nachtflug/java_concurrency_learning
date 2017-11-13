package concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.IterableUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadPoolExecutorFutureTask {

    @AllArgsConstructor @Getter
    static class ExecutableTask implements Callable<String> {

        private String name;

        @Override
        public String call() throws Exception {
            System.out.printf("%s: Starting.\n", this.name);

            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);

            return "Hello world. I'm " + name;
        }
    }

    static class ResultTask extends FutureTask<String> {

        private String name;

        public ResultTask(Callable<String> callable) {
            super(callable);
            this.name = ((ExecutableTask) callable).getName();
        }

        // will trigger this method when callable is done.
        @Override
        protected void done() {
            if (isCancelled())
                System.out.printf("%s: has been canceled\n", name);
            else
                System.out.printf("%s: Has finished\n", name);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();
        List<ResultTask> rsltTask = IterableUtils.genIdxList(0, 5).stream()
                .map(i -> "Task " + i)
                .map(ExecutableTask::new)
                .map(ResultTask::new)
                .collect(Collectors.toList());
        rsltTask.forEach(executor::submit);
        TimeUnit.SECONDS.sleep(5);
        rsltTask.forEach(task -> task.cancel(true));
        rsltTask.stream().filter(task -> !task.isCancelled())
                .map(task -> {
                    try {
                        return task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .forEach(System.out::println);
        executor.shutdown();

    }

}
