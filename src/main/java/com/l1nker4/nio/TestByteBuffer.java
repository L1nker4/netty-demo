package com.l1nker4.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.l1nker4.nio.ByteBufferUtil.*;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
//        readFromFile();
//        readWrite();
//        testScatter();
        testCopyPackage();
    }

    public static void readFromFile() {
        //1.通过IO流获取FileChannel
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(buffer);
                log.debug("本次读取长度为：{}", len);
                if (len == -1) {
                    break;
                }
                //切换到读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("当前读取字节为：{}", (char) b);
                }
                //buffer切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readWrite() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        //class java.nio.HeapByteBuffer     堆内存，    效率低，收到GC的影响
        //class java.nio.DirectByteBuffer   对外内存，  效率较高

        System.out.println(buffer.getClass());
        System.out.println(ByteBuffer.allocateDirect(10).getClass());
        buffer.put((byte) 0x61);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        buffer.flip();
        debugAll(buffer);
        System.out.println((char) buffer.get());
        debugAll(buffer);
        buffer.clear();
        debugAll(buffer);
        buffer.put((byte) 0x71);
        debugAll(buffer);
        System.out.println((char) buffer.get());
        debugAll(buffer);
        buffer.rewind();
        debugAll(buffer);
        System.out.println((char) buffer.get());
        debugAll(buffer);
        System.out.println((char) buffer.get(3));
        debugAll(buffer);
    }

    /**
     * 字符串写入bytebuffer
     */
    public static void testString() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        //getBytes
        String str = "hello";
        buffer.put(str.getBytes());
        debugAll(buffer);
        buffer.clear();

        //Charset.defaultddCharset()
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(str);
        debugAll(buffer1);

        //wrap
        ByteBuffer buffer2 = ByteBuffer.wrap(str.getBytes());
        debugAll(buffer2);

        System.out.println(StandardCharsets.UTF_8.decode(buffer2).toString());
    }

    /**
     * 测试分散读取
     */
    public static void testScatter() {
        ByteBuffer b1 = ByteBuffer.allocate(4);
        ByteBuffer b2 = ByteBuffer.allocate(4);
        ByteBuffer b3 = ByteBuffer.allocate(4);
        ByteBuffer[] arr = new ByteBuffer[]{b1, b2, b3};
        try (FileChannel channel = new RandomAccessFile("data.txt", "rw").getChannel()) {
            channel.read(arr);
            for (ByteBuffer buffer : arr) {
                buffer.flip();
                debugAll(buffer);
            }
            channel.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 粘包、半包问题
     * 业务代码对缓冲区的错误处理
     */
    public static void testCopyPackage(){
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    /**
     * 对buffer进行split，解决粘包问题
     * @param source
     */
    public static void split(ByteBuffer source){
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                //存储到ByteBuffer
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                //数据填充到target
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
