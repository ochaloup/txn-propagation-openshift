package io.narayana.test.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.zeroturnaround.exec.ProcessExecutor;

import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.utils.DirectoryCreator;
import io.narayana.test.utils.FileUtils;

public class ApplicationServer {
    private final ApplicationServerMetadata appServer;
    private PropertiesProvider properties;

    private static final int DEFAULT_CLI_PORT = 9990;

    public ApplicationServer(String serverName) {
        this(serverName, PropertiesProvider.DEFAULT);
    }

    public ApplicationServer(String serverName, PropertiesProvider properties) {
        appServer = ApplicationServerMetadata.instance().setName(serverName);
        appServer
            .setJbossHome(properties.jbossTargetDir(serverName))
            .setJbossOriginHome(properties.jbossSourceHome(serverName))
            .setConfigFileDefinition(properties.jbossConfig(serverName, "standalone.xml"))
            .setPortOffset(properties.jbossPortOffset(serverName, 0))
            .setCliPort(properties.jbossCliPort(serverName, DEFAULT_CLI_PORT + appServer.getPortOffset()));
        this.properties = properties;
    }

    public void prepare() {
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
            File configFile = FileUtils.toFile(jbossSource, "standalone", "configuration", appServer.getConfigFileDefinition());
            if(configFile.exists()) { // searching for config file at the JBOSS_HOME/standalone/configuration
                org.apache.commons.io.FileUtils.copyFile(configFile, appServer.getConfigurationDir());
            } else { // trying to find config file as absolute path definition
                configFile = FileUtils.searchForFile(appServer.getConfigFileDefinition());
                org.apache.commons.io.FileUtils.copyFile(configFile, appServer.getConfigurationDir());
            }
            appServer.setConfigFile(configFile.getName()); // name of config file used during server startup
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot copy configuration file " + appServer.getConfigFile() + " to configuration folder "
                    + appServer.getConfigurationDir(), ioe);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot use non-existent config file '" + appServer.getConfigFileDefinition()
                + "' for further start of the jboss from '" + jbossSource + "'", e);
        }
    }

    public void start() {
        start();
    }

    public void start(String... additionalParams) {
        try {
            String script = FileUtils.toFile(appServer.getJbossOriginHome(), "bin", getCommand("standalone")).getPath();
            String[] params = new String[]{script, "-c", appServer.getConfigFile(),
                    "-Djboss.socket.binding.port-offset=" + appServer.getPortOffset(),
                    "-Djboss.server.data.dir=" + appServer.getDataDir().getPath(),
                    "-Djboss.server.log.dir=" + appServer.getLogDir().getPath(),
                    "-Djboss.server.deploy.dir=" + appServer.getContentDir().getPath(),
                    "-Djboss.server.config.dir=" + appServer.getConfigurationDir().getPath()};
            new ProcessExecutor().command(ArrayUtils.addAll(params, additionalParams)).execute();
        } catch (Exception te) {
            throw new RuntimeException("Cannot start app server " + appServer.getJbossHome());
        }
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
            String script = FileUtils.toFile(appServer.getJbossOriginHome(), "bin", getCommand("jboss-cli")).getPath();
            return new ProcessExecutor().command(script, "-c", "--controller=localhost:" + appServer.getCliPort(),
                    String.format("--commands=%s", cliCommand))
                    .readOutput(true).execute().outputUTF8();
        } catch (Exception e) {
            throw new RuntimeException("Cannot run jboss cli command '" + cliCommand + "'");
        }
    }

    private String getCommand(String baseCommandNotSuffixed) {
        if(SystemUtils.IS_OS_LINUX) return baseCommandNotSuffixed + ".sh";
        else return baseCommandNotSuffixed + ".bat";
    }
}
