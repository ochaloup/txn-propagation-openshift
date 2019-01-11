package io.narayana.test.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.logging.Logger;

import io.narayana.test.properties.EnvVariables;

public final class FileUtils {
    private static final Logger log = Logger.getLogger(FileUtils.class);
    private FileUtils() {}


    public static File getDirectory(String filePath) {
        return getDirectory(new StringExtended(filePath));
    }

    public static File getDirectory(StringExtended filePath) {
        File potentialDir = getIfExists(filePath.getNonEmpty());
        if(!potentialDir.isDirectory()) throw new IllegalStateException("File '" + potentialDir + "' is not a directory");
        return potentialDir;
    }

    public static File getOrCreateDirectory(StringExtended filePath) {
        File potentialDir = getIfExists(filePath.getNonEmpty());
        if(potentialDir.exists() && potentialDir.isFile())
            throw new IllegalStateException("File '" + potentialDir + "' exists as a regural file but it's expected to be a directory");
        if(!potentialDir.exists()) potentialDir.mkdirs();
        return potentialDir;
    }

    public static File get(StringExtended filePath) {
        return get(filePath.getNonEmpty());
    }

    public static File get(String filePath) {
        return new File(adjustFileLocation(filePath));
    }

    public static File getIfExists(StringExtended filePath) {
        return getIfExists(filePath.getNonEmpty());
    }

    public static File getIfExists(String filePath) {
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

    public static File toFile(File base, String... paths) {
        if(base == null) throw new NullPointerException("base");
        if(paths == null) return base;
        return new File(base, String.join(File.separator, paths));
    }

    public static File toFile(String... paths) {
        if(paths == null) throw new NullPointerException("paths");
        return new File(String.join(File.separator, paths));
    }

    /**
     * Searching for file (you could provide filename in form of file URL - it means file:/...)
     * 1. as absolute path
     * 2. as resource of class loader
     * 3. at {@link ProjectProperties#JAR_LIBRARY} path
     */
    public static File searchForFile(final String fileName) {
        // Consult absolute path
        File file = new File(fileName);

        // Working with URLs inside of try block
        try {
            // if url then convert to file
            if(!file.exists() && fileName.startsWith("file:")) {
                URL url = new URL(fileName);
                file = urlToFile(url);
            }
            // load as resource of class loader
            if(!file.exists()) {
                URL url = ClassLoader.getSystemClassLoader().getResource(file.getPath());
                if(url != null) {
                    file = urlToFile(url);
                }
            }
            // try to load from jar library path
            if(!file.exists()) {
                file = new File(EnvVariables.get(EnvVariables.BASE_DIR_PARAM), file.getPath());
            }
            // not able to find file neither at classloader path nor library path -> exception
            if(!file.exists()) {
                throw new IllegalStateException("Can't find file '" + fileName + "'");
            }
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
        return file;
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

    public static void writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file, false) ){
            writer.write(content);
        } catch (IOException ioe) {
            String contentPart = content.substring(0, 20);
            if(!contentPart.equals(content)) contentPart += "...";
            throw new IllegalStateException("Can't write to file '" + file + "' of content: " + contentPart, ioe);
        }
    }

    private static File urlToFile(final URL url) {
        return new File(url.getFile());
    }

    private static String adjustFileLocation(final String fileLocation) {
        String normalizedFileName = fileLocation.trim().replaceFirst(
                "^~", EnvVariables.getE(EnvVariables.HOME_DIR_PARAM).getNonEmpty());
        if(!normalizedFileName.startsWith(Character.toString(File.separatorChar))
                && EnvVariables.getE(EnvVariables.BASE_DIR_PARAM).isNotEmpty()) {
            normalizedFileName = EnvVariables.get(EnvVariables.BASE_DIR_PARAM) + File.separatorChar + normalizedFileName;
        }
        return normalizedFileName;
    }
}
