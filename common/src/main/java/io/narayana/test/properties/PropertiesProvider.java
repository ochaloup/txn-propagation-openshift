package io.narayana.test.properties;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

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
    private static final Configuration cfg = ConfigurationProvider.getConfiguration();

    private static final String TMP_DIR_PARAM = "java.io.tmpdir";
    private static final String JBOSS_HOME_PARAM = "jboss.home";
    private static final String JBOSS_DIST_PARAM = "jboss.dist";

    public static final StringExtended tmpDir() {
        return new StringExtended(cfg.get(TMP_DIR_PARAM));
    }

    public static final StringExtended jbossDist() {
        return takeFirstDefined(JBOSS_DIST_PARAM, JBOSS_HOME_PARAM);
    }

    private static StringExtended takeFirstDefined(String... items) {
        for(String item: items) {
            if(StringUtils.isNonEmpty(cfg.get(item))) {
                return new StringExtended(cfg.get(item));
            }
        }
        return new StringExtended((String) null);
    }
}

