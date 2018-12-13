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
import io.narayana.test.standalone.ApplicationServerPreparation;
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

        PropertiesProvider properties = new PropertiesProvider(PropertiesProvider.DEFAULT);

        final String eap1 = "eap1";
        File jbossSource = properties.jbossSourceHome(eap1);
        File jbossTarget = properties.standaloneJbossTargetDir(eap1);
        ApplicationServerPreparation appServer1 = new ApplicationServerPreparation(eap1, properties);
        appServer1.prepareWildFlyServer(jbossSource, jbossTarget);
        appServer1.runJBoss();
    }
}
