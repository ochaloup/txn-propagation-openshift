package io.narayana.test.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.logging.Logger;

public final class FileUtils {
    private static final Logger log = Logger.getLogger(FileUtils.class);
    private FileUtils() {}

    public static File get(StringExtended filePath) {
        return get(filePath.getNonEmpty());
    }

    public static File getDirectory(StringExtended filePath) {
        File potentialDir = get(filePath.getNonEmpty());
        if(!potentialDir.isDirectory()) throw new IllegalStateException("File '" + potentialDir + "' is not a directory");
        return potentialDir;
    }

    public static File getOrCreateDirectory(StringExtended filePath) {
        File potentialDir = get(filePath.getNonEmpty());
        if(potentialDir.exists() && potentialDir.isFile())
            throw new IllegalStateException("File '" + potentialDir + "' exists as a regural file but it's expected to be a directory");
        if(!potentialDir.exists()) potentialDir.mkdirs();
        return potentialDir;
    }

    public static File get(String filePath) {
        File file = new File(adjustFileLocation(filePath));
        if(!file.exists()) {
            throw new IllegalStateException("File '" + filePath + "' does not exists even when adjusted to '" + file.getPath() + "'");
        }
        return file;
    }

    public static File createDirectory(File baseFile, String... paths) {
        if(baseFile.exists() && baseFile.isFile())
            throw new IllegalStateException("The location '" + baseFile + "' corresponds to existent of regular non-directory file."
                    + "it can't be used as base directory for creating sub path '" + Arrays.asList(paths) + "'");
        if(!baseFile.exists()) baseFile.mkdirs();
        try {
            Path created = Files.createDirectories(Paths.get(baseFile.getPath(), paths));
            return created.toFile();
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot create directory at '" + baseFile + "' with subpath '" + Arrays.asList(paths) + "'", ioe);
        }
    }

    public static DirectoryCreator createMultipleDirectories(File baseFile) {
        return new DirectoryCreator(baseFile);
    }

    public static void delete(final File fileToDelete) {
        if(!fileToDelete.exists()) return;
        FileVisitor<Path> visitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if(dir == null) throw new NullPointerException("dir");
                if(attrs == null) throw new NullPointerException("attrs");
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(fileToDelete.toPath(), visitor);
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot delete file at '" + fileToDelete + "'", ioe);
        }
    }

    public static void copy(final File from, final File to) {
        copy(from, to, ".*");
    }

    public static void copy(final File from, final File to, final String regex) {
        FileVisitor<Path> visitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if(dir == null) throw new NullPointerException("dir");
                if(attrs == null) throw new NullPointerException("attrs");
                // from path being /a/b, dir is /a/b/c, to path is /d then creating a directory /d/c
                Path toPathRelativeToDir = convertFromTo(dir);
                if(!toPathRelativeToDir.toFile().exists()) toPathRelativeToDir.toFile().mkdirs();
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.getFileName().toString().matches(regex)) {
                    Path copyDestination = convertFromTo(file);
                    Files.copy(file, copyDestination);
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                Path copyDestination = convertFromTo(file);
                Files.copy(file, copyDestination);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                return FileVisitResult.CONTINUE;
            }
            private Path convertFromTo(Path pathToHandle) {
                Path relativizedPath = from.toPath().relativize(pathToHandle);
                Path res = to.toPath().resolve(relativizedPath);
                log.debug("path: " + pathToHandle + " ,from: " + from + ", to: " + to + ", res: " + res);
                return res;
            }
        };
        try {
            Files.walkFileTree(from.toPath(), visitor);
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy file(s) from '" + from + "' to '" + to + "'", ioe);
        }
    }

    public static File toFile(File base, String... paths) {
        if(paths == null) return base;
        return new File(base, String.join(File.separator, paths));
    }

    public static void unzip(File fileToUnzip, File targetPath) {
        try {
            ZipFile zipFile = new ZipFile(fileToUnzip);
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
                log.debugf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

                File file = new File(targetPath, name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                is.close();
                fos.close();

            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String adjustFileLocation(final String fileLocation) {
        String normalizedFileName = fileLocation.trim().replaceFirst("^~",System.getProperty("user.home"));
        if(System.getProperty("basedir") != null) {
            if(!normalizedFileName.startsWith("/")) {
                normalizedFileName = System.getProperty("basedir") + "/" + normalizedFileName;
            }
        }
        return normalizedFileName;
    }
}
