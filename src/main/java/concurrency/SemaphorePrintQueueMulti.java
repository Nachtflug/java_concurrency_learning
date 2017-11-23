package concurrency;

import util.IterableUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphorePrintQueueMulti {

    static class PrintQueue {

        private boolean[] freePrinters;
        private Lock lockPrinters;
        private final Semaphore semaphore;
        private static final int RESOURCE_COUNT = 3;


        public PrintQueue() {
            this.semaphore = new Semaphore(RESOURCE_COUNT);
            freePrinters = new boolean[RESOURCE_COUNT];
            Arrays.fill(freePrinters, true);
            lockPrinters = new ReentrantLock();
        }

        public void printJob(Object document) {
            try {
                semaphore.acquire();
                int assignedPrinter = getPrinter();
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n",
                        Thread.currentThread().getName(), duration);
                TimeUnit.SECONDS.sleep(duration);
                freePrinters[assignedPrinter] = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }

        private int getPrinter() {
            try {
                lockPrinters.lock();
                Optional<Integer> retMaybe = IterableUtils.genIdxList(0, RESOURCE_COUNT)
                        .filter(i -> freePrinters[i])
                        .findAny();
                retMaybe.ifPresent(i -> freePrinters[i] = false);
                return retMaybe.orElse(-1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lockPrinters.unlock();
            }
            return -1;
        }
    }

    public static void main(String[] args) {
        PrintQueue queue = new PrintQueue();
        IterableUtils.genIdxList(0, 10)
                .map(idx -> new Thread(() -> {
                    System.out.printf("%s: Going to print a job\n", Thread.currentThread().getName());
                    queue.printJob(new Object());
                    System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
                }, "Thread " + idx))
                .forEach(Thread::start);
    }

}


