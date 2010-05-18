package edu.ualberta.med.biobank.test.speed;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AppServiceSpeedTest {

    private static final Logger logger = Logger
        .getLogger(AppServiceSpeedTest.class);

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite = null;

    public static void main(String[] args) {
        try {
            new AppServiceSpeedTest();
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    public AppServiceSpeedTest() throws Exception {
        PropertyConfigurator.configure("conf/log4j.properties");

        File file = new File("conf/all.keystore");

        String defaultServerUrl = "http://localhost:8080";
        // String defaultServerUrl = "https://136.159.173.90:9443";
        // String defaultServerUrl = "https://cbsr.med.ualberta.ca:8443";

        appService = ServiceConnection.getAppService(System.getProperty(
            "server.url", defaultServerUrl)
            + "/biobank2", file.toURI().toURL(), "testuser", "test");

        cbsrSite = getCbsrSite();

        doTest(new TestContainer(appService, cbsrSite));
        // doTest(new TestContainerWrapper(appService, cbsrSite));
        // doTest(new TestContainerAliquots(appService, cbsrSite));
    }

    private void doTest(SpeedTest test) throws Exception {
        logger.info("starting test " + test.getClass().getName());
        long tsStart = (new Date()).getTime();
        test.doTest();
        long tsEnd = (new Date()).getTime();
        logger.info("test " + test.getClass().getName()
            + " execution time is: " + (tsEnd - tsStart) + " ms");
    }

    private SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        return null;
    }
}
