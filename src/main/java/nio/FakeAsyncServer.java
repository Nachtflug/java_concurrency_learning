package nio;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FakeAsyncServer {

    @AllArgsConstructor
    public static class SocketHandlerExecutePool {

        private ExecutorService executor;

        public SocketHandlerExecutePool(int maxPoolSize, int queueSize) {
            executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    maxPoolSize, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(queueSize));
        }

        public void execute(Runnable task) {
            executor.execute(task);
        }

    }

    public static void main(String[] args) throws IOException {
        int port = 4567;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("block server init in port: " + port);
            Socket socket;
            SocketHandlerExecutePool acceptor = new SocketHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                acceptor.execute(new SyncBlockServer.SocketHandler(socket));
            }
        }
    }

}
