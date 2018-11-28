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

    private void prepareJBossHome(String basePath) {
        String tmpDir = PropertiesProvider.tmpDir().nonEmpty();
        File baseFile = FileUtils.get(basePath);

        try {
            String type = Files.probeContentType(baseFile.toPath());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find out content type of base jboss location at '" + baseFile.getPath() + "'");
        }
    }
}
