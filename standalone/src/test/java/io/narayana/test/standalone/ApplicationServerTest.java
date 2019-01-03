package io.narayana.test.standalone;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import io.narayana.test.properties.PropertiesProvider;

@ExtendWith(TempDirectory.class)
public class ApplicationServerTest {

    @BeforeEach
    public void prepareServerStructure(@TempDir Path tempDir) {
        Path conf = tempDir.resolve("standalone" + File.separator + "configuration");
        conf.toFile().mkdirs();
        /*
        appServer.setConfigurationDir(creator.createSingle("standalone", "configuration"));
        appServer.setDataDir(creator.createSingle("standalone", "data"));
        appServer.setTmpDir(creator.createSingle("standalone", "tmp"));
        appServer.setLogDir(creator.createSingle("standalone", "log"));
        appServer.setContentDir(creator.createSingle("standalone", "content"));
        */
    }

    @Test
    public static void standaloneServer(@TempDir Path tempDir) {
        System.setProperty("jboss.home", tempDir.toFile().getPath());
        assertTrue(tempDir.resolve("standalone" + File.separator + "configuration").toFile().exists());
        ApplicationServer s = new ApplicationServer("default", PropertiesProvider.DEFAULT);
        System.out.println(s.isStarted() ? "YES" : "NO");
    }
}