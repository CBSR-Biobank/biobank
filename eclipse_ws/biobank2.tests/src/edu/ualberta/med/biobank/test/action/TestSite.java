package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestSite extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        siteId = SiteHelper.createSite(appService, name,
            Utils.getRandomString(8, 12),
            ActivityStatusEnum.ACTIVE, new HashSet<Integer>());
    }

    @Test
    public void saveNew() throws Exception {
        // null name
        String altName = name + "_alt";
        SiteSaveAction saveAction =
            SiteHelper.getSaveAction(null, altName, ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        saveAction =
            SiteHelper.getSaveAction(altName, null, ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = SiteHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setActivityStatusId(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add Site with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = SiteHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setAddress(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = SiteHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setStudyIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing clinic
        SiteInfo siteInfo =
            appService.doAction(new SiteGetInfoAction(siteId));
        siteInfo.site.setName(name + "_2");
        SiteSaveAction siteSave =
            SiteHelper.getSaveAction(appService, siteInfo);
        appService.doAction(siteSave);

        // ensure we can change short name on existing site
        siteInfo = appService.doAction(new SiteGetInfoAction(siteId));
        siteInfo.site.setNameShort(name + "_2");
        siteSave = SiteHelper.getSaveAction(appService, siteInfo);
        appService.doAction(siteSave);

        // test for duplicate name
        SiteSaveAction saveSite =
            SiteHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveSite);
            Assert.fail("should not be allowed to add site with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveSite.setName(Utils.getRandomString(5, 10));
        saveSite.setNameShort(name + "_2");

        try {
            appService.doAction(saveSite);
            Assert.fail(
                "should not be allowed to add site with same name short");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void studyCollection() throws Exception {
        List<Integer> allStudyIds = new ArrayList<Integer>();
        Set<Integer> expectedResult = new HashSet<Integer>();
        Set<Integer> studyIdsSet1 = new HashSet<Integer>();
        Set<Integer> studyIdsSet2 = new HashSet<Integer>();

        for (int i = 0; i < 20; ++i) {
            Integer id = StudyHelper.createStudy(
                appService, name + "_study" + i, ActivityStatusEnum.ACTIVE);
            allStudyIds.add(id);
            if (i < 10) {
                studyIdsSet1.add(id);
            } else {
                studyIdsSet2.add(id);
            }
        }

        // add study set 1 to site created in startup
        SiteInfo siteInfo =
            appService.doAction(new SiteGetInfoAction(siteId));
        SiteSaveAction siteSaveAction =
            SiteHelper.getSaveAction(appService, siteInfo);
        siteSaveAction.setStudyIds(studyIdsSet1);
        appService.doAction(siteSaveAction);
        expectedResult.addAll(studyIdsSet1);
        siteInfo = appService.doAction(new SiteGetInfoAction(siteId));
        Assert.assertEquals(expectedResult,
            getStudyIds(siteInfo.studyCountInfo));

        // create a second site, site 2, with the second set of studies
        Integer siteId2 = SiteHelper.createSite(appService, name + "_2",
            Utils.getRandomString(8, 12),
            ActivityStatusEnum.ACTIVE, studyIdsSet2);
        siteInfo = appService.doAction(new SiteGetInfoAction(siteId2));
        expectedResult.clear();
        expectedResult.addAll(studyIdsSet2);
        Assert.assertEquals(expectedResult,
            getStudyIds(siteInfo.studyCountInfo));

        // make sure site 1 still has same collection
        siteInfo = appService.doAction(new SiteGetInfoAction(siteId));
        expectedResult.clear();
        expectedResult.addAll(studyIdsSet1);
        Assert.assertEquals(expectedResult,
            getStudyIds(siteInfo.studyCountInfo));
    }

    private Set<Integer> getStudyIds(List<StudyCountInfo> studyCountInfo) {
        Set<Integer> ids = new HashSet<Integer>();
        for (StudyCountInfo info : studyCountInfo) {
            ids.add(info.getStudy().getId());
        }
        return ids;
    }

}
