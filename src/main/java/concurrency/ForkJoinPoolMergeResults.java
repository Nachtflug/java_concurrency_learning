package concurrency;

import lombok.AllArgsConstructor;
import util.IterableUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ForkJoinPoolMergeResults {

    static class DocumentMock {
        private String[] words = {"the", "hello", "goodbye", "packt", "java", "thread",
                "pool", "random", "class", "main"};

        public String[][] generateDocument(int numLines, int numWords, String word) {
            Random r = new Random();
            String[][] doc = Arrays.stream(new String[numLines][numWords])
                    .map(line -> Arrays.stream(line)
                            .map(w -> words[r.nextInt(words.length)])
                            .toArray(String[]::new))
                    .toArray(String[][]::new);
            long count = Arrays.stream(doc)
                    .flatMap(Arrays::stream)
                    .filter(w -> w.equals(word))
                    .count();
            System.out.printf("DocumentMock: The word appears %d times in the document\n", count);
            return doc;
        }
    }

    @AllArgsConstructor
    static class LineTask extends RecursiveTask<Integer> { //fork tasks in single line.
        private String[] line;
        private int start, end;
        private String word;

        @Override
        protected Integer compute() {
            if (end - start < 100)
                return count();
            int mid = (start + end) / 2;
            LineTask t1 = new LineTask(line, start, mid, word);
            LineTask t2 = new LineTask(line, mid, end, word);
            invokeAll(t1, t2);
            try {
                return t1.get() + t2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private int count() {
            return (int) IterableUtils.genIdxList(start, end).stream()
                    .filter(idx -> line[idx].equals(word))
                    .count();

        }
    }

    @AllArgsConstructor
    static class DocumentTask extends RecursiveTask<Integer> {
        private String[][] document;
        private int start, end;
        private String word;

        @Override
        protected Integer compute() {
            if (end - start < 10)
                return processLines();
            int mid = start + end;
            mid /= 2;
            DocumentTask t1 = new DocumentTask(document, start, mid, word);
            DocumentTask t2 = new DocumentTask(document, mid, end, word);
            invokeAll(t1, t2);
            try {
                return t1.get() + t2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private int processLines() {
            List<LineTask> lineTasks = IterableUtils.genIdxList(start, end).stream()
                    .map(idx -> new LineTask(document[idx], 0, document[idx].length, word))
                    .collect(Collectors.toList());
            invokeAll(lineTasks);
            return lineTasks.stream().mapToInt(t -> {
                try {
                    return t.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return 0;
            }).sum();

        }
    }

    public static void main(String[] args) {
        DocumentMock mock = new DocumentMock();
        String[][] doc = mock.generateDocument(1000, 10000, "the");
        DocumentTask task = new DocumentTask(doc, 0, 100, "the");
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
        do {
            System.out.println("******************************");
            System.out.printf("Main: Thread Count: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Thread steal: %d\n", pool.getStealCount());
            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());
        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.printf("Main: The word appears %d in the document", task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
