package io.narayana.test.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.narayana.test.utils.FileUtils.toFile;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

@ExtendWith(TempDirectory.class)
public class FileUtilsTest {

    @Test
    void directoryCreationAndDeletion(@TempDir Path tempDir) {
        File base = FileUtils.createDirectory(tempDir.toFile(), "base");
        FileUtils.createMultipleDirectories(base)
            .create("a")
            .create("b", "c")
            .create("a", "b", "c");
        createFile(base, "b", "test.txt");

        assertTrue(toFile(base, "a").isDirectory(), "expecting 'base/a' being the directory");
        assertTrue(toFile(base, "b", "c").isDirectory(), "expecting 'base/b/c' being the directory");
        assertTrue(toFile(base, "a", "b", "c").isDirectory(), "expecting 'base/b/c' being the directory");
        assertTrue(toFile(base, "b", "test.txt").isFile(), "expecting 'base/b/c' being the directory");

        FileUtils.delete(base);

        assertFalse(toFile(base, "a").isDirectory(), "expecting 'base/a' being removed");
        assertFalse(toFile(base, "b", "c").isDirectory(), "expecting 'base/b/c' being removed");
        assertFalse(toFile(base, "a", "b", "c").isDirectory(), "expecting 'base/b/c' being removed");
        assertFalse(base.isDirectory(), "expecting 'base/b/c' being removed");
    }

    @Test
    void copy(@TempDir Path tempDir) {
        File source = new File(tempDir.toFile(), "source");
        File target = new File(tempDir.toFile(), "target");

        FileUtils.createMultipleDirectories(source)
          .create("a")
          .create("b", "c")
          .create("a", "b", "c");
        createFile(source, "b", "test.txt");
        createFile(source, "a", "b", "c", "test.txt");

        FileUtils.copy(source, target);

        assertTrue(toFile(target, "a").isDirectory(), "expecting 'a' being copied as the directory");
        assertTrue(toFile(target, "b", "c").isDirectory(), "expecting 'b/c' being copied as the directory");
        assertTrue(toFile(target, "a", "b", "c").isDirectory(), "expecting 'a/b/c' being copied as the directory");
        assertTrue(toFile(target, "b", "test.txt").isFile(), "expecting 'b/test.txt' being copied as the directory");
        assertTrue(toFile(target, "a", "b", "c", "test.txt").isFile(), "expecting 'a/b/c/test.txt' being copied as the file");

        createFile(source, "b", "c", "test.txt");
        createFile(source, "b", "c", "test.log");

        FileUtils.copy(toFile(source, "b", "c"), toFile(target, "b", "c"), ".*\\.log");
        assertFalse(toFile(target, "b", "c", "test.txt").isFile(), "expecting 'b/c/test.txt' was not copied");
        assertTrue(toFile(target, "b", "c", "test.log").isFile(), "expecting 'b/c/test.log' was copied");
    }

    private File createFile(File baseFileOrDir, String... subpaths) {
        File fileToCreate = toFile(baseFileOrDir, subpaths);
        try {
            PrintWriter writer = new PrintWriter(fileToCreate, "UTF-8");
            writer.println("first line");
            writer.close();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot write to file " + fileToCreate, e);
        }
        return fileToCreate;
    }
}
