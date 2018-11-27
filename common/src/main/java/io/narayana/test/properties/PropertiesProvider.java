package io.narayana.test.properties;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ConfigurationContext;

/**
 * Class to gathering properties to configure test runtime.
 */
public class PropertiesProvider {
    public static void main(String[] args) {
        ConfigurationContext context = ConfigurationProvider.getConfigurationContextBuilder()
            .addDefaultPropertySources()
            .addPropertySources(new EnvironmentPropertySourceLowercase())
            .addDefaultPropertyFilters()
            .addDefaultPropertyConverters()
            .build();
        Configuration configuration = ConfigurationProvider.createConfiguration(context);
        System.out.println("franta is: " + configuration.get("franta"));
    }
}
