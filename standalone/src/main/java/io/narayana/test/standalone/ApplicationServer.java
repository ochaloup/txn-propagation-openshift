package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.logging.Logger;
import org.zeroturnaround.exec.ProcessExecutor;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.runner.Runner;
import io.narayana.test.utils.DirectoryCreator;
import io.narayana.test.utils.FileUtils;

public class ApplicationServer {
    private static final Logger log = Logger.getLogger(ApplicationServer.class);
    private final ApplicationServerMetadata metadata;
    private PropertiesProvider conf = PropertiesProvider.INSTANCE;

    private static final int DEFAULT_CLI_PORT = 9990;

    public ApplicationServer(String serverName) {
        metadata = ApplicationServerMetadata.instance().setName(serverName);
        metadata
            .setJbossHome(conf.jbossTargetDir(serverName))
            .setJbossOriginHome(conf.jbossSourceHome(serverName))
            .setConfigFileDefinition(conf.jbossConfig(serverName, "standalone.xml"))
            .setPortOffset(conf.jbossPortOffset(serverName, 0))
            .setCliPort(conf.jbossCliPort(serverName, DEFAULT_CLI_PORT + metadata.getPortOffset()));
    }

    public ApplicationServer prepare() {
        // loading the original jboss location and target location that is constructed here and which is used for jboss start
        File jbossSource = metadata.getJbossOriginHome();
        File jbossTarget = metadata.getJbossHome();

        // clean the target directory
        org.apache.commons.io.FileUtils.deleteQuietly(jbossTarget);

        // check if the source is zip then we need to unzip first
        try {
            String type = Files.probeContentType(jbossSource.toPath());
            if(type.contains("zip")) {
                File unzipLocation = new File(conf.tmpDir(), metadata.getName());
                org.apache.commons.io.FileUtils.deleteDirectory(unzipLocation);
                FileUtils.unzip(jbossSource, unzipLocation);
                jbossSource = unzipLocation;
                metadata.setJbossOriginHome(jbossSource);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot find content type of base jboss location at '" + jbossSource + "'", ioe);
        }

        DirectoryCreator creator = FileUtils.createMultipleDirectories(jbossTarget);
        metadata.setConfigurationDir(creator.createSingle("standalone", "configuration"));
        metadata.setDataDir(creator.createSingle("standalone", "data"));
        metadata.setTmpDir(creator.createSingle("standalone", "tmp"));
        metadata.setLogDir(creator.createSingle("standalone", "log"));
        metadata.setContentDir(creator.createSingle("standalone", "content"));

        try {
            org.apache.commons.io.FileUtils.copyDirectory(
                    FileUtils.toFile(jbossSource, "standalone", "configuration"), metadata.getConfigurationDir(),
                    (filename) -> filename.getName().matches(".*\\.properties"));
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy properties file from " + FileUtils.toFile(jbossSource, "standalone", "configuration")
              + " to " + metadata.getConfigurationDir());
        }

        // prepare configuration file to go
        try {
            File configFile = FileUtils.toFile(jbossSource, "standalone", "configuration", metadata.getConfigFileDefinition());
            if(configFile.exists()) { // searching for config file at the JBOSS_HOME/standalone/configuration
                org.apache.commons.io.FileUtils.copyFileToDirectory(configFile, metadata.getConfigurationDir());
            } else { // trying to find config file as absolute path definition
                configFile = FileUtils.searchForFile(metadata.getConfigFileDefinition());
                org.apache.commons.io.FileUtils.copyFileToDirectory(configFile, metadata.getConfigurationDir());
            }
            metadata.setConfigFile(configFile.getName()); // name of config file used during server startup
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy configuration file " + metadata.getConfigFile() + " to configuration folder "
                    + metadata.getConfigurationDir(), ioe);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot use non-existent config file '" + metadata.getConfigFileDefinition()
                + "' for further start of the jboss from '" + jbossSource + "'", e);
        }
        return this;
    }

    public ApplicationServer start() {
        return start(new String[] {});
    }

    public ApplicationServer start(final String... additionalParams) {
        Runner.run(() -> executeStart(additionalParams));
        while(!isStarted()) {
            System.out.println("Waiting for server to start...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                // TODO: stop container probably
                Thread.currentThread().interrupt();
                throw new RuntimeException("Sleeping for app server startup interrupted");
            }
        }
        return this;
    }

    public boolean isStarted() {
        String out = cli(":read-attribute(name=server-state)");
        Pattern patternOutcome = Pattern.compile("\"outcome\" => \"(.+?)\"");
        Matcher matcherOutcome = patternOutcome.matcher(out);

        Pattern patternResult = Pattern.compile("\"result\" => \"(.+?)\"");
        Matcher matcherResult = patternResult.matcher(out);

        if(!matcherOutcome.find()) {
            throw new IllegalStateException("Cannot find 'outcome' string at result of the server-state cli command which was:" + String.format("%n") + out);
        }
        if(!matcherOutcome.group(1).equalsIgnoreCase("success")) {
            throw new IllegalStateException("Cannot succesfuly get result of the server-state cli command where where output was: " + String.format("%n") + out);
        }
        if(!matcherResult.find()) {
            throw new IllegalStateException("Cannot find 'result' string at result of the server-state cli command which was:" + String.format("%n") + out);
        }
        return matcherResult.group(1).equalsIgnoreCase("running");
    }

    // "$JBOSS_BIN"/bin/jboss-cli.sh -c --controller=localhost:$PORT --command=":read-attribute(name=server-state)" | grep -s running
    public String cli(String cliCommand) {
        try {
            String script = FileUtils.toFile(metadata.getJbossOriginHome(), "bin", getCommand("jboss-cli")).getPath();
            return new ProcessExecutor().command(script, "-c", "--controller=localhost:" + metadata.getCliPort(),
                    String.format("--commands=%s", cliCommand))
                    .readOutput(true).execute().outputUTF8();
        } catch (Exception e) {
            throw new RuntimeException("Cannot run jboss cli command '" + cliCommand + "'");
        }
    }


    private void executeStart(String... additionalParams) {
        String script = FileUtils.toFile(metadata.getJbossOriginHome(), "bin", getCommand("standalone")).getPath();
        String[] params = new String[]{script, "-c", metadata.getConfigFile(),
                "-Djboss.socket.binding.port-offset=" + metadata.getPortOffset(),
                "-Djboss.server.data.dir=" + metadata.getDataDir().getPath(),
                "-Djboss.server.log.dir=" + metadata.getLogDir().getPath(),
                "-Djboss.server.deploy.dir=" + metadata.getContentDir().getPath(),
                "-Djboss.server.config.dir=" + metadata.getConfigurationDir().getPath()};
        String[] allParams = ArrayUtils.addAll(params, additionalParams);
        log.debugf("starting server with command: '%s'", Arrays.asList(allParams));

        try {
            new ProcessExecutor().command(allParams).execute();
        } catch (Exception te) {
            throw new RuntimeException("Cannot start app server " + metadata.getJbossHome()
                + " with command: '" + Arrays.asList(allParams) + "'");
        }
    }

    private String getCommand(String baseCommandNotSuffixed) {
        if(SystemUtils.IS_OS_LINUX) return baseCommandNotSuffixed + ".sh";
        else return baseCommandNotSuffixed + ".bat";
    }
}
