package io.narayana.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.junit.jupiter.api.Test;

import io.narayana.test.bean.slsb.StatelessBean;
import io.narayana.test.bean.slsb.StatelessRemote;
import io.narayana.test.properties.PropertiesProvider;
import io.narayana.test.standalone.ApplicationServer;
import io.narayana.test.xaresource.TestXAResource;
import io.narayana.test.xaresource.TestXAResourceCheckerSingleton;

public class StandardTest {
    private static final String DEFAULT_WEB_XML =
        "<web-app>\n" +
        "   <servlet-mapping>\n" +
        "      <servlet-name>javax.ws.rs.core.Application</servlet-name>\n" +
        "      <url-pattern>/*</url-pattern>\n" +
        "   </servlet-mapping>\n" +
        "</web-app>";

    @Test
    public void runningJboss() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "mytest.war")
            .addClasses(StatelessBean.class, StatelessRemote.class)
            .addClasses(TestXAResource.class, TestXAResourceCheckerSingleton.class)
            .addAsWebInfResource(new StringAsset(DEFAULT_WEB_XML),"web.xml");
        new ZipExporterImpl(archive).exportTo(new File(archive.getName()), true);

        final String eap1 = "eap1";
        ApplicationServer appServer1 = new ApplicationServer(eap1, PropertiesProvider.DEFAULT);
        appServer1.prepare();
        appServer1.start();
    }
}
