package io.narayana.test.utils;

import java.io.File;

public final class DirectoryCreator {
    private final File basePath;

    DirectoryCreator(File basePath) {
        this.basePath = basePath;
    }

    public DirectoryCreator create(String... subpath) {
        createSingle(subpath);
        return this;
    }

    public File createSingle(String... subpath) {
        if (subpath == null || subpath.length == 0)
            throw new IllegalStateException("No data for creation directory was provided");
        return FileUtils.createDirectory(this.basePath, subpath);
    }
}
