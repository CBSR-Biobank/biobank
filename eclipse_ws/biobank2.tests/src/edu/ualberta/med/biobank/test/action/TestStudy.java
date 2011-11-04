package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestStudy extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        siteId =
            SiteHelper.createSite(appService, name, "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>());
    }

    @Test
    public void testGetSiteCollection() throws Exception {
        Integer studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);

        List<Integer> siteIds =
            SiteHelper.createSites(appService, name,
                ActivityStatusEnum.ACTIVE, 5 + r.nextInt(15));
        siteIds.add(siteId);

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));

    }
}
