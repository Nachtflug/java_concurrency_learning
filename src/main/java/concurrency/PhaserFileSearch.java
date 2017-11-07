package concurrency;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import util.T2;

import java.io.File;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PhaserFileSearch {

    @AllArgsConstructor
    static class FileSearch implements Runnable {

        private String initPath;
        private String end;
        private List<String> results;
        private Phaser phaser;

        private void directoryProcess(File file) {
            File[] list = file.listFiles();
            if (list != null) {
                Map<Boolean, List<File>> grouped = Arrays.stream(list)
                        .collect(Collectors.groupingBy(File::isDirectory, Collectors.toList()));
                grouped.getOrDefault(true, Lists.newArrayList()).forEach(this::directoryProcess);
                grouped.getOrDefault(false, Lists.newArrayList()).forEach(this::fileProcess);
            }
        }

        private void fileProcess(File file) {
            if (file.getName().endsWith(end))
                results.add(file.getAbsolutePath());
        }

        private void filterResults() {
            long now = new Date().getTime();
            results = results.stream()
                    .filter(path -> {
                        long delta = now - new File(path).lastModified();
                        return delta < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
                    })
                    .collect(Collectors.toList());
        }

        private boolean checkResults() {
            String threadName = Thread.currentThread().getName();
            int phase = phaser.getPhase();
            System.out.printf("%s: Phase %d: %d results\n", threadName, phase, results.size());
            if (results.isEmpty()) {
                System.out.printf("%s: Phase %d: End\n", threadName, phase);
                phaser.arriveAndDeregister();
            } else  {
                phaser.arriveAndAwaitAdvance();
            }
            return !results.isEmpty();
        }

        private void showInfo() {
            results.forEach(path -> System.out.printf("%s: %s\n",
                    Thread.currentThread().getName(), path));
            phaser.arriveAndAwaitAdvance();
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            phaser.arriveAndAwaitAdvance();        //phase 1
            System.out.printf("%s: Starting.\n", threadName);
            File file = new File(initPath);
            if (file.isDirectory())
                directoryProcess(file);
            if (!checkResults())          //phase 2
                return;
            filterResults();
            if (!checkResults())          //phase 3
                return;
            showInfo();
            phaser.arriveAndAwaitAdvance(); //phase 4

            System.out.printf("%s: Work completed.\n", threadName);
        }
    }

    public static void main(String[] args) {
        final String[] dics = {"Windows", "Program Files", "Documents And Settings"};
        final String extend = ".log";
        Phaser phaser = new Phaser(3);
        List<Thread> threads = Arrays.stream(dics)
                .map(d -> T2.of("C:\\" + d, d.split(" ")[0]))
                .map(t -> t.set1(new FileSearch(t._1(), extend, Lists.newArrayList(), phaser)))
                .map(t -> new Thread(t._1(), t._2()))
                .collect(Collectors.toList());

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Terminated: " + phaser.isTerminated());

    }

}
