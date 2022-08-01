package com.l1nker4.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestFileChannel {

    public static void main(String[] args) {
//        testTransferTo();
//        testPath();
        testFiles();
    }

    public static void testTransferTo() {
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel();) {
            //使用zero-copy
            long size = from.size();
            //left代表剩余未传输字节
            for (long left = size; left > 0; ) {
                //transferTo限制最大文件为2G，通过for多次传输可以超过限制
                log.debug("position: {}, left: {}", size - left, left);
                left -= from.transferTo(size - left, left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testPath() {
        Path path = Paths.get("data");
        System.out.println(path.normalize());
        log.debug(String.valueOf(Files.exists(path)));
    }

    /**
     * Files遍历文件夹
     */
    public static void testFiles() {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();

        try {
            Files.walkFileTree(Paths.get("D:\\project"), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith("java")){
                        System.out.println(file);
                    }

                    fileCount.incrementAndGet();
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("--->" + dir);
                    dirCount.incrementAndGet();
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testDelete(){

    }
}
