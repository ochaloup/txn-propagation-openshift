package io.narayana.test.utils;

import java.io.File;

public final class FileUtils {
    private FileUtils() {}

    public static File get(String filePath) {
        File file = new File(adjustFileLocation(filePath));
        if(!file.exists()) {
            throw new IllegalStateException("File '" + filePath + "' does not exists even when adjusted to '" + file.getPath() + "'");
        }
        return file;
    }

    public static String adjustFileLocation(final String fileLocation) {
        String normalizedFileName = fileLocation.trim().replaceFirst("^~",System.getProperty("user.home"));
        if(System.getProperty("basedir") != null) {
            if(!normalizedFileName.startsWith("/")) {
                normalizedFileName = System.getProperty("basedir") + "/" + normalizedFileName;
            }
        }
        return normalizedFileName;
    }
}
