package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.zeroturnaround.exec.ProcessExecutor;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.utils.FileUtils;

public class ApplicationServerPreparation {
    private final ApplicationServerMetadata appServer;
    private PropertiesProvider properties;

    public ApplicationServerPreparation(String serverName, PropertiesProvider properties) {
        appServer = ApplicationServerMetadata.instance().setName(serverName);
        this.properties = properties;
    }

    public void prepareWildFlyServer(File jbossOriginalHome, File jbossTarget, String configFile, int portOffset) {
        appServer
            .setJbossOriginHome(jbossOriginalHome)
            .setConfigFile(configFile)
            .setJbossHome(jbossTarget)
            .setPortOffset(portOffset);

        prepareJBossHome();
    }

    private void prepareJBossHome() {
        File jbossSource = appServer.getJbossOriginHome();
        File jbossTarget = appServer.getJbossHome();

        // clean the target directory
        org.apache.commons.io.FileUtils.deleteQuietly(jbossTarget);
        // check if the source is zip then we need to unzip first
        try {
            String type = Files.probeContentType(jbossSource.toPath());
            if(type.contains("zip")) {
                File unzipLocation = new File(properties.tmpDir(), appServer.getName());
                org.apache.commons.io.FileUtils.deleteDirectory(unzipLocation);
                FileUtils.unzip(jbossSource, unzipLocation);
                jbossSource = unzipLocation;
                appServer.setJbossOriginHome(jbossSource);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot find out content type of base jboss location at '" + jbossSource + "'", ioe);
        }

        FileUtils.createMultipleDirectories(jbossTarget)
            .create("standalone", "configuration")
            .create("standalone", "data")
            .create("standalone", "tmp")
            .create("standalone", "log")
            .create("standalone", "content");

        File targetConfiguration = FileUtils.toFile(jbossTarget, "standalone", "configuration");
        try {
            org.apache.commons.io.FileUtils.copyDirectory(
                    FileUtils.toFile(jbossSource, "standalone", "configuration"), targetConfiguration,
                    (filename) -> filename.getName().matches(".*\\.properties"));
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy properties file from " + FileUtils.toFile(jbossSource, "standalone", "configuration")
              + " to " + FileUtils.toFile(jbossTarget, "standalone", "configuration"));
        }

        // prepare configuration file to go
        try {
            File configFileAbs = new File(FileUtils.adjustFileLocation(appServer.getConfigFile()));
            if(configFileAbs.exists()) {
                org.apache.commons.io.FileUtils.copyFile(configFileAbs, targetConfiguration);
                appServer.setConfigFile(configFileAbs.getName());
            } else {
                File configFile = FileUtils.toFile(jbossSource, "standalone", "configuration", appServer.getConfigFile());
                if(!configFile.isFile()) throw new IllegalStateException("Cannot use non-existent config file '" + appServer.getConfigFile()
                    + "'" + " at '" + configFile + "'");
                org.apache.commons.io.FileUtils.copyFileToDirectory(configFile, targetConfiguration);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy configuration file " + appServer.getConfigFile() + " to configuration folder " + targetConfiguration, ioe);
        }
    }

    public void runJBoss() {
        try {
            String script = FileUtils.toFile(appServer.getJbossOriginHome(), "bin", "standalone.sh").getPath();
            new ProcessExecutor().command(script, "-c", appServer.getConfigFile(),
                "-Djboss.socket.binding.port-offset=" + appServer.getPortOffset(),
                "-Djboss.server.data.dir=" + FileUtils.toFile(appServer.getJbossHome(), "standalone", "data").getPath(),
                "-Djboss.server.log.dir=" + FileUtils.toFile(appServer.getJbossHome(), "standalone", "log").getPath(),
                "-Djboss.server.deploy.dir=" + FileUtils.toFile(appServer.getJbossHome(), "standalone", "content").getPath(),
                "-Djboss.server.config.dir=" + FileUtils.toFile(appServer.getJbossHome(), "standalone", "configuration").getPath())
            .execute();
        } catch (Exception te) {
            throw new RuntimeException("Cannot start app server " + appServer.getJbossHome());
        }
        // "$JBOSS_BIN"/bin/jboss-cli.sh -c --controller=localhost:$PORT --command=":read-attribute(name=server-state)" | grep -s running
    }
}
