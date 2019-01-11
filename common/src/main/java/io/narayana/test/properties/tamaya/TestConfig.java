package io.narayana.test.properties.tamaya;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tamaya.core.propertysource.BasePropertySource;
import org.apache.tamaya.spi.PropertyValue;

/**
 * <p>
 * This class provides way how to setup the Tamaya with configuration for tests.
 * We can just put properties via this config static methods.
 * As the property source is defined in <code>META-INF/services/org.apache.tamaya.spi.PropertySource</code>
 * data will be used.
 * <p>
 * https://www.infoq.com/articles/Apache-Tamaya-Configure-Once-Run-Everywhere
 */
public class TestConfig extends BasePropertySource {
    private static ThreadLocal<Map<String,PropertyValue>> maps = new ThreadLocal<>();
    private static String name = "test-config";

    static {
        maps.set(new ConcurrentHashMap<>());
    }

    public TestConfig(){
        super(name, 10000);
    }

    @Override
    public Map<String,PropertyValue> getProperties(){
        return maps.get();
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    public static PropertyValue put(String key, String value){
        return TestConfig.maps.get().put(key, PropertyValue.of(key, value, name));
    }

    public static PropertyValue remove(String key){
        return TestConfig.maps.get().remove(key);
    }

    public static void clear() {
        TestConfig.maps.get().clear();
    }
}
