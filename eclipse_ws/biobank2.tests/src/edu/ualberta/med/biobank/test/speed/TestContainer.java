package edu.ualberta.med.biobank.test.speed;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestContainer extends SpeedTest {

    private static final Logger logger = Logger.getLogger(TestContainer.class);

    public TestContainer(WritableApplicationService appService, SiteWrapper site) {
        super(appService, site);
    }

    public void testHql() throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and containerType.topLevel = true", Arrays
            .asList(new Object[] { site.getId() }));
        List<Container> containers = appService.query(criteria);
        for (Container c : containers) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildPositionCollection().size());

            criteria = new HQLCriteria("from "
                + ContainerPosition.class.getName() + " as cp "

                + " left join fetch cp.container as child"

                + " where cp.parentContainer.id = ?", Arrays
                .asList(new Object[] { c.getId() }));
            List<ContainerPosition> childrenPos = appService.query(criteria);

            for (ContainerPosition childPos : childrenPos) {
                Container child = childPos.getContainer();
                int count = child.getChildPositionCollection().size();
                logger.debug(child.getLabel() + ": number of children: "
                    + count);
            }

        }
    }

    public void testNonWrapper() throws Exception {
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
                int count = child.getChildPositionCollection().size();
                logger.debug(child.getLabel() + ": number of children: "
                    + count);
            }

        }
    }

    public void testWrapper() throws Exception {
        for (ContainerWrapper c : site.getTopContainerCollection()) {
            logger.info(c.getLabel() + ": number of children: "
                + c.getChildCount());
            Map<RowColPos, ContainerWrapper> pos = c.getChildren();
            for (ContainerWrapper child : pos.values()) {
                int count = child.getChildCount();
                logger.debug(child.getLabel() + ": number of children: "
                    + count);
            }
        }
    }

    public void t1estAliquots() throws Exception {
        HQLCriteria criteria = new HQLCriteria(
            "select distinct ap.container from "
                + AliquotPosition.class.getName() + " as ap "
                + "join ap.container as c " + "where c.site.id = ?", Arrays
                .asList(new Object[] { site.getId() }));
        List<Container> containers = appService.query(criteria);

        int count = 0;
        for (Container c : containers) {
            logger.info(c.getLabel() + ": number of aliquots: "
                + c.getAliquotPositionCollection().size());
            ++count;

            if (count >= 20)
                break;

        }

    }
}
