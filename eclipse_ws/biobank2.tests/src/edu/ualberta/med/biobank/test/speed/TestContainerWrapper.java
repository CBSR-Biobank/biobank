package edu.ualberta.med.biobank.test.speed;

import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class TestContainerWrapper extends SpeedTest {

    private static final Logger logger = Logger.getLogger(TestContainer.class);

    public TestContainerWrapper(WritableApplicationService appService,
        SiteWrapper site) {
        super(appService, site);
    }

    @Override
    public void doTest() throws Exception {
        for (ContainerWrapper c : site.getTopContainerCollection()) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildCount());
            Map<RowColPos, ContainerWrapper> pos = c.getChildren();
            for (ContainerWrapper child : pos.values()) {
                logger.debug(child.getLabel() + ": number of children: "
                    + child.getChildCount());
            }
        }
    }
}