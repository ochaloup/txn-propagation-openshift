package io.narayana.test.standalone;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import io.narayana.test.properties.tamaya.TestConfig;
import io.narayana.test.utils.FileUtils;

@ExtendWith(TempDirectory.class)
public class ApplicationServerTest {
    private static final String CONFIG_CONTENT =
            "<?xml version='1.0' encoding='UTF-8'?><server xmlns=\"urn:jboss:domain:10.0\"></server>";

    @BeforeEach
    public void prepareServerStructure(@TempDir Path tempDir) {
        Path originServer = tempDir.resolve("origin");
        Path originStandalone = originServer.resolve("standalone");
        File confDir = originStandalone.resolve("configuration").toFile();
        confDir.mkdirs();
        FileUtils.writeFile(new File(confDir, "standalone.xml"), CONFIG_CONTENT);
        FileUtils.writeFile(new File(confDir, "standalone-full.xml"), CONFIG_CONTENT);

        originStandalone.resolve("data").toFile().mkdirs();
        originStandalone.resolve("tmp").toFile().mkdirs();
        originStandalone.resolve("log").toFile().mkdirs();
        originStandalone.resolve("content").toFile().mkdirs();

        tempDir.resolve("target").toFile().mkdirs();
    }

    @AfterEach
    public void cleanUp() {
        TestConfig.clear();
    }

    @Test
    public void standaloneServer(@TempDir Path tempDir) {
        Path originDir = tempDir.resolve("origin");
        Path targetDir = tempDir.resolve("target");
        TestConfig.put("jboss.home", originDir.toFile().getAbsolutePath());
        TestConfig.put("jboss.target.path", targetDir.toFile().getAbsolutePath());

        assertThat(originDir.resolve("standalone").resolve("configuration")).exists();

        new ApplicationServer("default").prepare();

        Path targetStandalone = targetDir.resolve("standalone");
        assertThat(targetStandalone.resolve("configuration")).exists();
        assertThat(targetStandalone.resolve("configuration").resolve("standalone.xml")).exists();
        assertThat(targetStandalone.resolve("configuration").resolve("standalone-full.xml")).doesNotExist();
        assertThat(targetStandalone.resolve("tmp")).exists();
        assertThat(targetStandalone.resolve("log")).exists();
        assertThat(targetStandalone.resolve("content")).exists();
    }

    @Test
    public void configDefined(@TempDir Path tempDir) {
        Path originDir = tempDir.resolve("origin");
        Path targetDir = tempDir.resolve("target");
        TestConfig.put("jboss.home", originDir.toFile().getAbsolutePath());
        TestConfig.put("jboss.target.path", targetDir.toFile().getAbsolutePath());
        TestConfig.put("jbosss.config", "standalone-full.xml");

        new ApplicationServer("default").prepare();

        Path targetStandalone = targetDir.resolve("standalone");
        assertThat(targetStandalone.resolve("configuration").resolve("standalone-full.xml")).doesNotExist();
    }
}