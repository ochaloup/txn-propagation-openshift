package io.narayana.test.properties;

import java.io.File;
import java.util.Arrays;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import io.narayana.test.utils.FileUtils;
import io.narayana.test.utils.StringExtended;
import io.narayana.test.utils.StringUtils;

/*
 * java -cp common/target/txn-propagation-common-1.0.0-SNAPSHOT.jar:/home/ochaloup/.m2/repository/org/apache/tamaya/tamaya-core/0.3-incubating/tamaya-core-0.3-incubating.jar:/home/ochaloup/.m2/repository/org/apache/tamaya/tamaya-api/0.3-incubating/tamaya-api-0.3-incubating.jar:/home/ochaloup/.m2/repository/org/apache/geronimo/specs/geronimo-annotation_1.2_spec/1.0-alpha-1/geronimo-annotation_1.2_spec-1.0-alpha-1.jar:/home/ochaloup/.m2/repository/org/jboss/logging/jboss-logging/3.3.2.Final/jboss-logging-3.3.2.Final.jar -Dtxn-propagation.properties=/tmp/props io.narayana.test.properties.PropertiesProvider
    ConfigurationContext context = ConfigurationProvider.getConfigurationContextBuilder()
        .addDefaultPropertySources()
        .addPropertySources(new EnvironmentPropertySourceLowercase())
        .addDefaultPropertyFilters()
        .addDefaultPropertyConverters()
        .build();
    Configuration configuration = ConfigurationProvider.createConfiguration(context);
 */
public final class PropertiesProvider {
    public static final PropertiesProvider INSTANCE = new PropertiesProvider();
    private final Configuration cfg;

    // -- standalone
    // source directory of jboss/wfly distribution
    private static final String JBOSS_HOME_PARAM = "jboss.home";
    private static final String JBOSS_DIST_PARAM = "jboss.dist";
    // directory where jboss will "copied" and started from
    private static final String JBOSS_TARGET_PATH_PARAM = "jboss.target.path";
    private static final String JBOSS_CONFIG = "jboss.config";
    private static final String JBOSS_PORT_OFFSET = "jboss.port.offset";
    private static final String JBOSS_CLI_PORT = "jboss.cli.port";

    private PropertiesProvider() {
        // use INSTANCE
        this.cfg = ConfigurationProvider.getConfiguration();
    }

    /**
     * Temporary dir where data can be loaded to which is defined by property
     * {@value EnvVariables#TMP_DIR_PARAM}.
     */
    public final File tmpDir() {
        StringExtended tmpDirString = new StringExtended(EnvVariables.TMP_DIR_PARAM);
        return FileUtils.getDirectory(tmpDirString);
    }

    /**
     * <p>
     * What was defined as place where jboss distribution could be found.
     * This is defined by properties {@value #JBOSS_DIST_PARAM} and then {@value #JBOSS_HOME_PARAM}
     * while the the suffix can be defined as priority place to take the value from.
     * <p>
     * For example, if defined {@code -Djboss.home.eap1} then {@code suffix}
     * is defined as {@code eap1} and the properties will be search in order.
     * <code>
     * jboss.dist.eap1 -> jboss.home.eap1 -> jboss.dist -> jboss.home
     * </code>
     */
    public final File jbossSourceHome(String eapName) {
        return getDirectory(eapName, JBOSS_DIST_PARAM, JBOSS_HOME_PARAM);
    }

    /**
     * Directory where jboss data will be loaded to and where the jboss will be started from,
     * defined by property {@value #JBOSS_TARGET_PATH_PARAM}.
     */
    public final File jbossTargetDir(String eapName) {
        StringExtended targetDir = takeFirstDefinedSuffixed(eapName, JBOSS_TARGET_PATH_PARAM);
        if(targetDir.isEmpty()) {
            return FileUtils.getDirectory("target");
        }
        return getOrCreateDirectory(eapName, JBOSS_TARGET_PATH_PARAM);
    }

    public final String jbossConfig(String eapName, String defaultValue) {
        StringExtended string = takeFirstDefinedSuffixed(eapName, JBOSS_CONFIG);
        if(string.isEmpty()) return defaultValue;
        return string.get();
    }
    public final int jbossPortOffset(String eapName, int defaultValue) {
        StringExtended string = takeFirstDefinedSuffixed(eapName, JBOSS_PORT_OFFSET);
        if(string.isEmpty()) return defaultValue;
        return new Integer(string.get());
    }
    public final int jbossCliPort(String eapName, int defaultValue) {
        StringExtended string = takeFirstDefinedSuffixed(eapName, JBOSS_CLI_PORT);
        if(string.isEmpty()) return defaultValue;
        return new Integer(string.get());
    }

    private StringExtended takeFirstDefinedSuffixed(String suffix, String... items) {
        if(StringUtils.isEmpty(suffix)) takeFirstDefined(items);

        String[] itemsPrefixed = new String[items.length * 2];
        int index = 0;
        for(String item: items) {
            itemsPrefixed[index++] = item + "." + suffix;
        }
        for(String item: items) {
            itemsPrefixed[index++] = item;
        }
        return takeFirstDefined(itemsPrefixed);
    }

    private StringExtended takeFirstDefined(String... items) {
        if(items == null) throw new NullPointerException("items");
        for(String item: items) {
            if(StringUtils.isNonEmpty(cfg.get(item))) {
                return new StringExtended(cfg.get(item));
            }
        }
        return new StringExtended((String) null);
    }

    private File getDirectory(String suffix, String... paramName) {
        if(paramName == null) throw new NullPointerException("paramName");
        try {
            StringExtended tmpDirString = takeFirstDefinedSuffixed(suffix, paramName);
            return FileUtils.getDirectory(tmpDirString);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("No existing directory for property '" + Arrays.asList(paramName) + "'", ise);
        }
    }

    private File getOrCreateDirectory(String suffix, String... paramName) {
        if(paramName == null) throw new NullPointerException("paramName");
        try {
            StringExtended tmpDirString = takeFirstDefinedSuffixed(suffix, paramName);
            return FileUtils.getOrCreateDirectory(tmpDirString);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("Error to gain directory for property '" + Arrays.asList(paramName) + "'", ise);
        }
    }
}

