package com.l1nker4.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class TestSelector {

    public static void main(String[] args) throws IOException {
        testSelector();
    }


    /**
     * 事件的类型：accept：server端有连接请求时触发
     * connect：client发起请求
     * read：读数据
     * write：写数据
     *
     * @throws IOException
     */
    public static void testSelector() throws IOException {
        //创建Selector
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //将channel注册到selector
        //通过SelectionKey可以得到事件的元数据
        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));
        //accept
        while (true) {
            //没有事件发生，线程阻塞，有事件则恢复运行
            selector.select();

            //处理事件，获取当前selector监听的channel产生的所有事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                log.debug("key: {}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                    log.debug("Socket Channel: {}", sc);
                    iterator.remove();
                } else if (key.isReadable()) {
                    //拿到触发read的channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    int read = channel.read(buffer);
                    if (read == -1) {
                        key.cancel();
                    } else {
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                    }
                }
//                iterator.remove();
            }
        }
    }
}
