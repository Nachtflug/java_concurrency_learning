package nio;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SyncBlockServer {


    @AllArgsConstructor
    public static class SocketHandler implements Runnable {

        private Socket socket;

        @Override
        public void run() {
            try (InputStreamReader isr = new InputStreamReader(this.socket.getInputStream());
                 BufferedReader in = new BufferedReader(isr);
                 PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {
                in.lines()
                        .map(l -> "q".equalsIgnoreCase(l) ? new Date().toString() : "bad request.")
                        .forEach(out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        int port = 3456;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("block server init in port: " + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new SocketHandler(socket)).start(); //acceptor
            }
        }
    }



}
