package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        FileUtils.createMultipleDirectories(jbossTarget)
            .create("standalone", "configuration")
            .create("standalone", "data")
            .create("standalone", "tmp")
            .create("standalone", "log")
            .create("standalone", "content");
            
        // get data from source to target to prepare runtime environment
        // jbossSource.toPath()
        Files.createDirectories(dir, attrs)
        mkdir -p "${JBOSS_TARGET_DIR}/standalone/configuration"
        mkdir -p "${JBOSS_TARGET_DIR}/standalone/data"
        mkdir -p "${JBOSS_TARGET_DIR}/standalone/tmp"
        mkdir -p "${JBOSS_TARGET_DIR}/standalone/log"
        mkdir -p "${JBOSS_TARGET_DIR}/standalone/content"
        cp "$JBOSS_HOME_BASE_DIR"/standalone/configuration/*.properties "${JBOSS_TARGET_DIR}/standalone/configuration"

        try {
            String type = Files.probeContentType(baseFile.toPath());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find out content type of base jboss location at '" + baseFile.getPath() + "'");
        }
    }
}
