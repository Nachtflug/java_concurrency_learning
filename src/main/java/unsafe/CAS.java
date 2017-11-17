package unsafe;

import sun.misc.Unsafe;
import util.UnsafeUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CAS {

    interface Counter {
        void increment();

        long getCount();
    }

    static class LockCounter implements Counter {

        private long count = 0;
        private ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();

        @Override
        public void increment() {
            lock.lock();
            count++;
            lock.unlock();
        }

        @Override
        public long getCount() {
            return count;
        }
    }

    static class CASCounter implements Counter { //Not quicker in jdk8

        private volatile long count = 0;
        private Unsafe unsafe;
        private long offset;

        public CASCounter() throws NoSuchFieldException {
            unsafe = UnsafeUtils.get();
            offset = unsafe.objectFieldOffset(CASCounter.class.getDeclaredField("count"));
        }

        @Override
        public void increment() {
            long before;
            do {
                before = unsafe.getLongVolatile(this, offset);
            }
            while (!unsafe.compareAndSwapLong(this, offset, before, before + 1));
        }

        @Override
        public long getCount() {
            return count;
        }
    }

    static class AtomicCounter implements Counter {

        private AtomicLong count = new AtomicLong();

        @Override
        public void increment() {
            count.incrementAndGet();
        }

        @Override
        public long getCount() {
            return count.get();
        }
    }

    static class CounterClient implements Runnable {
        private Counter c;
        private int num;

        public CounterClient(Counter c, int num) {
            this.c = c;
            this.num = num;
        }

        @Override
        public void run() {
            for (int i = 0; i < num; i++) {
                c.increment();
            }
        }
    }

    private static long test(Counter c, int numberOfThreads, int numOfInc) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        long before = System.currentTimeMillis();
        Arrays.stream(new CounterClient[numberOfThreads])
                .map(nul -> new CounterClient(c, numOfInc))
                .forEach(service::submit);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
        long after = System.currentTimeMillis();
        if (c.getCount() != numOfInc * numberOfThreads)
            System.out.println("Counter result: " + c.getCount());
        return after - before;
    }

    public static void main(String[] args) throws Exception {
        int NUM_OF_INC = 100000;
        System.out.println("threads\tcas\tlock\tatomic");
        for (int i = 10; i <= 10000; i *= 10) {
            long tCas = test(new CASCounter(), i, NUM_OF_INC);
            long tLock = test(new LockCounter(), i, NUM_OF_INC);
            long tAtomic = test(new AtomicCounter(), i, NUM_OF_INC);
            System.out.printf("%d\t%d\t%d\t%d\n", i, tCas, tLock, tAtomic);
        }
    }

}
