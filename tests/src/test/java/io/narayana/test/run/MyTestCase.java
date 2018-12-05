package io.narayana.test.run;

import java.io.File;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.junit.Test;

import io.narayana.test.bean.slsb.StatelessBean;
import io.narayana.test.bean.slsb.StatelessRemote;
import io.narayana.test.standalone.ApplicationServerPreparation;
import io.narayana.test.xaresource.TestXAResource;
import io.narayana.test.xaresource.TestXAResourceCheckerSingleton;

public class MyTestCase {
    private static final String DEFAULT_WEB_XML =
        "<web-app>\n" +
        "   <servlet-mapping>\n" +
        "      <servlet-name>javax.ws.rs.core.Application</servlet-name>\n" +
        "      <url-pattern>/*</url-pattern>\n" +
        "   </servlet-mapping>\n" +
        "</web-app>";

    @Test
    public void test() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "mytest.war")
            .addClasses(StatelessBean.class, StatelessRemote.class)
            .addClasses(TestXAResource.class, TestXAResourceCheckerSingleton.class)
            .addAsWebInfResource(new StringAsset(DEFAULT_WEB_XML),"web.xml");
        new ZipExporterImpl(archive).exportTo(new File(archive.getName()), true);

        Configuration configuration = ConfigurationProvider.getConfiguration();
        System.out.println("franta is: " + configuration.get("franta"));

        ApplicationServerPreparation prep = new ApplicationServerPreparation();
        prep.prepareWildFlyServer("myserver");
    }

    private void prepareDeploymentClient() {
        return;
    }
}
