package io.narayana.test.properties;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

/**
 * Test java class to gathering properties to configure test runtime.
 *
 * java -cp common/target/txn-propagation-common-1.0.0-SNAPSHOT.jar:/home/ochaloup/.m2/repository/org/apache/tamaya/tamaya-core/0.3-incubating/tamaya-core-0.3-incubating.jar:/home/ochaloup/.m2/repository/org/apache/tamaya/tamaya-api/0.3-incubating/tamaya-api-0.3-incubating.jar:/home/ochaloup/.m2/repository/org/apache/geronimo/specs/geronimo-annotation_1.2_spec/1.0-alpha-1/geronimo-annotation_1.2_spec-1.0-alpha-1.jar:/home/ochaloup/.m2/repository/org/jboss/logging/jboss-logging/3.3.2.Final/jboss-logging-3.3.2.Final.jar -Dtxn-propagation.properties=/tmp/props io.narayana.test.properties.PropertiesProvider
 */
public class PropertiesProvider {
    public static void main(String[] args) {
        /*
        ConfigurationContext context = ConfigurationProvider.getConfigurationContextBuilder()
            .addDefaultPropertySources()
            .addPropertySources(new EnvironmentPropertySourceLowercase())
            .addDefaultPropertyFilters()
            .addDefaultPropertyConverters()
            .build();
        Configuration configuration = ConfigurationProvider.createConfiguration(context);
        */
        Configuration configuration = ConfigurationProvider.getConfiguration();
        System.out.println("franta is: " + configuration.get("franta"));
    }
}

