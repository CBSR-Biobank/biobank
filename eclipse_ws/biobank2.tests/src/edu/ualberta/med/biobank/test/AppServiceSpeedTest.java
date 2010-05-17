package edu.ualberta.med.biobank.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AppServiceSpeedTest {

    private static final Logger logger = Logger
        .getLogger(AppServiceSpeedTest.class.getName());

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite = null;

    private List<ContainerWrapper> topContainers = null;

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

        appService = ServiceConnection.getAppService("https://"
            + System.getProperty("server", "cbsr.med.ualberta.ca:8443")
            + "/biobank2", file.toURI().toURL(), "testuser", "test");

        cbsrSite = getCbsrSite();

        speedTestContainerWrapper();
        speedTestContainer();
    }

    private void speedTestContainerWrapper() throws Exception {
        logger.info("speedTestContainerWrapper: test start");
        initTopContainersMap();

        for (ContainerWrapper c : topContainers) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildCount());
            Map<RowColPos, ContainerWrapper> pos = c.getChildren();
            for (ContainerWrapper child : pos.values()) {
                logger.debug(child.getLabel() + ": number of children: "
                    + child.getChildCount());
            }
        }
        logger.info("speedTestContainerWrapper: test end");
    }

    private void speedTestContainer() throws ApplicationException {
        logger.info("speedTestContainer: test start");
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and containerType.topLevel = true", Arrays
            .asList(new Object[] { cbsrSite.getId() }));
        List<Container> containers = appService.query(criteria);
        for (Container c : containers) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildPositionCollection().size());
            for (ContainerPosition childPos : c.getChildPositionCollection()) {
                Container child = childPos.getContainer();
                logger.debug(child.getLabel() + ": number of children: "
                    + child.getChildPositionCollection().size());
            }

        }
        logger.info("speedTestContainer: test end");
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
        topContainers = new ArrayList<ContainerWrapper>();
        for (ContainerWrapper container : cbsrSite.getTopContainerCollection()) {
            topContainers.add(container);
        }
    }

}
