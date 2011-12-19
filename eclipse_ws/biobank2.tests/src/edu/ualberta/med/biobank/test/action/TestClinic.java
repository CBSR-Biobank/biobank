package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Collection;
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
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;
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
        name = testname.getMethodName() + r.nextInt();

        clinicSaveAction = ClinicHelper.getSaveAction(name, name,
            ActivityStatusEnum.ACTIVE, r.nextBoolean());
    }

    @Test
    public void saveNew() throws Exception {
        clinicSaveAction.setName(null);
        try {
            appService.doAction(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        clinicSaveAction.setName(name);
        clinicSaveAction.setNameShort(null);
        try {
            appService.doAction(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setNameShort(name);
        clinicSaveAction.setActivityStatusId(null);
        try {
            appService.doAction(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add Clinic with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE.getId());
        clinicSaveAction.setAddress(null);
        try {
            appService.doAction(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        clinicSaveAction.setAddress(new Address());
        clinicSaveAction.setContactSaveInfos(null);
        try {
            appService.doAction(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing clinic
        Integer clinicId = appService.doAction(clinicSaveAction).getId();
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
        ClinicSaveAction saveClinic2 =
            ClinicHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE, false);
        try {
            appService.doAction(saveClinic2);
            Assert.fail("should not be allowed to add clinic with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveClinic2.setName(Utils.getRandomString(5, 10));
        saveClinic2.setNameShort(name + "_2");

        try {
            appService.doAction(saveClinic2);
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
        Integer clinicId = appService.doAction(clinicSaveAction).getId();

        ClinicInfo clinicInfo =
            appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(contactsAll),
            getContactNames(clinicInfo.contacts));

        // remove Set 2 from the clinic, Set 1 should be left
        clinicSaveAction = ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSaveAction.setContactSaveInfos(set1);
        appService.doAction(clinicSaveAction);

        clinicInfo = appService.doAction(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(getContactNamesFromSaveInfo(set1),
            getContactNames(clinicInfo.contacts));

        // remove all
        clinicSaveAction = ClinicHelper.getSaveAction(appService, clinicInfo);
        clinicSaveAction.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        appService.doAction(clinicSaveAction);

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
        Integer clinicId = appService.doAction(clinicSaveAction).getId();
        appService.doAction(new ClinicDeleteAction(clinicId));
    }

    public static class Provisioning {
        Integer siteId;
        Integer studyId;
        Integer clinicId;
        List<Integer> patientIds;

        public Provisioning() {
            patientIds = new ArrayList<Integer>();
        }
    }

    /*
     * Creates a clinic, study and site. Clinic is linked to study via contact.
     * Study is linked to Site.
     * 
     * @returns site ID.
     */
    private static Provisioning provisionProcessingConfiguration(
        BiobankApplicationService appService, String basename)
        throws ApplicationException {
        Provisioning provisioning = new Provisioning();
        provisioning.clinicId =
            ClinicHelper.createClinicWithContacts(appService, basename
                + "_clinic", 10);
        ClinicInfo clinicInfo =
            appService.doAction(new ClinicGetInfoAction(provisioning.clinicId));
        StudySaveAction studySaveAction =
            StudyHelper.getSaveAction(basename + "_study", basename + "_study",
                ActivityStatusEnum.ACTIVE);
        HashSet<Integer> ids = new HashSet<Integer>();
        ids.add(clinicInfo.contacts.get(0).getId());
        studySaveAction.setContactIds(ids);
        provisioning.studyId = appService.doAction(studySaveAction).getId();

        SiteSaveAction siteSaveAction =
            SiteHelper.getSaveAction(basename + "_site", basename + "_site",
                ActivityStatusEnum.ACTIVE);
        ids = new HashSet<Integer>();
        ids.add(provisioning.studyId);
        siteSaveAction.setStudyIds(ids);
        provisioning.siteId = appService.doAction(siteSaveAction).getId();

        PatientSaveAction patientSaveAction =
            new PatientSaveAction(null, provisioning.studyId,
                basename + "_patient1", Utils.getRandomDate());
        provisioning.patientIds.add(appService.doAction(patientSaveAction)
            .getId());
        return provisioning;
    }

    @Test
    public void deleteWithStudies() throws ApplicationException {
        Provisioning provisioning =
            provisionProcessingConfiguration(appService, name);
        try {
            appService.doAction(new ClinicDeleteAction(provisioning.clinicId));
            Assert
                .fail("should not be allowed to delete a clinic linked to a study");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithSrcDispatch() throws Exception {
        Provisioning provisioning =
            provisionProcessingConfiguration(appService, name);

        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService,
                provisioning.siteId, provisioning.clinicId,
                DispatchState.CREATION.getId(), Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                provisioning.patientIds.get(0), provisioning.clinicId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        appService.doAction(new DispatchSaveAction(d, specs, shipsave));

        try {
            appService.doAction(new ClinicDeleteAction(provisioning.clinicId));
            Assert
                .fail(
                "should not be allowed to delete a clinic which is a source of dispatches");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void deleteWithDstDispatch() {

    }

}
