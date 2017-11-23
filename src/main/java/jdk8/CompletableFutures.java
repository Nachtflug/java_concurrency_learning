package jdk8;


import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CompletableFutures {

    public static void main(String[] args) throws InterruptedException {

        final Supplier<List<String>> randomSleeper = () -> {
            int sleep = new Random().nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList("this", "is", "a", "example", sleep + "");
        };

        CompletableFuture
                .supplyAsync(randomSleeper)
                .thenApply(l -> String.join(" ", l))
                .thenAccept(System.out::println).join();



    }

}
