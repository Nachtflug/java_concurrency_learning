package concurrency;

import util.IterableUtils;
import util.T2;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadPoolExecutorCustom {

    static class MyExecutor extends ThreadPoolExecutor {

        private ConcurrentHashMap<String, Date> startTimes;


        public MyExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            startTimes = new ConcurrentHashMap<>();
        }

        @Override
        public void shutdown() {
            System.out.printf("MyExecutor: Goning to shutdown.\n");
            System.out.printf("MyExecutor: Executed tasks: %d.\n", getCompletedTaskCount());
            System.out.printf("MyExecutor: Running tasks: %d.\n", getActiveCount());
            System.out.printf("MyExecutor: Pending tasks: %d.\n", getQueue().size());
            super.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            System.out.printf("MyExecutor: Goning to immediately shutdown.\n");
            System.out.printf("MyExecutor: Executed tasks: %d.\n", getCompletedTaskCount());
            System.out.printf("MyExecutor: Running tasks: %d.\n", getActiveCount());
            System.out.printf("MyExecutor: Pending tasks: %d.\n", getQueue().size());
            return super.shutdownNow();
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            System.out.printf("MyExecutor: A task is beginning: %s : %s\n", t.getName(), r.hashCode());
            startTimes.put(r.hashCode() + "", new Date());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            Future<?> result = (Future<?>) r;
            System.out.printf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            System.out.printf("MyExecutor: A task is finishing\n");
            try {
                System.out.printf("MyExecutor: Result: %s\n", result.get());
                Date startDate = startTimes.remove(r.hashCode() + "");
                Date finishDate = new Date();
                System.out.printf("MyExecutor: Duration: %d\n",
                        finishDate.getTime() - startDate.getTime());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            super.afterExecute(r, t);
        }
    }

    public static void main(String[] args) {
        MyExecutor myExecutor = new MyExecutor(2, 4, 1000,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

        List<Future<String>> results = Arrays.stream(new int[5])
                .mapToObj(ig -> (Callable<String>) () -> {
                    TimeUnit.SECONDS.sleep(2);
                    return new Date().toString();
                }).map(myExecutor::submit)
                .collect(Collectors.toList());

        myExecutor.shutdown();
//        List<Runnable> unfinished = myExecutor.shutdownNow();
        IterableUtils.genIdxList(0, results.size()).stream()
                .map(i -> T2.of(i, results.get(i)))
                .map(t -> t.map2(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return "";
                })).map(t -> String.format("Main: Result for task %d : %s\n", t._1(), t._2()))
                .forEach(System.out::println);
        System.out.println("Main: End of the program");

    }

}
