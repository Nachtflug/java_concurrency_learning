package concurrency;

import lombok.AllArgsConstructor;
import util.IterableUtils;
import util.T2;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PhaserCustomize {

    static class MyPhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    return studentsArrived();
                case 1:
                    return finishfirstExam();
                case 2:
                    return finishSecondExam();
                default:
                    return true;
            }
        }

        private boolean finishSecondExam() {
            System.out.println("Phaser: All have finished the second exam.\nTime for third one\n");
            return false;
        }

        private boolean finishfirstExam() {
            System.out.println("Phaser: All have finished the first exam.\nTime for second one\n");
            return false;
        }

        private boolean studentsArrived() {
            System.out.println("Phaser: the exam are going to start. Students are ready.");
            System.out.printf("Phaser: We have %d students.\n\n", getRegisteredParties());
            return false;
        }
    }

    @AllArgsConstructor
    static class Student implements Runnable {

        private Phaser phaser;

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.printf("%s: Has arrived.%s \n", threadName, new Date());
            phaser.arriveAndAwaitAdvance();
            doExam1();
            System.out.printf("%s: Finished first exam.%s\n", threadName, new Date());
            phaser.arriveAndAwaitAdvance();
            doExam2();
            System.out.printf("%s: Finished second exam.%s\n", threadName, new Date());
            phaser.arriveAndAwaitAdvance();
            doExam3();
            System.out.printf("%s: Finished third exam.%s\n", threadName, new Date());
            phaser.arriveAndAwaitAdvance();
        }

        private void doExam1() {
            wasteRandomTime();
        }

        private void doExam2() {
            wasteRandomTime();
        }

        private void doExam3() {
            wasteRandomTime();
        }

        private void wasteRandomTime() {
            try {
                TimeUnit.SECONDS.sleep((long) (Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Phaser phaser = new MyPhaser();
        List<Thread> threads = IterableUtils.genIdxList(0, 5).stream()
                .map(idx -> T2.of(new Student(phaser), idx))
                .map(t -> new Thread(t._1(), "student " + t._2()))
                .collect(Collectors.toList());
        threads.forEach(t -> phaser.register());
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.printf("Main: The phaser has finished: %s.\n", phaser.isTerminated());
    }

}
