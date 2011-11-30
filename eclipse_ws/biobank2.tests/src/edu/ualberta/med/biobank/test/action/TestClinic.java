package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;

public class TestClinic extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private Integer clinicId;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        clinicId = ClinicHelper.createClinic(appService, name,
            ActivityStatusEnum.ACTIVE);
        siteId = SiteHelper.createSite(appService, "Site-" + name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(clinicId));
    }

    @Test
    public void testNameChecks() throws Exception {
        // ensure we can change name on existing clinic
        ClinicInfo clinicInfo =
            appService.doAction(new ClinicGetInfoAction(clinicId));
        clinicInfo.clinic.setName(name + "_2");
        ClinicSaveAction clinicSave =
            ClinicHelper.getSaveAction(appService, clinicInfo);
        appService.doAction(clinicSave);

        // ensure we can change short name on existing clinic
        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        clinicInfo.clinic.setNameShort(name + "_2");
        clinicSave = ClinicHelper.getSaveAction(appService, clinicInfo);
        appService.doAction(clinicSave);

        // test for duplicate name
        ClinicSaveAction saveClinic =
            ClinicHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE, false);
        try {
            appService.doAction(saveClinic);
            Assert.fail("should not be allowed to add clinic with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveClinic.setName(Utils.getRandomString(5, 10));
        saveClinic.setNameShort(name + "_2");

        try {
            appService.doAction(saveClinic);
            Assert
                .fail("should not be allowed to add clinic with same name short");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testSaveNew() throws Exception {

    }

}
