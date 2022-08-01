package com.l1nker4.nio.blocking;

import com.l1nker4.nio.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestBlockingServer {


    public static void main(String[] args) throws IOException {
        testBlockingServer();

    }

    public static void testBlockingServer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //创建server
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        //accept
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //socketChannel用于和客户端的通信
            //如果为非阻塞，socketChannel为null
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                log.debug("connected...{}", socketChannel);
                socketChannel.configureBlocking(false);
                channels.add(socketChannel);
            }
            for (SocketChannel channel : channels) {
                //接收客户端的数据
                //channel也是非阻塞
                int read = channel.read(buffer);
                if (read > 0) {
                    log.debug("before read...");
                    buffer.flip();
                    ByteBufferUtil.debugAll(buffer);
                    buffer.clear();
                    log.debug("after read...");
                }
            }
        }
    }


}
