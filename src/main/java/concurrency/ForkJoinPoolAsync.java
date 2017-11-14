package concurrency;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ForkJoinPoolAsync {

    @AllArgsConstructor
    static class FolderProcessor extends RecursiveTask<List<String>> {

        private String path;
        private String extension;

        @Override
        protected List<String> compute() {
            File file = new File(path);
            File[] content = file.listFiles();
            if (content != null) {
                List<FolderProcessor> subTasks = Arrays.stream(content)
                        .filter(File::isDirectory)
                        .map(f -> new FolderProcessor(f.getAbsolutePath(), extension))
                        .collect(Collectors.toList());
                subTasks.forEach(RecursiveTask::fork);   //fork is an async/non-block way
                List<String> files = Arrays.stream(content)
                        .filter(File::isFile)
                        .filter(f -> f.getName().endsWith(extension))
                        .map(File::getAbsolutePath)
                        .collect(Collectors.toList());
                if (subTasks.size() > 50)
                    System.out.printf("%s: %d tasks ran.\n", file.getAbsolutePath(), subTasks.size());
                subTasks.stream()
                        .map(RecursiveTask::join)     //wait until join, effect work-stealing algorithm.
                        .forEach(files::addAll);
                return files;
            }

            return Lists.newArrayList();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        FolderProcessor system = new FolderProcessor("C:\\Windows", "log");
        FolderProcessor apps = new FolderProcessor("C:\\Program Files", "exe");
        FolderProcessor documents = new FolderProcessor("C:\\Documents And Settings", "doc");
        pool.execute(system);
        pool.execute(apps);
        pool.execute(documents);

        do {
            System.out.printf("***********************\n");
            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            System.out.printf("Main: Active Thread: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
            System.out.printf("Main: Steel Count: %d\n", pool.getStealCount());
            System.out.printf("***********************\n");
            TimeUnit.SECONDS.sleep(1);
        } while (!(system.isDone() && apps.isDone() && documents.isDone()));
        pool.shutdown();
        system.join().forEach(System.out::println);
        apps.join().forEach(System.out::println);
        documents.join().forEach(System.out::println);
    }

}
