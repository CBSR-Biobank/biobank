package edu.ualberta.med.biobank.test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
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
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

        appService = ServiceConnection.getAppService(
            "https://" + System.getProperty("server", "136.159.173.90:9443")
                + "/biobank2", file.toURI().toURL(), "testuser", "test");

        cbsrSite = getCbsrSite();

        doTest(new SpeedTestContainer(appService, cbsrSite));
        doTest(new SpeedTestContainerWrapper(appService, cbsrSite));
    }

    private void doTest(SpeedTest test) throws Exception {
        long tsStart = (new Date()).getTime();
        test.doTest();
        long tsEnd = (new Date()).getTime();
        logger.info("test execution time is: " + (tsEnd - tsStart) + " ms");
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

abstract class SpeedTest {
    protected WritableApplicationService appService;

    protected SiteWrapper site;

    public SpeedTest(WritableApplicationService appService, SiteWrapper site) {
        this.appService = appService;
        this.site = site;
    }

    public abstract void doTest() throws Exception;
}

class SpeedTestContainer extends SpeedTest {

    private static final Logger logger = Logger
        .getLogger(SpeedTestContainer.class);

    public SpeedTestContainer(WritableApplicationService appService,
        SiteWrapper site) {
        super(appService, site);
    }

    @Override
    public void doTest() throws Exception {
        logger.info("speedTestContainer: test start");
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and containerType.topLevel = true", Arrays
            .asList(new Object[] { site.getId() }));
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
}

class SpeedTestContainerWrapper extends SpeedTest {

    private static final Logger logger = Logger
        .getLogger(SpeedTestContainer.class);

    public SpeedTestContainerWrapper(WritableApplicationService appService,
        SiteWrapper site) {
        super(appService, site);
    }

    @Override
    public void doTest() throws Exception {
        logger.info("speedTestContainerWrapper: test start");

        for (ContainerWrapper c : site.getTopContainerCollection()) {
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
}
