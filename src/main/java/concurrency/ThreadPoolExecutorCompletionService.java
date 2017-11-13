package concurrency;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.concurrent.*;

public class ThreadPoolExecutorCompletionService {

    @AllArgsConstructor
    static class ReportGenerator implements Callable<String> {
        private String sender;
        private String title;

        @Override
        public String call() throws Exception {
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s_%s: ReportGenerator: Generating a report during %d seconds\n",
                    this.sender, this.title, duration);
            TimeUnit.SECONDS.sleep(duration);
            return sender + ": " + title;
        }

    }

    @AllArgsConstructor
    static class ReportRequest implements Runnable {

        private String name;
        private CompletionService<String> service;

        @Override
        public void run() {
            ReportGenerator generator = new ReportGenerator(name, "Report");
            service.submit(generator);     //1. CompletionService submits a callable
        }
    }

    @AllArgsConstructor
    @Setter
    static class ReportProcessor implements Runnable {

        private CompletionService<String> service;
        private boolean end;

        @Override
        public void run() {
            while (!end) {
                try {
                    Future<String> rslt = service.poll(20, TimeUnit.SECONDS);
                    if (rslt != null) {
                        String report = rslt.get();  //2. And result processing could be in another class.
                        System.out.printf("Report Processor: Report Received: %s\n", report);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("ReportSender: End");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<String> service = new ExecutorCompletionService<>(executor);
        ReportRequest faceRequest = new ReportRequest("Face", service);
        ReportRequest onlineRequest = new ReportRequest("Online", service);
        Thread faceThread = new Thread(faceRequest);
        Thread onlineThread = new Thread(onlineRequest);
        ReportProcessor processor = new ReportProcessor(service, false);
        Thread senderThread = new Thread(processor);
        System.out.printf("Main: Starting the Threads\n");
        faceThread.start();
        onlineThread.start();
        senderThread.start();
        System.out.printf("Main: Waiting for the report generators\n");
        faceThread.join();
        onlineThread.join();


        System.out.printf("Main: Shutting down the executor\n");
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        processor.setEnd(true);
        System.out.println("Main: Ends");

    }

}
