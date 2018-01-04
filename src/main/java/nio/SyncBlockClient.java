package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SyncBlockClient {

    public static void main(String[] args) {
//        int port = 3456;
        int port = 4567;
        try (Socket socket = new Socket("127.0.0.1", port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("q");
            System.out.println("Now is: " + in.readLine());
            out.println("q\nq");
            System.out.println("Now is: " + in.readLine());
            System.out.println("Now is: " + in.readLine());
        } catch (IOException e) {

        }
    }

}
