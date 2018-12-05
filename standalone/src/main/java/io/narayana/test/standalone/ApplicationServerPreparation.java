package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.utils.FileUtils;

public class ApplicationServerPreparation {
    private Map<String, ApplicationServerMetadata> servers = new HashMap<>();

    public void prepareWildFlyServer(String serverName, String jbossDist) {
        ApplicationServerMetadata metadata = ApplicationServerMetadata.instance()
            .setName(serverName)
            .setOriginalDistParam(jbossDist);
    }

    private void prepareJBossHome(String serverName) {
        File jbossSource = PropertiesProvider.jbossSourceHome(serverName);
        File jbossTarget = PropertiesProvider.standaloneJbossTargetDir(serverName);

        // clean target directory for passing data in
        FileUtils.delete(jbossSource);
        // check if the source is zip then we need to unzip first
        try {
            String type = Files.probeContentType(jbossSource.toPath());
            if(type.contains("zip")) {
                File unzipLocation = new File(PropertiesProvider.tmpDir(), serverName);
                FileUtils.delete(unzipLocation);
                FileUtils.unzip(jbossSource, unzipLocation);
                jbossSource = unzipLocation;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find out content type of base jboss location at '" + jbossSource + "'");
        }

        FileUtils.createMultipleDirectories(jbossTarget)
            .create("standalone", "configuration")
            .create("standalone", "data")
            .create("standalone", "tmp")
            .create("standalone", "log")
            .create("standalone", "content");

        FileUtils.copy(
            FileUtils.toFile(jbossSource, "standalone", "configuration"),
            FileUtils.toFile(jbossTarget, "standalone", "configuration"), ".*\\.properties");

// echo "-c standalone-xts.xml -Djboss.socket.binding.port-offset=$PORT_OFFSET -Djboss.server.data.dir=${JBOSS_CONF_DIR}/standalone/data -Djboss.server.log.dir=${JBOSS_CONF_DIR}/standalone/log -Djboss.server.temp.dir=${JBOSS_CONF_DIR}/standalone/tmp -Djboss.server.deploy.dir=${JBOSS_CONF_DIR}/standalone/content -Djboss.server.config.dir=${JBOSS_CONF_DIR}/standalone/configuration"

    }
}
