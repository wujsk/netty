package com.cyy.test.test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: netty
 * @description: 遍历文件树
 * @author: 酷炫焦少
 * @create: 2024-11-26 22:29
 **/
public class FilesWalkFileTreeTest {
    public static void main(String[] args) {
        // 拷贝多级文件
        String source = "D:\\毛概\\焦少康 2022015462";
        String target = "D:\\毛概\\焦少康 2022015462 123";
        try {
            Files.walk(Paths.get(source)).forEach(path -> {
                String targetName = path.toString().replace(source, target);
                try {
                    if (Files.isDirectory(path)) {
                        Files.createDirectory(Paths.get(targetName));
                    }
                    if (Files.isRegularFile(path)) {
                        Files.createFile(Paths.get(targetName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void m3() {
        // 不走回收站，极其危险
        try {
            Files.walkFileTree( Paths.get("D:\\c代码"), new SimpleFileVisitor<Path>(){
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查看jar有多少个
     */
    private static void m2() {
        AtomicInteger count = new AtomicInteger();
        try {
            Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\latest\\jre-1.8"), new SimpleFileVisitor<>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".jar")) {
                        count.incrementAndGet();
                    }
                    return super.visitFile(file, attrs);
                }
            });
            System.out.println(count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查看有多少个目录和文件
     */
    private static void m1() {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        try {
            Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\latest\\jre-1.8"), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("dir ==>" + dir.getFileName());
                    dirCount.incrementAndGet();
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("file ==>" + file.getFileName());
                    fileCount.incrementAndGet();
                    return super.visitFile(file, attrs);
                }
            });
            System.out.println("dirCount=" + dirCount + " fileCount=" + fileCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
