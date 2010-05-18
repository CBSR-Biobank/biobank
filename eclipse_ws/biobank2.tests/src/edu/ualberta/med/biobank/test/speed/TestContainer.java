package edu.ualberta.med.biobank.test.speed;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestContainer extends SpeedTest {

    private static final Logger logger = Logger.getLogger(TestContainer.class);

    public TestContainer(WritableApplicationService appService, SiteWrapper site) {
        super(appService, site);
    }

    @Override
    public void doTest() throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and containerType.topLevel = true", Arrays
            .asList(new Object[] { site.getId() }));
        List<Container> containers = appService.query(criteria);
        for (Container c : containers) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildPositionCollection().size());

            criteria = new HQLCriteria("from "
                + ContainerPosition.class.getName()

                + " as cp "
                // + " left join fetch cp.container as child"

                + " where cp.parentContainer.id = ?", Arrays
                .asList(new Object[] { c.getId() }));
            List<ContainerPosition> childrenPos = appService.query(criteria);

            for (ContainerPosition childPos : childrenPos) {
                Container child = childPos.getContainer();
                logger.info(child.getLabel());
                // logger.debug(child.getLabel() + ": number of children: "
                // + child.getChildPositionCollection().size());
            }

        }
    }

    // @Override
    public void doTestOld() throws Exception {
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
    }
}
