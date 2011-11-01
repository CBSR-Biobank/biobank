package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestClinic extends TestAction {

    private Integer studyId;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "ClinicTest" + r.nextInt();

        studyId = StudyHelper.createStudy(appService, name,
            ActivityStatusEnum.ACTIVE);
        siteId = SiteHelper.createSite(appService, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(studyId));
    }

    @Test
    public void testSaveNew() throws Exception {

    }

}
