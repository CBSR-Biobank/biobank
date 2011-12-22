package edu.ualberta.med.biobank.test.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupFormReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestResearchGroup extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void saveResearchGroup() throws Exception {
        AddressSaveInfo addressSaveInfo =
            new AddressSaveInfo(null, "test", "test", "test", "test", "test",
                "test", "test", "test", "test", "test");
        ResearchGroupSaveInfo save =
            new ResearchGroupSaveInfo(null, name + "rg", name + "rg",
                studyId, "comment", addressSaveInfo,
                ActivityStatusEnum.ACTIVE.getId());
        ResearchGroupSaveAction rgSave = new ResearchGroupSaveAction(save);

        Integer id = appService.doAction(rgSave).getId();

        ResearchGroupGetInfoAction rgInfoAction =
            new ResearchGroupGetInfoAction(id);
        ResearchGroupFormReadInfo rgInfo = appService.doAction(rgInfoAction);
        Assert.assertTrue(rgInfo.rg.name.equals(name + "rg"));
        Assert.assertTrue(rgInfo.rg.nameShort.equals(name + "rg"));
        Assert.assertTrue(rgInfo.rg.getStudy().getId().equals(studyId));
        Assert.assertTrue(rgInfo.rg.getActivityStatus().id
            .equals(ActivityStatusEnum.ACTIVE.getId()));

    }

    @Test
    public void testUpload() {

    }

}
