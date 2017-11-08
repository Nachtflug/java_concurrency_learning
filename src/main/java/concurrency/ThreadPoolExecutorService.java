package concurrency;

import lombok.RequiredArgsConstructor;
import util.IterableUtils;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorService {

    @RequiredArgsConstructor
    static class Task implements Runnable {

        private Date initDate = new Date();
        private final String name;


        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.printf("%s: Task %s: Created on: %s\n", threadName, name, initDate);
            System.out.printf("%s: Task %s: Started on: %s\n", threadName, name, new Date());
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Task %s: Doing a task during %d seconds\n", threadName, name, duration);
            try {
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: Task %s: Finished on: %s\n", threadName, name, new Date());
        }
    }

    static class Server {

        private ThreadPoolExecutor executor;

        public Server() {
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        public void execute(Task task) {
            System.out.printf("Server: A new task has arrived\n");
            executor.execute(task);
            System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
            System.out.printf("Server: Active Count: %d\n", executor.getActiveCount());
            System.out.printf("Server: Completed Tasks: %d\n", executor.getCompletedTaskCount());
        }

        public void endServer() {
            executor.shutdown();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        IterableUtils.genIdxList(0, 100).stream()
                .map(i -> "Task " + i)
                .map(Task::new)
                .forEach(server::execute);
        server.endServer();
    }

}
