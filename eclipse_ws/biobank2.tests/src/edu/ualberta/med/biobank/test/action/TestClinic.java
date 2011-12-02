package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestClinic extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private Integer clinicId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        clinicId = ClinicHelper.createClinic(appService, name,
            ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void testSaveNew() throws Exception {

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
    public void testContacts() throws Exception {
        Set<Integer> idsAll = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < 10; ++i) {
            ContactSaveAction contactSaveAction = new ContactSaveAction();
            contactSaveAction.setClinicId(clinicId);
            contactSaveAction.setName(name + "_contact" + i);
            Integer id = appService.doAction(contactSaveAction).getId();

            idsAll.add(id);
            if (i < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
        }

        ClinicInfo clinicInfo =
            appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(idsAll, getContactIds(clinicInfo.contacts));

        // remove Set 2 from the clinic, Set 1 should be left
        ClinicSaveAction clinicSave =
            ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSave.setContactIds(set1);
        appService.doAction(clinicSave);

        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(set1, getContactIds(clinicInfo.contacts));

        // remove all
        clinicSave = ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSave.setContactIds(new HashSet<Integer>());
        appService.doAction(clinicSave);

        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertTrue(clinicInfo.contacts.isEmpty());

        // check that this clinic no longer has any source specimens
        openHibernateSession();
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + Contact.class.getName()
            + " ct WHERE ct.clinic.id=?");
        q.setParameter(0, clinicId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
        closeHibernateSession();
    }

    private Set<Integer> getContactIds(List<Contact> contacts)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();
        for (Contact c : contacts) {
            result.add(c.getId());
        }
        return result;
    }

}
