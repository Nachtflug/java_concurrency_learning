package nio;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AIOServer implements Runnable {

    private int port;

    private CountDownLatch latch;
    private AsynchronousServerSocketChannel channel;

    public AIOServer(int port) {
        this.port = port;
        try {
            channel = AsynchronousServerSocketChannel.open();
            channel.bind(new InetSocketAddress(port));
            System.out.println("AIO Server started in port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void doAccept() {

        channel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AIOServer>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AIOServer attachment) {
                attachment.channel.accept(attachment, this);  // loop for next connection
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                result.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer integer, ByteBuffer attachment) {
                        attachment.flip();
                        byte[] body = new byte[attachment.remaining()];
                        attachment.get(body);
                        try {
                            String req = new String(body, "UTF-8");
                            System.out.println("Order: " + req);
                            String currentTime = "q".equalsIgnoreCase(req) ? new Date().toString() : "Bad request.";
                            if (Strings.isNullOrEmpty(currentTime)) return;
                            byte[] toWrite = currentTime.getBytes();
                            ByteBuffer writeBuffer = ByteBuffer.allocate(toWrite.length);
                            writeBuffer.put(toWrite);
                            writeBuffer.flip();
                            result.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer integer1, ByteBuffer attachment) {
                                    if (buffer.hasRemaining())
                                        result.write(buffer, buffer, this); //loop till write complete
                                }

                                @Override
                                public void failed(Throwable exc, ByteBuffer attachment) {
                                    try {
                                        result.close();
                                    } catch (IOException e) {
//                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            result.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }

            @Override
            public void failed(Throwable exc, AIOServer attachment) {
                attachment.latch.countDown();
            }
        });

    }


    public static void main(String[] args) {
        new Thread(new AIOServer(6789)).start();
        new Thread(new AIOClient("127.0.0.1", 6789)).start();
    }

}
