package com.l1nker4.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestBlockingClient {

    public static void main(String[] args) throws IOException {
        testBlockingClient();
    }

    public static void testBlockingClient() throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        sc.write(Charset.defaultCharset().encode("hello world"));
        System.out.println("waiting");
        sc.close();
    }
}
