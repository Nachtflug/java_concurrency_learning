package concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.IterableUtils;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ForkJoinPoolFramework {

    @Getter @Setter @AllArgsConstructor
    static class Product {
        private String name;
        private double price;
    }

    static class ProductListGenerator {
        public List<Product> generate(int size) {
            return IterableUtils.genIdxList(0, size).stream()
                    .map(i -> new Product("Product " + i, 10))
                    .collect(Collectors.toList());
        }
    }

    @AllArgsConstructor
    static class Task extends RecursiveAction {

        private List<Product> products;
        private int first;
        private int last;
        private double increment;

        @Override
        protected void compute() {
            if (last - first < 10)
                updatePrices();
            else {
                int middle = (last + first) / 2;
                System.out.printf("Task: Pending tasks: %s\n", getQueuedTaskCount());
                Task t1 = new Task(products, first, middle + 1, increment);
                Task t2 = new Task(products, middle + 1, last, increment);
                invokeAll(t1, t2);
            }
        }

        private void updatePrices() {

            IterableUtils.genIdxList(first, last)
                    .forEach(idx -> {
                        Product p = products.get(idx);
                        p.setPrice(p.getPrice() * (1 + increment));
                    });

        }
    }

    public static void main(String[] args) {
        ProductListGenerator generator = new ProductListGenerator();
        List<Product> products = generator.generate(10000);
        Task mainTask = new Task(products, 0, products.size(), 0.2);
        ForkJoinPool fk = new ForkJoinPool();
        fk.execute(mainTask);
        do {
            System.out.printf("Main: Thread Count: %d\n", fk.getActiveThreadCount());
            System.out.printf("Main: Thread steal: %d\n", fk.getStealCount());
            System.out.printf("Main: Parallelism: %d\n", fk.getParallelism());
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!mainTask.isDone());

        fk.shutdown();
        if (mainTask.isCompletedNormally()) {
            System.out.printf("Main: The process has completed normally\n");
        }
        products.stream()
                .filter(p -> p.getPrice() != 12)
                .forEach(p -> System.out.printf("Product %s: %f\n", p.getName(), p.getPrice()));
    }

}
