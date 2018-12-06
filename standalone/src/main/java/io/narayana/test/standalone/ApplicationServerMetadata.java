package io.narayana.test.standalone;

import java.io.File;

import io.narayana.test.utils.FileUtils;

public final class ApplicationServerMetadata {
    private String name;
    private File jbossOriginHome, // where the jboss come from
                 jbossHome; // where jboss is placed to and started from
    private File configurationDir, dataDir, tmpDir, logDir, contentDir;

    private ApplicationServerMetadata() {}
    public static ApplicationServerMetadata instance() {
        return new ApplicationServerMetadata();
    }

    public File getJbossHome() {
        return jbossHome;
    }

    public ApplicationServerMetadata setJbossHome(File jbossHome) {
        this.jbossHome = jbossHome;
        return this;
    }

    public File getJbossOriginHome() {
        return jbossOriginHome;
    }
    public ApplicationServerMetadata setJbossOriginHome(File originalDistParam) {
        this.jbossOriginHome = originalDistParam;
        return this;
    }

    public String getName() {
        return name;
    }

    public ApplicationServerMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public File getConfigurationDir() {
        if(configurationDir == null)
            return FileUtils.toFile(jbossHome, "standalone", "configuration");
        return configurationDir;
    }
    public void setConfigurationDir(File configurationDir) {
        this.configurationDir = configurationDir;
    }
    public File getDataDir() {
        if(dataDir == null)
            return FileUtils.toFile(jbossHome, "standalone", "data");
        return dataDir;
    }
    public void setDataDir(File dataDir) {
        this.dataDir = dataDir;
    }
    public File getTmpDir() {
        if(tmpDir == null)
            return FileUtils.toFile(jbossHome, "standalone", "tmp");
        return tmpDir;
    }
    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }
    public File getLogDir() {
        if(logDir == null)
            return FileUtils.toFile(jbossHome, "standalone", "log");
        return logDir;
    }
    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }
    public File getContentDir() {
        return contentDir;
    }
    public void setContentDir(File contentDir) {
        this.contentDir = contentDir;
    }
}
