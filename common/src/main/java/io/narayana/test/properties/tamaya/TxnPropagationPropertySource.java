package io.narayana.test.properties.tamaya;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tamaya.core.propertysource.BasePropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.jboss.logging.Logger;

import io.narayana.test.utils.FileUtils;

public class TxnPropagationPropertySource extends BasePropertySource {
    private static final Logger log = Logger.getLogger(TxnPropagationPropertySource.class);
    private static final String PROPERY_FILE_PARAM_NAME = "txn.properties";

    private final Map<String, PropertyValue> properties;

    public TxnPropagationPropertySource() {
        super("txn-propagation-property-file");

        properties = loadPropertiesFromConfigFile(PROPERY_FILE_PARAM_NAME);
    }

    @Override
    public PropertyValue get(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return properties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    private Map<String, PropertyValue> loadPropertiesFromConfigFile(String configFileParamName) {
        String propertyFileLocation = System.getProperty(configFileParamName);
        if(propertyFileLocation == null) {
            log.debugf("Property file '%s' was not defined", configFileParamName);
            return new HashMap<>();
        }

        File propertyFileLocationAdjusted = FileUtils.getIfExists(propertyFileLocation);

        try(FileInputStream inputStream = new FileInputStream(propertyFileLocationAdjusted)) {
            Properties props = new Properties();
            props.load(inputStream);
            Map<String, PropertyValue> returnedProperties = new HashMap<>();
            for(String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                returnedProperties.put(key, PropertyValue.of(key, value, getName()));
            }
            return returnedProperties;
        } catch (FileNotFoundException fnf) {
            log.warnf("You defined value '%s' for property '%s' which was adjusted for '%s'. Such file does not exist",
                    propertyFileLocation, configFileParamName, propertyFileLocationAdjusted);
            log.debug(fnf);
        } catch (IOException ioe) {
            log.debugf(ioe, "Cannot close file '%s'", propertyFileLocationAdjusted);
        }
        return new HashMap<>();
    }
}