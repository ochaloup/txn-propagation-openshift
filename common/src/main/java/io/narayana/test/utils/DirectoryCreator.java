package io.narayana.test.utils;

import java.io.File;

public final class DirectoryCreator {
    private final File basePath;

    DirectoryCreator(File basePath) {
        this.basePath = basePath;
    }

    public DirectoryCreator create(String... subpath) {
        if (subpath == null || subpath.length == 0)
            return this;
        FileUtils.createDirectory(this.basePath, subpath);
        return this;
    }
}
