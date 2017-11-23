package concurrency;

import util.IterableUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchVideoConference {

    static class VideoConference implements Runnable {

        private final CountDownLatch controller;

        public VideoConference(int number) {
            this.controller = new CountDownLatch(number);
        }

        public void arrive(String name) {
            System.out.printf("%s has arrived.\n", name);
            controller.countDown();
            System.out.printf("VideoConference: still waiting for %d participants.\n",
                    controller.getCount());
        }

        @Override
        public void run() {
            System.out.printf("VideoConference: Initialization: %d participants.\n",
                    controller.getCount());
            try {
                controller.await();
                System.out.printf("VideoConference: All the participants have come\n");
                System.out.printf("VideoConference: Let's start\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int numberCount = 10;
        VideoConference conference = new VideoConference(numberCount);
        new Thread(conference).start();
        IterableUtils.genIdxList(0, 10)
                .map(idx -> new Thread(() -> {
                    long duration = (long) (Math.random() * 10);
                    try {
                        TimeUnit.SECONDS.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    conference.arrive("Participant " + idx);
                })).forEach(Thread::start);

    }
}
