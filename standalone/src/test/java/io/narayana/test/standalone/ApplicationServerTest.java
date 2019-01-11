package io.narayana.test.standalone;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import io.narayana.test.properties.tamaya.TestConfig;
import io.narayana.test.utils.FileUtils;

@ExtendWith(TempDirectory.class)
public class ApplicationServerTest {

    @BeforeEach
    public void prepareServerStructure(@TempDir Path tempDir) {
        Path originServer = tempDir.resolve("origin");
        Path originStandalone = originServer.resolve("standalone");
        File confDir = originStandalone.resolve("configuration").toFile();
        confDir.mkdirs();
        FileUtils.writeFile(new File(confDir, "standalone.xml"), "<?xml version='1.0' encoding='UTF-8'?><server xmlns=\"urn:jboss:domain:10.0\"></server>");

        originStandalone.resolve("standalone" + File.separator + "data").toFile().mkdirs();
        originStandalone.resolve("standalone" + File.separator + "tmp").toFile().mkdirs();
        originStandalone.resolve("standalone" + File.separator + "log").toFile().mkdirs();
        originStandalone.resolve("standalone" + File.separator + "content").toFile().mkdirs();

        tempDir.resolve("target").toFile().mkdirs();
    }

    @Test
    public void standaloneServer(@TempDir Path tempDir) {
        Path originDir = tempDir.resolve("origin");
        TestConfig.put("jboss.home", originDir.toFile().getAbsolutePath());
        TestConfig.put("jboss.target.path", tempDir.resolve("target").toFile().getAbsolutePath());

        assertThat(originDir.resolve("standalone").resolve("configuration")).exists();

        ApplicationServer s = new ApplicationServer("default")
                .prepare();
        // System.out.println(s.isStarted() ? "YES" : "NO");
    }
}