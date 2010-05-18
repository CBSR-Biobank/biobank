package edu.ualberta.med.biobank.test.speed;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Container;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestContainerAliquots extends SpeedTest {

    private static final Logger logger = Logger.getLogger(TestContainer.class);

    public TestContainerAliquots(WritableApplicationService appService,
        SiteWrapper site) {
        super(appService, site);
    }

    @Override
    public void doTest() throws Exception {
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
