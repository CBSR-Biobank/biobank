package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
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
import edu.ualberta.med.biobank.common.action.aliquotedspecimen.AliquotedSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetContactsAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
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
    public void testNameChecks() throws Exception {
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
    public void testContactCollection() throws Exception {
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

    @Test
    public void testSourceSpecimens() throws Exception {
        openHibernateSession();
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        closeHibernateSession();
        Assert.assertTrue("specimen types not found in database",
            !spcTypes.isEmpty());

        Set<Integer> idsAll = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < 10; ++i) {
            SourceSpecimenSaveAction srcSpcSaveAction =
                new SourceSpecimenSaveAction();
            srcSpcSaveAction.setNeedOriginalVolume(r.nextBoolean());
            srcSpcSaveAction.setStudyId(studyId);
            SpecimenType spcType = spcTypes.get(r.nextInt(spcTypes.size()));
            srcSpcSaveAction.setSpecimenTypeId(spcType.getId());
            Integer id = appService.doAction(srcSpcSaveAction).getId();

            idsAll.add(id);
            if (i < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
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
        openHibernateSession();
        q = session.createQuery("SELECT COUNT(*) FROM "
            + SourceSpecimen.class.getName()
            + " ss WHERE ss.study.id=?");
        q.setParameter(0, studyId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
        closeHibernateSession();
    }

    private Set<Integer> getSourceSpecimenIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (SourceSpecimen srcSpc : studyInfo.sourceSpcs) {
            actualIds.add(srcSpc.getId());
        }
        return actualIds;
    }

    @Test
    public void testAliquotedSpecimens() throws Exception {
        openHibernateSession();
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        closeHibernateSession();

        Set<Integer> idsAll = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < 10; ++i) {
            AliquotedSpecimenSaveAction aqSpcSaveAction =
                new AliquotedSpecimenSaveAction();
            aqSpcSaveAction.setQuantity(r.nextInt());
            aqSpcSaveAction.setVolume(r.nextDouble());
            aqSpcSaveAction.setStudyId(studyId);
            aqSpcSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE
                .getId());
            aqSpcSaveAction.setSpecimenTypeId(spcTypes.get(
                r.nextInt(spcTypes.size()))
                .getId());
            Integer id = appService.doAction(aqSpcSaveAction).getId();

            idsAll.add(id);
            if (i < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
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
        openHibernateSession();
        q = session.createQuery("SELECT COUNT(*) FROM "
            + AliquotedSpecimen.class.getName()
            + " aspc WHERE aspc.study.id=?");
        q.setParameter(0, studyId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
        closeHibernateSession();
    }

    private Set<Integer> getAliquotedSpecimenIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (AliquotedSpecimen aqSpc : studyInfo.aliquotedSpcs) {
            actualIds.add(aqSpc.getId());
        }
        return actualIds;
    }

    @Test
    public void testStudyEventAttrs() throws Exception {
        openHibernateSession();
        Query q = session.createQuery("FROM " + GlobalEventAttr.class.getName()
            + " gea INNER JOIN FETCH gea.eventAttrType");
        @SuppressWarnings("unchecked")
        List<GlobalEventAttr> globalEvAttrs = q.list();
        closeHibernateSession();

        Set<Integer> idsAll = new HashSet<Integer>();

        // add a study event attribute for each global event attribute
        for (GlobalEventAttr gEvAttr : globalEvAttrs) {
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
            idsAll.add(id);
        }

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Assert.assertEquals(idsAll, getStudyEventAttrIds(studyInfo));

        // attempt to add 1 of each global type again - should not be allowed
        for (GlobalEventAttr gEvAttr : globalEvAttrs) {
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
        openHibernateSession();
        q = session.createQuery("SELECT COUNT(*) FROM "
            + StudyEventAttr.class.getName()
            + " sea WHERE sea.study.id=?");
        q.setParameter(0, studyId);
        Assert.assertTrue(HibernateUtil.getCountFromQuery(q).equals(0L));
        closeHibernateSession();
    }

    private Set<Integer> getStudyEventAttrIds(StudyInfo studyInfo) {
        Set<Integer> actualIds = new HashSet<Integer>();
        for (StudyEventAttr aqSpc : studyInfo.studyEventAttrs) {
            actualIds.add(aqSpc.getId());
        }
        return actualIds;
    }

    @Test
    public void testDelete() throws ApplicationException {
        // delete a study with no patients and no other associations
        appService.doAction(new StudyDeleteAction(studyId));

        // add patients to study
        studyId = StudyHelper.createStudy(appService, name,
            ActivityStatusEnum.ACTIVE);
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

        // add contacts to study
        Integer clinicId =
            ClinicHelper.createClinic(appService, name + "_clinic",
                ActivityStatusEnum.ACTIVE);

        // add source specimens to study
        studyId = StudyHelper.createStudy(appService, name,
            ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void testComments() {

    }
}
