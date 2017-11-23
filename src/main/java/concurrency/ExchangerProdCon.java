package concurrency;

import util.IterableUtils;

import java.util.concurrent.Exchanger;
import java.util.stream.Stream;

public class ExchangerProdCon {

    public static void main(String[] args) throws InterruptedException {
        final Exchanger exchanger = new Exchanger();

        Thread producer = new Thread(() -> IterableUtils.genIdxList(0, 10).forEach(idx -> {
            System.out.printf("Producer: Cycle %d\n", idx + 1);
            Stream<String> buffer = IterableUtils.genIdxList(0, 10)
                    .map(j -> "Event" + (idx * 10 + j));
            try {
                exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        Thread consumer = new Thread(() -> IterableUtils.genIdxList(0, 10).forEach(idx -> {
            try {
                System.out.println("Consumer: Cycle " + (idx + 1));
                Stream<String> buffer = (Stream<String>) exchanger.exchange(null);
                if (buffer != null) {
                    buffer.forEach(s -> System.out.println("Consumer: " + s));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }));

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

}
