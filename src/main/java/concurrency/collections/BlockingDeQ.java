package concurrency.collections;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockingDeQ {

    @AllArgsConstructor
    static class Client implements Runnable {

        private LinkedBlockingDeque<String> requestList;

        @Override
        public void run() {
            IterableUtils.genIdxList(0, 3).forEach(i -> {
                IterableUtils.genIdxList(0, 5).forEach(j -> {
                    String request = "" + i + ":" + j;
                    try {
                        requestList.put(request);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("Client: %s at %s\n", request, new Date());
                });
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            System.out.printf("Client: End\n");
        }
    }

    public static void main(String[] args) {
        LinkedBlockingDeque<String> list = new LinkedBlockingDeque<>(3);
        Client client = new Client(list);
        Thread thread = new Thread(client);
        thread.start();

        IterableUtils.genIdxList(0, 5).forEach(i -> {
            IterableUtils.genIdxList(0, 3).forEach(j -> {
                try {
                    String request = list.take();
                    System.out.printf("Main: Request: %s at %s. Size: %d\n",
                            request, new Date(), list.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

}
