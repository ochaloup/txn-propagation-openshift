package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.zeroturnaround.exec.ProcessExecutor;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.utils.DirectoryCreator;
import io.narayana.test.utils.FileUtils;

public class ApplicationServerPreparation {
    private final ApplicationServerMetadata appServer;
    private PropertiesProvider properties;

    public ApplicationServerPreparation(String serverName, PropertiesProvider properties) {
        appServer = ApplicationServerMetadata.instance().setName(serverName);
        this.properties = properties;
    }

    public void prepareWildFlyServer(File jbossOriginalHome, File jbossTarget) {
        prepareWildFlyServer(jbossOriginalHome, jbossTarget, "standalone.xml", 0);
    }

    public void prepareWildFlyServer(File jbossOriginalHome, File jbossTarget, String configFile, int portOffset) {
        appServer
            .setJbossOriginHome(jbossOriginalHome)
            .setConfigFileDefinition(configFile)
            .setJbossHome(jbossTarget)
            .setPortOffset(portOffset);

        prepareJBossHome();
    }

    private void prepareJBossHome() {
        // loading the original jboss location and target location that is constructed here and which is used for jboss start
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
            throw new IllegalStateException("Cannot find content type of base jboss location at '" + jbossSource + "'", ioe);
        }

        DirectoryCreator creator = FileUtils.createMultipleDirectories(jbossTarget);
        appServer.setConfigurationDir(creator.createSingle("standalone", "configuration"));
        appServer.setDataDir(creator.createSingle("standalone", "data"));
        appServer.setTmpDir(creator.createSingle("standalone", "tmp"));
        appServer.setLogDir(creator.createSingle("standalone", "log"));
        appServer.setContentDir(creator.createSingle("standalone", "content"));

        try {
            org.apache.commons.io.FileUtils.copyDirectory(
                    FileUtils.toFile(jbossSource, "standalone", "configuration"), appServer.getConfigurationDir(),
                    (filename) -> filename.getName().matches(".*\\.properties"));
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy properties file from " + FileUtils.toFile(jbossSource, "standalone", "configuration")
              + " to " + appServer.getConfigurationDir());
        }

        // prepare configuration file to go
        try {
            File configFile = FileUtils.get(appServer.getConfigFileDefinition());
            if(configFile.exists()) { // config file exists then use it as absolute path to go
                org.apache.commons.io.FileUtils.copyFile(configFile, appServer.getConfigurationDir());
            } else {
                // trying to find the config file as string which is in the configuration directory of the source jboss home
                configFile = FileUtils.toFile(jbossSource, "standalone", "configuration", appServer.getConfigFileDefinition());
                if(!configFile.isFile()) throw new IllegalStateException("Cannot use non-existent config file '" + appServer.getConfigFileDefinition()
                    + "'" + " '" + configFile + "' used from at source jboss home '" + jbossSource + "'");
                org.apache.commons.io.FileUtils.copyFileToDirectory(configFile, appServer.getConfigurationDir());
            }
            appServer.setConfigFile(configFile.getName()); // name of config file used during server startup
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy configuration file " + appServer.getConfigFile() + " to configuration folder "
                    + appServer.getConfigurationDir(), ioe);
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
