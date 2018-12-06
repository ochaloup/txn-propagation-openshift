package io.narayana.test.standalone;

import java.io.File;

public final class ApplicationServerMetadata {
    private String name;
    private File jbossOriginHome, // where the jboss come from
                 jbossHome; // where jboss is placed to and started from
    private String configFile; // standalone.xml or /tmp/standalone-full.xml or ...
    private int portOffset;

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

    public String getConfigFile() {
        return configFile;
    }
    public ApplicationServerMetadata setConfigFile(String configFile) {
        this.configFile = configFile;
        return this;
    }
    public int getPortOffset() {
        return portOffset;
    }
    public ApplicationServerMetadata setPortOffset(int portOffset) {
        this.portOffset = portOffset;
        return this;
    }
}
