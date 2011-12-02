package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.aliquotedspecimen.AliquotedSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetContactsAction;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.sourcespecimen.SourceSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyDeleteAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * This test suite assumes that the association between Sites and Studies is
 * tested in the Site test class.
 * 
 */
public class TestStudy extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        studyId = StudyHelper.createStudy(appService, name,
            ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void saveNew() throws Exception {
        // null name
        String altName = name + "_alt";
        StudySaveAction saveAction =
            StudyHelper.getSaveAction(null, altName, ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        saveAction =
            StudyHelper.getSaveAction(altName, null, ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setActivityStatusId(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setSiteIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setContactIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setSourceSpcIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setAliquotSpcIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        saveAction = StudyHelper.getSaveAction(altName, altName,
            ActivityStatusEnum.ACTIVE);
        saveAction.setStudyEventAttrIds(null);
        try {
            appService.doAction(saveAction);
            Assert.fail(
                "should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing study
        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        studyInfo.study.setName(name + "_2");
        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        appService.doAction(studySave);

        // ensure we can change short name on existing study
        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
        studyInfo.study.setNameShort(name + "_2");
        studySave = StudyHelper.getSaveAction(appService, studyInfo);
        appService.doAction(studySave);

        // test for duplicate name
        StudySaveAction saveStudy =
            StudyHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE);
        try {
            appService.doAction(saveStudy);
            Assert
                .fail("should not be allowed to add study with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveStudy.setName(Utils.getRandomString(5, 10));
        saveStudy.setNameShort(name + "_2");

        try {
            appService.doAction(saveStudy);
            Assert
                .fail("should not be allowed to add study with same name short");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void contactCollection() throws Exception {
        // check for empty contact list after creation of study
        Assert.assertTrue(getStudyContacts(studyId).isEmpty());

        int numClinics = r.nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds =
            ClinicHelper.createClinicsWithContacts(appService,
                name, numClinics, numContacts);

        List<Contact> studyContactsSet1 = new ArrayList<Contact>();
        List<Contact> studyContactsSet2 = new ArrayList<Contact>();
        Set<Contact> expectedStudyContacts = new HashSet<Contact>();

        // get a contact id from each clinic
        for (Integer clinicId : clinicIds) {
            List<Contact> contacts =
                appService.doAction(new ClinicGetContactsAction(clinicId))
                    .getList();
            Assert.assertNotNull(contacts);
            Assert.assertNotNull(contacts.get(0));
            Assert.assertNotNull(contacts.get(1));
            studyContactsSet1.add(contacts.get(0));
            studyContactsSet2.add(contacts.get(1));
        }

        // add a contact one by one from set 1
        for (Contact c : studyContactsSet1) {
            expectedStudyContacts.add(c);
            studyAddContacts(Arrays.asList(c));
            Assert.assertEquals(expectedStudyContacts,
                getStudyContacts(studyId));
        }

        // add contact set 2
        studyAddContacts(studyContactsSet2);
        expectedStudyContacts.addAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts, getStudyContacts(studyId));

        // remove all contacts from set 1 individually
        for (Contact c : studyContactsSet1) {
            expectedStudyContacts.remove(c);
            studyRemoveContacts(Arrays.asList(c));
            Assert.assertEquals(expectedStudyContacts,
                getStudyContacts(studyId));
        }

        // remove contact set 2
        studyRemoveContacts(studyContactsSet2);
        expectedStudyContacts.removeAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts, getStudyContacts(studyId));
        Assert.assertTrue(getStudyContacts(studyId).isEmpty());

        // test removing clinics - should fail
        studyAddContacts(studyContactsSet2);
        expectedStudyContacts.addAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts, getStudyContacts(studyId));

        for (Contact c : studyContactsSet2) {
            try {
                appService.doAction(new ClinicDeleteAction(c.getClinic()
                    .getId()));
                Assert
                    .fail(
                    "should not be allowed to delete a clinic with contact linked to study");
            } catch (ActionCheckException e) {
                Assert.assertTrue(true);
            }
        }
    }

    private void studyAddContacts(List<Contact> contacts)
        throws ApplicationException {
        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        for (Contact c : contacts) {
            ClinicInfo clinicInfo =
                new ClinicInfo(c.getClinic(), 0L, 0L, Arrays.asList(c));
            studyInfo.clinicInfos.add(clinicInfo);
        }
        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        appService.doAction(studySave);
    }

    private void studyRemoveContacts(List<Contact> contactsToRemove)
        throws ApplicationException {
        // get a list of contact IDs to remove
        List<Integer> idsToRemove = new ArrayList<Integer>();
        for (Contact c : contactsToRemove) {
            idsToRemove.add(c.getId());
        }

        // get a list of current contact IDs
        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Set<Integer> studyContactIds = new HashSet<Integer>();
        for (ClinicInfo infos : studyInfo.clinicInfos) {
            for (Contact c : infos.getContacts()) {
                studyContactIds.add(c.getId());
            }
        }
        studyContactIds.removeAll(idsToRemove);

        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        studySave.setContactIds(studyContactIds);
        appService.doAction(studySave);
    }

    private Set<Contact> getStudyContacts(Integer studyId)
        throws ApplicationException {
        StudyInfo studyInfo = appService.doAction(new
            StudyGetInfoAction(studyId));
        Set<Contact> contacts = new HashSet<Contact>();
        for (ClinicInfo clinicInfo : studyInfo.clinicInfos) {
            contacts.addAll(clinicInfo.getContacts());
        }
        return contacts;
    }

    private List<SpecimenType> getSpecimenTypes() {
        openHibernateSession();
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        closeHibernateSession();
        Assert.assertTrue("specimen types not found in database",
            !spcTypes.isEmpty());
        return spcTypes;
    }

    private static Set<Integer> addSourceSpecimens(Integer studyId,
        int numSourceSpecimens, List<SpecimenType> specimenType)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < numSourceSpecimens; ++i) {
            SourceSpecimenSaveAction srcSpcSaveAction =
                new SourceSpecimenSaveAction();
            srcSpcSaveAction.setNeedOriginalVolume(r.nextBoolean());
            srcSpcSaveAction.setStudyId(studyId);
            SpecimenType spcType =
                specimenType.get(r.nextInt(specimenType.size()));
            srcSpcSaveAction.setSpecimenTypeId(spcType.getId());
            result.add(appService.doAction(srcSpcSaveAction).getId());
        }
        return result;
    }

    private Long getSourceSpecimenCount(Integer studyId) {
        openHibernateSession();
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + SourceSpecimen.class.getName()
            + " ss WHERE ss.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        closeHibernateSession();
        return result;
    }

    private Set<Integer> getSourceSpecimenIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (SourceSpecimen srcSpc : studyInfo.sourceSpcs) {
            actualIds.add(srcSpc.getId());
        }
        return actualIds;
    }

    @Test
    public void sourceSpecimens() throws Exception {
        Set<Integer> idsAll =
            addSourceSpecimens(studyId, 10, getSpecimenTypes());
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        int count = 0;
        for (Integer id : idsAll) {
            if (count < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
            ++count;
        }

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(idsAll, getSourceSpecimenIds(studyInfo));

        // remove Set 2 from the study, Set 1 should be left
        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        studySave.setSourceSpcIds(set1);
        appService.doAction(studySave);

        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(set1, getSourceSpecimenIds(studyInfo));

        // remove all
        studySave = StudyHelper.getSaveAction(appService, studyInfo);
        studySave.setSourceSpcIds(new HashSet<Integer>());
        appService.doAction(studySave);

        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.sourceSpcs.isEmpty());

        // check that this study no longer has any source specimens
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    private static Set<Integer> addAliquotedSpecimens(Integer studyId,
        int numAliquotedSpecimens, List<SpecimenType> specimenTypes)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < numAliquotedSpecimens; ++i) {
            AliquotedSpecimenSaveAction aqSpcSaveAction =
                new AliquotedSpecimenSaveAction();
            aqSpcSaveAction.setQuantity(r.nextInt());
            aqSpcSaveAction.setVolume(r.nextDouble());
            aqSpcSaveAction.setStudyId(studyId);
            aqSpcSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE
                .getId());
            aqSpcSaveAction.setSpecimenTypeId(specimenTypes.get(
                r.nextInt(specimenTypes.size()))
                .getId());
            result.add(appService.doAction(aqSpcSaveAction).getId());
        }
        return result;

    }

    private Long getAliquotedSpecimenCount(Integer studyId) {
        openHibernateSession();
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + AliquotedSpecimen.class.getName()
            + " aspc WHERE aspc.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        closeHibernateSession();
        return result;
    }

    private Set<Integer> getAliquotedSpecimenIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (AliquotedSpecimen aqSpc : studyInfo.aliquotedSpcs) {
            actualIds.add(aqSpc.getId());
        }
        return actualIds;
    }

    @Test
    public void aliquotedSpecimens() throws Exception {
        Set<Integer> idsAll =
            addAliquotedSpecimens(studyId, 10, getSpecimenTypes());
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        int count = 0;
        for (Integer id : idsAll) {
            if (count < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
            count++;
        }

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(idsAll, getAliquotedSpecimenIds(studyInfo));

        // remove Set 2 from the study, Set 1 should be left
        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        studySave.setAliquotSpcIds(set1);
        appService.doAction(studySave);

        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(set1, getAliquotedSpecimenIds(studyInfo));

        // remove all
        studySave = StudyHelper.getSaveAction(appService, studyInfo);
        studySave.setAliquotSpcIds(new HashSet<Integer>());
        appService.doAction(studySave);

        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.aliquotedSpcs.isEmpty());

        // check that this study no longer has any aliquoted specimens
        Assert.assertTrue(getAliquotedSpecimenCount(studyId).equals(0L));
    }

    private List<GlobalEventAttr> getGlobalEvAttrs() {
        openHibernateSession();
        Query q = session.createQuery("FROM " + GlobalEventAttr.class.getName()
            + " gea INNER JOIN FETCH gea.eventAttrType");
        @SuppressWarnings("unchecked")
        List<GlobalEventAttr> result = q.list();
        closeHibernateSession();
        return result;
    }

    private Long getStudyEventAttrCount(Integer studyId) {
        // check that this study no longer has any study event attributes
        openHibernateSession();
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + StudyEventAttr.class.getName()
            + " sea WHERE sea.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        closeHibernateSession();
        return result;
    }

    private Set<Integer> addStudyEventAttrToStudy(Integer studyId)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();

        // add a study event attribute for each global event attribute
        for (GlobalEventAttr gEvAttr : getGlobalEvAttrs()) {
            StudyEventAttrSaveAction stEvAttrSave =
                new StudyEventAttrSaveAction();

            stEvAttrSave.setGlobalEventAttrId(gEvAttr.getId());
            stEvAttrSave.setRequired(r.nextBoolean());

            if (gEvAttr.getEventAttrType().getName().startsWith("select_")) {
                stEvAttrSave.setPermissible("a;b;c;d;e;f");
            }
            stEvAttrSave.setStudyId(studyId);
            stEvAttrSave.setActivityStatusId(ActivityStatusEnum.ACTIVE
                .getId());
            Integer id = appService.doAction(stEvAttrSave).getId();
            result.add(id);
        }
        return result;
    }

    @Test
    public void studyEventAttrs() throws Exception {
        Set<Integer> idsAll = addStudyEventAttrToStudy(studyId);

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(idsAll, getStudyEventAttrIds(studyInfo));

        // attempt to add 1 of each global type again -> should not be allowed
        for (GlobalEventAttr gEvAttr : getGlobalEvAttrs()) {
            StudyEventAttrSaveAction stEvAttrSave =
                new StudyEventAttrSaveAction();

            stEvAttrSave.setGlobalEventAttrId(gEvAttr.getId());
            stEvAttrSave.setRequired(r.nextBoolean());

            if (gEvAttr.getEventAttrType().getName().startsWith("select_")) {
                stEvAttrSave.setPermissible("a;b;c;d;e;f");
            }
            stEvAttrSave.setStudyId(studyId);
            stEvAttrSave.setActivityStatusId(ActivityStatusEnum.ACTIVE
                .getId());

            try {
                appService.doAction(stEvAttrSave);
                Assert
                    .fail("should not be allowed to add more than 1 global type");
            } catch (ApplicationException e) {
                Assert.assertTrue(true);
            }
        }

        // remove each study event attr
        Set<Integer> idsRemaining = new HashSet<Integer>(idsAll);
        for (Integer id : idsAll) {
            idsRemaining.remove(id);
            StudySaveAction studySave =
                StudyHelper.getSaveAction(appService, studyInfo);
            studySave.setStudyEventAttrIds(idsRemaining);
            appService.doAction(studySave);

            studyInfo = appService.doAction(new StudyGetInfoAction(studyId));
            Assert.assertEquals(idsRemaining, getStudyEventAttrIds(studyInfo));
        }

        // check that this study no longer has any study event attributes
        Assert.assertTrue(getStudyEventAttrCount(studyId).equals(0L));
    }

    private Set<Integer> getStudyEventAttrIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (StudyEventAttr aqSpc : studyInfo.studyEventAttrs) {
            actualIds.add(aqSpc.getId());
        }
        return actualIds;
    }

    @Ignore("needs implementation")
    @Test
    public void comments() {
        // TODO: requires implementation
    }

    @Test
    public void delete() throws ApplicationException {
        // delete a study with no patients and no other associations
        appService.doAction(new StudyDeleteAction(studyId));
    }

    @Test
    public void deleteWithPatients() throws ApplicationException {
        // add patients to study
        PatientSaveAction patientSaveAction =
            new PatientSaveAction(null, studyId, Utils.getRandomString(5, 10),
                Utils.getRandomDate());
        Integer patientId = appService.doAction(patientSaveAction).getId();

        try {
            appService.doAction(new StudyDeleteAction(studyId));
            Assert.fail(
                "should not be allowed to delete a study with patients");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }
        appService.doAction(new PatientDeleteAction(patientId));
        appService.doAction(new StudyDeleteAction(studyId));
    }

    @Test
    public void deleteWithContacts() throws ApplicationException {
        // add contact to study - should be allowed to delete
        //
        // there should be none for this study after the delete
        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Integer clinicId =
            ClinicHelper.createClinic(appService, name + "_clinic",
                ActivityStatusEnum.ACTIVE);
        ContactSaveAction contactSave = new ContactSaveAction();
        contactSave.setName(name + "_contact");
        contactSave.setClinicId(clinicId);
        Integer contactId = appService.doAction(contactSave).getId();
        StudySaveAction studySaveAction =
            StudyHelper.getSaveAction(appService, studyInfo);
        studySaveAction.setContactIds(new HashSet<Integer>(contactId));
        appService.doAction(studySaveAction);
        appService.doAction(new StudyDeleteAction(studyId));
    }

    @Test
    public void deleteWithSourceSpecimens() throws ApplicationException {
        // add source specimens to study
        addSourceSpecimens(studyId, 5, getSpecimenTypes());
        appService.doAction(new StudyDeleteAction(studyId));
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithAliquotedSpecimens() throws ApplicationException {
        // add source specimens to study
        //
        // there should be none for this study after the delete
        addAliquotedSpecimens(studyId, 5, getSpecimenTypes());
        appService.doAction(new StudyDeleteAction(studyId));
        Assert.assertTrue(getAliquotedSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithStudyEventAttrs() throws ApplicationException {
        // add study event attributes to study
        //
        // there should be none for this study after the delete
        addStudyEventAttrToStudy(studyId);
        appService.doAction(new StudyDeleteAction(studyId));
        Assert.assertTrue(getStudyEventAttrCount(studyId).equals(0L));

    }
}
