package concurrency;

import lombok.Getter;
import util.IterableUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierMatrixSearch {

    static class MatrixMock {

        private int[][] data;

        public MatrixMock(int size, int length, int number) {

            data = new int[size][length];
            Random r = new Random();
            Arrays.setAll(data, i -> Arrays.stream(data[i])
                    .map(v -> r.nextInt(100))
                    .toArray());
            long count = Arrays.stream(data).flatMapToInt(Arrays::stream)
                    .filter(d -> d == number)
                    .count();
            System.out.printf("Mock: There are %d ocurrences of %d in generated data.\n", count, number);
        }

        public int[] getRow(int row) {
            return (row >= 0 && row < data.length) ? data[row] : null;
        }

    }

    @Getter
    static class Results {

        private int data[];

        public Results(int size) {
            this.data = new int[size];
        }

        public void setData(int position, int value) {
            data[position] = value;
        }

    }

    public static void main(String[] args) {
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;
        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);
        Results rs = new Results(ROWS);

        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, () -> {
            System.out.println("Grouper: Processing dresults...");
            int finalRslt = Arrays.stream(rs.getData()).sum();
            System.out.printf("Grouper: Total result: %d.\n", finalRslt);
        });
        IterableUtils.genIdxList(0, PARTICIPANTS).stream()
                .map(idx -> (Runnable) () -> {
                    int firstRow = idx * LINES_PARTICIPANT;
                    int lastRow = firstRow + LINES_PARTICIPANT;
                    System.out.printf("%s: Processing lines from %d to %d.\n",
                            Thread.currentThread().getName(), firstRow, lastRow);
                    IterableUtils.genIdxList(firstRow, lastRow).forEach(j ->
                            rs.setData(j, ((int) Arrays.stream(mock.getRow(j))
                            .filter(n -> n == SEARCH)
                            .count())));
                    System.out.printf("%s: Line processed.\n",
                            Thread.currentThread().getName());
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).map(Thread::new)
                .forEach(Thread::start);

        System.out.println("Main finished.");

    }

}
