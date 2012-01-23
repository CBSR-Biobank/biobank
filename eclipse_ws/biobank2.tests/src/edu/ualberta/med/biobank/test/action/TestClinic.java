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
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenInfoAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestClinic extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private ClinicSaveAction clinicSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        clinicSaveAction = ClinicHelper.getSaveAction(name, name,
            ActivityStatusEnum.ACTIVE, R.nextBoolean());
    }

    @Test
    public void saveNew() throws Exception {
        clinicSaveAction.setName(null);
        try {
            EXECUTOR.exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        clinicSaveAction.setName(name);
        clinicSaveAction.setNameShort(null);
        try {
            EXECUTOR.exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setNameShort(name);
        clinicSaveAction.setActivityStatusId(null);
        try {
            EXECUTOR.exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add Clinic with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE.getId());
        clinicSaveAction.setAddress(null);
        try {
            EXECUTOR.exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setAddress(new Address());
        clinicSaveAction.setContactSaveInfos(null);
        try {
            EXECUTOR.exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // success path
        clinicSaveAction
            .setContactSaveInfos(new HashSet<ContactSaveInfo>());
        EXECUTOR.exec(clinicSaveAction);
    }

    @Test
    public void checkGetAction() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                provisioning.patientIds.get(0), provisioning.clinicId);
        EXECUTOR.exec(new CollectionEventGetSourceSpecimenInfoAction(ceventId))
            .getList();

        ClinicInfo clinicInfo =
            EXECUTOR.exec(new ClinicGetInfoAction(provisioning.clinicId));

        Assert.assertEquals("Active", clinicInfo.clinic.getActivityStatus()
            .getName());
        Assert.assertEquals(new Long(1), clinicInfo.patientCount);
        Assert.assertEquals(new Long(1), clinicInfo.collectionEventCount);
        Assert.assertEquals(1, clinicInfo.contacts.size());
        Assert.assertEquals(1, clinicInfo.studyInfos.size());
    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing clinic
        Integer clinicId = EXECUTOR.exec(clinicSaveAction).getId();
        ClinicInfo clinicInfo =
            EXECUTOR.exec(new ClinicGetInfoAction(clinicId));
        clinicInfo.clinic.setName(name + "_2");
        ClinicSaveAction clinicSave =
            ClinicHelper.getSaveAction(clinicInfo);
        EXECUTOR.exec(clinicSave);

        // ensure we can change short name on existing clinic
        clinicInfo = EXECUTOR.exec(new ClinicGetInfoAction(clinicId));
        clinicInfo.clinic.setNameShort(name + "_2");
        clinicSave = ClinicHelper.getSaveAction(clinicInfo);
        EXECUTOR.exec(clinicSave);

        // test for duplicate name
        ClinicSaveAction saveClinic2 =
            ClinicHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE, false);
        try {
            EXECUTOR.exec(saveClinic2);
            Assert.fail("should not be allowed to add clinic with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveClinic2.setName(Utils.getRandomString(5, 10));
        saveClinic2.setNameShort(name + "_2");

        try {
            EXECUTOR.exec(saveClinic2);
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

        clinicSaveAction.setContactSaveInfos(contactsAll);
        Integer clinicId = EXECUTOR.exec(clinicSaveAction).getId();

        ClinicInfo clinicInfo =
            EXECUTOR.exec(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(contactsAll),
            getContactNames(clinicInfo.contacts));

        // remove Set 2 from the clinic, Set 1 should be left
        clinicSaveAction =
            ClinicHelper.getSaveAction(clinicInfo);
        clinicSaveAction.setContactSaveInfos(set1);
        EXECUTOR.exec(clinicSaveAction);

        clinicInfo = EXECUTOR.exec(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(set1),
            getContactNames(clinicInfo.contacts));

        // remove all
        clinicSaveAction =
            ClinicHelper.getSaveAction(clinicInfo);
        clinicSaveAction.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        EXECUTOR.exec(clinicSaveAction);

        clinicInfo = EXECUTOR.exec(new ClinicGetInfoAction(clinicId));
        Assert.assertTrue(clinicInfo.contacts.isEmpty());

        // check that this clinic no longer has any contacts
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + Contact.class.getName()
            + " ct WHERE ct.clinic.id=?");
        q.setParameter(0, clinicId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
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
    public void delete() throws ApplicationException {
        // delete a study with no patients and no other associations
        Integer clinicId = EXECUTOR.exec(clinicSaveAction).getId();
        EXECUTOR.exec(new ClinicDeleteAction(clinicId));

        // hql query for site should return empty
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + Clinic.class.getName() + " WHERE id=?");
        q.setParameter(0, clinicId);
        Long result = HibernateUtil.getCountFromQuery(q);

        Assert.assertTrue(result.equals(0L));
    }

    @Test
    public void deleteWithStudies() throws ApplicationException {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);
        try {
            EXECUTOR.exec(new ClinicDeleteAction(
                provisioning.clinicId));
            Assert
                .fail("should not be allowed to delete a clinic linked to a study");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithSrcDispatch() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        DispatchHelper.createDispatch(EXECUTOR, provisioning.clinicId,
            provisioning.siteId,
            provisioning.patientIds.get(0));

        try {
            EXECUTOR.exec(new ClinicDeleteAction(
                provisioning.clinicId));
            Assert
                .fail(
                "should not be allowed to delete a clinic which is a source of dispatches");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithDstDispatch() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        // add second clinic to be the destination of the dispatch

        ClinicSaveAction csa2 =
            ClinicHelper.getSaveAction(name + "_clinic2", name,
                ActivityStatusEnum.ACTIVE, R.nextBoolean());
        Integer clinicId2 = EXECUTOR.exec(csa2).getId();

        DispatchHelper.createDispatch(EXECUTOR, provisioning.clinicId,
            clinicId2,
            provisioning.patientIds.get(0));

        try {
            EXECUTOR.exec(new ClinicDeleteAction(clinicId2));
            Assert
                .fail(
                "should not be allowed to delete a clinic which is a destination for dispatches");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

}
