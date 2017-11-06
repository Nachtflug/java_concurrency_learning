package concurrency;

import util.IterableUtils;

import java.util.concurrent.Semaphore;

public class SemaphorePrintQueue {


    static class PrintQueue {

        private final Semaphore semaphore;

        public PrintQueue() {
            this.semaphore = new Semaphore(1);
        }

        public void printJob(Object document) {
            try {
                semaphore.acquire();
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n",
                        Thread.currentThread().getName(), duration);
                Thread.sleep(duration * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    public static void main(String[] args) {
        PrintQueue queue = new PrintQueue();
        IterableUtils.genIdxList(0, 10).stream()
                .map(idx -> new Thread(() -> {
                    System.out.printf("%s: Goning to print a job\n", Thread.currentThread().getName());
                    queue.printJob(new Object());
                    System.out.printf("%s: The documnet has been printed\n", Thread.currentThread().getName());
                }, "Thread " + idx))
                .forEach(Thread::start);
    }
}
