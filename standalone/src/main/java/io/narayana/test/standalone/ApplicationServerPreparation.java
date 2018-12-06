package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.utils.FileUtils;

public class ApplicationServerPreparation {
    private ApplicationServerMetadata appServer;
    private PropertiesProvider properties;

    public void prepareWildFlyServer(String serverName, File jbossOriginalHome, PropertiesProvider properties) {
        appServer = ApplicationServerMetadata.instance()
            .setName(serverName)
            .setJbossOriginHome(jbossOriginalHome);
        this.properties = properties;
    }

    private void prepareJBossHome(final File jbossTarget) {
        appServer.setJbossHome(jbossTarget);
        File jbossSource = appServer.getJbossOriginHome();

        org.apache.commons.io.FileUtils.deleteQuietly(jbossTarget);
        // check if the source is zip then we need to unzip first
        try {
            String type = Files.probeContentType(jbossSource.toPath());
            if(type.contains("zip")) {
                File unzipLocation = new File(properties.tmpDir(), appServer.getName());
                org.apache.commons.io.FileUtils.deleteDirectory(unzipLocation);
                FileUtils.unzip(jbossSource, unzipLocation);
                jbossSource = unzipLocation;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find out content type of base jboss location at '" + jbossSource + "'", e);
        }

        FileUtils.createMultipleDirectories(jbossTarget)
            .create("standalone", "configuration")
            .create("standalone", "data")
            .create("standalone", "tmp")
            .create("standalone", "log")
            .create("standalone", "content");
        appServer.setConfigurationDir(FileUtils.toFile(jbossTarget, "standalone", "configuration"));

        try {
            org.apache.commons.io.FileUtils.copyDirectory(
                    FileUtils.toFile(jbossSource, "standalone", "configuration"),
                    FileUtils.toFile(jbossTarget, "standalone", "configuration"),
                    (filename) -> filename.getName().matches(".*\\.properties"));
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy properties file from " + FileUtils.toFile(jbossSource, "standalone", "configuration")
              + " to " + FileUtils.toFile(jbossTarget, "standalone", "configuration");
        }
// echo "-c standalone-xts.xml -Djboss.socket.binding.port-offset=$PORT_OFFSET -Djboss.server.data.dir=${JBOSS_CONF_DIR}/standalone/data -Djboss.server.log.dir=${JBOSS_CONF_DIR}/standalone/log -Djboss.server.temp.dir=${JBOSS_CONF_DIR}/standalone/tmp -Djboss.server.deploy.dir=${JBOSS_CONF_DIR}/standalone/content -Djboss.server.config.dir=${JBOSS_CONF_DIR}/standalone/configuration"

    }
}
