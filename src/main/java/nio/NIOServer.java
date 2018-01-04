package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NIOServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public NIOServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NIO server init in port: " + port);
            stop = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        this.stop = true;
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                SocketChannel sc = serverSocketChannel.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readed = sc.read(readBuffer);
                if (readed > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("received order: " + body);
                    String ret = "q".equalsIgnoreCase(body) ? new Date().toString() : "bad request.";
                    ByteBuffer writeBuffer = ByteBuffer.allocate(ret.length());
                    writeBuffer.put(ret.getBytes());
                    writeBuffer.flip();
                    sc.write(writeBuffer);

                } else if (readed < 0) {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                SelectionKey key;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    handleKey(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 5678;
        new Thread(new NIOServer(port)).start();
        new Thread(new NIOClient(null, port)).start();
    }
}
