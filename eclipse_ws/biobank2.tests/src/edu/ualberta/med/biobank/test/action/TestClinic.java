package edu.ualberta.med.biobank.test.action;

import java.util.Collection;
import java.util.HashSet;
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
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
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
    public void saveNew() throws Exception {
        // null name
        String altName = name + "_alt";
        ClinicSaveAction saveAction =
            ClinicHelper.getSaveAction(null, altName,
                ActivityStatusEnum.ACTIVE, true);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        saveAction =
            ClinicHelper.getSaveAction(altName, null,
                ActivityStatusEnum.ACTIVE, true);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = ClinicHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE, true);
        saveAction.setActivityStatusId(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add Clinic with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = ClinicHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE, true);
        saveAction.setAddress(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = ClinicHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE, true);
        saveAction.setContactSaveInfos(null);
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
    public void contacts() throws Exception {
        Set<ContactSaveInfo> contactsAll = new HashSet<ContactSaveInfo>();
        Set<ContactSaveInfo> set1 = new HashSet<ContactSaveInfo>();
        Set<ContactSaveInfo> set2 = new HashSet<ContactSaveInfo>();

        for (int i = 0; i < 10; ++i) {
            ContactSaveInfo contactSaveInfo = new ContactSaveInfo();
            contactSaveInfo.name = name + "_contact" + i;

            contactsAll.add(contactSaveInfo);
            if (i < 5) {
                set1.add(contactSaveInfo);
            } else {
                set2.add(contactSaveInfo);
            }
        }

        ClinicSaveAction clinicSave =
            ClinicHelper.getSaveAction(name + "_2",
                name + "_2", ActivityStatusEnum.ACTIVE, true);
        clinicSave.setContactSaveInfos(contactsAll);
        clinicId = appService.doAction(clinicSave).getId();

        ClinicInfo clinicInfo =
            appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(contactsAll),
            getContactNames(clinicInfo.contacts));

        // remove Set 2 from the clinic, Set 1 should be left
        clinicSave = ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSave.setContactSaveInfos(set1);
        appService.doAction(clinicSave);

        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(set1),
            getContactNames(clinicInfo.contacts));

        // remove all
        clinicSave = ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSave.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        appService.doAction(clinicSave);

        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertTrue(clinicInfo.contacts.isEmpty());

        // check that this clinic no longer has any contacts
        openHibernateSession();
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + Contact.class.getName()
            + " ct WHERE ct.clinic.id=?");
        q.setParameter(0, clinicId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
        closeHibernateSession();

        // TODO attempt to delete a contact that is assoc to a study
    }

    private Set<String> getContactNamesFromSaveInfo(
        Collection<ContactSaveInfo> contactSaveInfos)
        throws ApplicationException {
        Set<String> result = new HashSet<String>();
        for (ContactSaveInfo contactSaveInfo : contactSaveInfos) {
            result.add(contactSaveInfo.name);
        }
        return result;
    }

    private Set<String> getContactNames(Collection<Contact> contacts)
        throws ApplicationException {
        Set<String> result = new HashSet<String>();
        for (Contact contact : contacts) {
            result.add(contact.getName());
        }
        return result;
    }

    @Test
    public void delete() {

    }

    @Test
    public void deleteWithStudies() {

    }

    @Test
    public void deleteWithSrcDispatch() {

    }

    @Test
    public void deleteWithDstDispatch() {

    }

}
