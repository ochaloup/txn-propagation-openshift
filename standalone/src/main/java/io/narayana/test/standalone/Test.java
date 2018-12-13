package io.narayana.test.standalone;

import io.narayana.test.properties.PropertiesProvider;

public class Test {
    public static void main(String[] args) {
        System.setProperty("jboss.home", "/home/ochaloup/jboss/wildfly/dist/target/wildfly-16.0.0.Beta1-SNAPSHOT");
        ApplicationServer s = new ApplicationServer("default", PropertiesProvider.DEFAULT);
        System.out.println(s.isStarted() ? "YES" : "NO");
    }
}