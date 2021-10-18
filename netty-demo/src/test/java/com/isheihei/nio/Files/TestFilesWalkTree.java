package com.isheihei.nio.Files;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFilesWalkTree {
    public static void main(String[] args) throws IOException {
        //递归删除文件夹，先删除文件，再删除空的文件夹，否则文件夹不为空会报错
        Files.walkFileTree(Paths.get("D:\\software\\Jdk"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    //遍历文件夹
    public static void walk() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\software\\Jdk"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                //打印所有的jar包
                if (file.toString().endsWith(".jar")) {
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dir count : " + dirCount + ", file count : " + fileCount + ", jar count : " + jarCount);

    }
}
