package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestClinic extends TestAction {

    private StudyWrapper study;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "ClinicTest" + r.nextInt();

        // FIXME should not use wrappers for set up
        study = StudyHelper.addStudy(name + Utils.getRandomString(10));
        siteId = SiteHelper.createSite(appService, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(study.getId()));
    }

    @Test
    public void testSaveNew() throws Exception {

    }

}
