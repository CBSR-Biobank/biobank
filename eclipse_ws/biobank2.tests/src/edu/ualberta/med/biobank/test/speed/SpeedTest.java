package edu.ualberta.med.biobank.test.speed;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class SpeedTest {
    protected WritableApplicationService appService;

    protected SiteWrapper site;

    public SpeedTest(WritableApplicationService appService, SiteWrapper site) {
        this.appService = appService;
        this.site = site;
    }

    public abstract void doTest() throws Exception;

}
