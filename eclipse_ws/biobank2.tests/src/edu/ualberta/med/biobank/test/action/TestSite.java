package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;

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
    public void testNameChecks() throws Exception {
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
            Assert
                .fail("should not be allowed to add site with same name short");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

}
