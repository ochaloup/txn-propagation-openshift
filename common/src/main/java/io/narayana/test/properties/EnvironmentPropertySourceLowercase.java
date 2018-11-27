package io.narayana.test.properties;

import java.util.HashMap;
import java.util.Map;

import org.apache.tamaya.core.propertysource.BasePropertySource;
import org.apache.tamaya.core.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.spi.PropertyValue;

/**
 * The difference from the original Tamaya EnvironmentPropertySource is
 * that this one lowercase the env property and changes the '_' to '.'
 * for matching the value.
 */
public class EnvironmentPropertySourceLowercase extends BasePropertySource {
    // first running this source and then the original Tamaya EnvironmentPropertySource
    public static final int ORDINAL = EnvironmentPropertySource.DEFAULT_ORDINAL - 1;

    public EnvironmentPropertySourceLowercase() {
        super("environment-properties-lowercase", ORDINAL);
    }

    @Override
    public PropertyValue get(String key) {
        String keyToSearch = key.toUpperCase().replace(".", "_");
        return PropertyValue.of(key, System.getenv(keyToSearch), getName());
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        Map<String, PropertyValue> values = new HashMap<>();
        for (Map.Entry<String,String> entry: System.getenv().entrySet()) {
            String key = entry.getKey().toLowerCase().replace("_", ".");
            values.put(key, PropertyValue.of(key, entry.getValue(), getName()));
        }
        return values;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
