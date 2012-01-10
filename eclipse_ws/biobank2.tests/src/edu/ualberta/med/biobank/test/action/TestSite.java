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
import edu.ualberta.med.biobank.common.action.container.ContainerDeleteAction;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.site.SiteDeleteAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestSite extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private SiteSaveAction siteSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        siteSaveAction =
            SiteHelper.getSaveAction(name, name, ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void saveNew() throws Exception {
        // null name
        siteSaveAction.setName(null);
        try {
            appService.doAction(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        siteSaveAction.setName(name);
        siteSaveAction.setNameShort(null);
        try {
            appService.doAction(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setName(name);
        siteSaveAction.setActivityStatusId(null);
        try {
            appService.doAction(siteSaveAction);
            Assert.fail(
                "should not be allowed to add Site with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setActivityStatusId(
            ActivityStatusEnum.ACTIVE.getId());
        siteSaveAction.setAddress(null);
        try {
            appService.doAction(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setAddress(new Address());
        siteSaveAction.setStudyIds(null);
        try {
            appService.doAction(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void nameChecks() throws Exception {
        Integer siteId = appService.doAction(siteSaveAction).getId();

        // ensure we can change name on existing clinic
        SiteInfo siteInfo =
            appService.doAction(new SiteGetInfoAction(siteId));
        siteInfo.site.setName(name + "_2");
        siteSaveAction = SiteHelper.getSaveAction(appService, siteInfo);
        appService.doAction(siteSaveAction);

        // ensure we can change short name on existing site
        siteInfo = appService.doAction(new SiteGetInfoAction(siteId));
        siteInfo.site.setNameShort(name + "_2");
        siteSaveAction = SiteHelper.getSaveAction(appService, siteInfo);
        appService.doAction(siteSaveAction);

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
        Integer siteId = appService.doAction(siteSaveAction).getId();
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

    @Test
    public void delete() throws ApplicationException {
        Integer siteId = appService.doAction(siteSaveAction).getId();
        appService.doAction(new SiteDeleteAction(siteId));

    }

    private Provisioning createSiteWithContainerType()
        throws ApplicationException {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(appService, name);

        ContainerTypeSaveAction ctSaveAction =
            ContainerTypeHelper.getSaveAction(name, name, provisioning.siteId,
                true, 3, 10,
                getContainerLabelingSchemes().get(0).getId());
        Integer containerTypeId = appService.doAction(ctSaveAction).getId();
        provisioning.containerTypeIds.add(containerTypeId);
        return provisioning;
    }

    @Test
    public void deleteWithContainerTypes() throws ApplicationException {
        Provisioning provisioning = createSiteWithContainerType();
        try {
            appService.doAction(new SiteDeleteAction(provisioning.siteId));
            Assert
                .fail(
                "should not be allowed to delete a site with container types");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // delete container followed by site - should work now
        appService.doAction(new ContainerTypeDeleteAction(
            provisioning.containerTypeIds.get(0)));
        appService.doAction(new SiteDeleteAction(provisioning.siteId));
    }

    @Test
    public void deleteWithContainers() throws ApplicationException {
        Provisioning provisioning = createSiteWithContainerType();

        Integer containerTypeId = provisioning.containerTypeIds.get(0);

        ContainerSaveAction containerSaveAction = new ContainerSaveAction();
        containerSaveAction.setStatusId(ActivityStatusEnum.ACTIVE.getId());
        containerSaveAction.setBarcode(Utils.getRandomString(5, 10));
        containerSaveAction.setLabel("01");
        containerSaveAction.setSiteId(provisioning.siteId);
        containerSaveAction.setTypeId(containerTypeId);
        Integer containerId = appService.doAction(containerSaveAction).getId();

        try {
            appService.doAction(new SiteDeleteAction(provisioning.siteId));
            Assert
                .fail(
                "should not be allowed to delete a site with containers");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // delete container followed by site - should work now
        appService.doAction(new ContainerDeleteAction(containerId));
        appService.doAction(new ContainerTypeDeleteAction(containerTypeId));
        appService.doAction(new SiteDeleteAction(provisioning.siteId));
    }

    @Test
    public void deleteWithProcessingEvents() throws ApplicationException {

    }

    @Test
    public void deleteWithSrcDispatch() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(appService, name);

        DispatchHelper.createDispatch(appService, provisioning.clinicId,
            provisioning.siteId,
            provisioning.patientIds.get(0));

        // create a second site to dispatch to
        Integer siteId2 = appService.doAction(
            SiteHelper.getSaveAction(name + "_site2", name + "_site2",
                ActivityStatusEnum.ACTIVE)).getId();

        DispatchHelper.createDispatch(appService, provisioning.siteId, siteId2,
            provisioning.patientIds.get(0));

        try {
            appService.doAction(new SiteDeleteAction(provisioning.siteId));
            Assert
                .fail(
                "should not be allowed to delete a site which is a source of dispatches");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithDstDispatch() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(appService, name);

        DispatchHelper.createDispatch(appService, provisioning.clinicId,
            provisioning.siteId,
            provisioning.patientIds.get(0));

        try {
            appService.doAction(new SiteDeleteAction(provisioning.siteId));
            Assert
                .fail(
                "should not be allowed to delete a site which is a destination for dispatches");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
    }

}
