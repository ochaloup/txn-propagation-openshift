package io.narayana.test.standalone;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import io.narayana.test.properties.tamaya.TestConfig;

@ExtendWith(TempDirectory.class)
public class ApplicationServerRunTest {
    private static final String JBOSS_HOME_TEST_PARAM = "jboss.home.test";
    private static final String JBOSS_HOME_TEST_VALUE
        = System.getProperty(JBOSS_HOME_TEST_PARAM);
    private Path targetDir = null;

    @BeforeEach
    public void prepareServerStructure(@TempDir Path tempDir) {
        targetDir = tempDir.resolve("target");
        targetDir.toFile().mkdirs();

        assertThat(JBOSS_HOME_TEST_VALUE)
            .as("Parameter '" + JBOSS_HOME_TEST_PARAM + "' with definition of the jboss home directory "
                    + "for test is empty. Maybe the maven profile downloading the WFLY was not activated.")
            .isNotNull();
        TestConfig.put("jboss.home", JBOSS_HOME_TEST_VALUE);
        TestConfig.put("jboss.target.path", targetDir.toFile().getAbsolutePath());
    }

    @AfterEach
    public void cleanUp() {
        TestConfig.clear();
    }

    @Test
    public void standaloneServer(@TempDir Path tempDir) {
        ApplicationServer appServer = new ApplicationServer("default").prepare();
        appServer.start();
        assertThat(appServer.isStarted())
            .as("Application server with home directory '%s' should be started.", JBOSS_HOME_TEST_VALUE)
            .isTrue();
    }
}