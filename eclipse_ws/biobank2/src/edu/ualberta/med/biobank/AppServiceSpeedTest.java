package edu.ualberta.med.biobank;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AppServiceSpeedTest {

    private static final Logger logger = Logger
        .getLogger(AppServiceSpeedTest.class.getName());

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite = null;

    private Map<String, ContainerWrapper> topContainersMap = null;

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
        URL url = file.toURI().toURL();

        appService = ServiceConnection.getAppService("https://"
            + System.getProperty("server", "cbsr.med.ualberta.ca:8443")
            + "/biobank2", url, "testuser", "test");

        cbsrSite = getCbsrSite();

        speedTestContainers();
    }

    private void speedTestContainers() throws Exception {
        initTopContainersMap();

        for (ContainerWrapper c : topContainersMap.values()) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildCount());
            Map<RowColPos, ContainerWrapper> pos = c.getChildren();
            for (ContainerWrapper child : pos.values()) {
                logger.info(child.getLabel() + ": number of children: "
                    + child.getChildCount());
            }
        }
    }

    private SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        return null;
    }

    private void initTopContainersMap() throws Exception {
        topContainersMap = new HashMap<String, ContainerWrapper>();
        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            topContainersMap.put(container.getLabel(), container);
        }
    }

}
