package io.narayana.test.standalone;

import java.io.File;

public final class ApplicationServerMetadata {
    private String name;
    private File jbossOriginHome, // where the jboss come from
                 jbossHome; // where jboss is placed to and started from
    private String configFileDefinition; // standalone.xml or /tmp/standalone-full.xml or ...
    private String configFile; // config which is passed during server startup, it means only name like 'standalone.xml' (not path!)
    private int portOffset, cliPort;
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

    public String getConfigFileDefinition() {
        return configFileDefinition;
    }
    public ApplicationServerMetadata setConfigFileDefinition(String configFileDefinition) {
        this.configFileDefinition = configFileDefinition;
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
    public int getCliPort() {
        return cliPort;
    }
    public ApplicationServerMetadata setCliPort(int cliPort) {
        this.cliPort = cliPort;
        return this;
    }

    public File getConfigurationDir() {
        return configurationDir;
    }
    public ApplicationServerMetadata setConfigurationDir(File configurationDir) {
        this.configurationDir = configurationDir;
        return this;
    }
    public File getDataDir() {
        return dataDir;
    }
    public ApplicationServerMetadata setDataDir(File dataDir) {
        this.dataDir = dataDir;
        return this;
    }
    public File getTmpDir() {
        return tmpDir;
    }
    public ApplicationServerMetadata setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
        return this;
    }
    public File getLogDir() {
        return logDir;
    }
    public ApplicationServerMetadata setLogDir(File logDir) {
        this.logDir = logDir;
        return this;
    }
    public File getContentDir() {
        return contentDir;
    }
    public ApplicationServerMetadata setContentDir(File contentDir) {
        this.contentDir = contentDir;
        return this;
    }
}
