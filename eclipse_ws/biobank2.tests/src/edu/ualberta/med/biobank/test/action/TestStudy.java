package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetContactsAction;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenInfoAction;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.study.StudyDeleteAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.AliquotedSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.SourceSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;
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
    private StudySaveAction studySaveAction;

    private Map<String, GlobalEventAttr> globalEventAttrs = null;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        studySaveAction =
            StudyHelper.getSaveAction(name, name, ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void saveNew() throws Exception {
        // null name
        studySaveAction.setName(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert.fail("should not be allowed to add study with no name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // null short name
        studySaveAction.setName(name);
        studySaveAction.setNameShort(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with no short name");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction.setNameShort(name);
        studySaveAction.setActivityStatusId(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with no activity status");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE.getId());
        studySaveAction.setSiteIds(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction.setSiteIds(new HashSet<Integer>());
        studySaveAction.setContactIds(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction.setContactIds(new HashSet<Integer>());
        studySaveAction.setSourceSpecimenSaveInfo(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        studySaveAction.setAliquotSpecimenSaveInfo(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        studySaveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        studySaveAction.setStudyEventAttrSaveInfo(null);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        // success path
        studySaveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        EXECUTOR.exec(studySaveAction);
    }

    @Test
    public void checkGetAction() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                provisioning.patientIds.get(0), provisioning.clinicId);
        ArrayList<SpecimenInfo> sourceSpecs = EXECUTOR.exec(
            new CollectionEventGetSourceSpecimenInfoAction(ceventId))
            .getList();

        Set<SourceSpecimenSaveInfo> ssSaveInfosAll =
            getSourceSpecimens(R.nextInt(5) + 1, getSpecimenTypes());
        Set<AliquotedSpecimenSaveInfo> asSaveInfosAll =
            addAliquotedSpecimens(R.nextInt(5) + 1, getSpecimenTypes());
        Set<StudyEventAttrSaveInfo> studyEventAttrSaveInfos =
            getStudyEventAttrSaveInfosAll();

        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(provisioning.studyId));
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction.setSourceSpecimenSaveInfo(ssSaveInfosAll);
        studySaveAction.setAliquotSpecimenSaveInfo(asSaveInfosAll);
        studySaveAction.setStudyEventAttrSaveInfo(studyEventAttrSaveInfos);
        EXECUTOR.exec(studySaveAction);

        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(provisioning.studyId));

        Assert.assertEquals("Active", studyInfo.study.getActivityStatus()
            .getName());
        Assert.assertEquals(new Long(1), studyInfo.patientCount);
        Assert.assertEquals(new Long(1), studyInfo.collectionEventCount);
        Assert.assertEquals(ssSaveInfosAll.size(), studyInfo.sourceSpcs.size());
        Assert.assertEquals(asSaveInfosAll.size(),
            studyInfo.aliquotedSpcs.size());
        Assert.assertEquals(studyEventAttrSaveInfos.size(),
            studyInfo.studyEventAttrs.size());
    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing study
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        studyInfo.study.setName(name + "_2");
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        EXECUTOR.exec(studySaveAction);

        // ensure we can change short name on existing study
        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(studyId));
        studyInfo.study.setNameShort(name + "_2");
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        EXECUTOR.exec(studySaveAction);

        // test for duplicate name
        StudySaveAction saveStudy =
            StudyHelper.getSaveAction(name + "_2", name,
                ActivityStatusEnum.ACTIVE);
        try {
            EXECUTOR.exec(saveStudy);
            Assert.fail("should not be allowed to add study with same name");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveStudy.setName(Utils.getRandomString(5, 10));
        saveStudy.setNameShort(name + "_2");

        try {
            EXECUTOR.exec(saveStudy);
            Assert
                .fail("should not be allowed to add study with same name short");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void contactCollection() throws Exception {
        int numClinics = R.nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds =
            ClinicHelper.createClinicsWithContacts(EXECUTOR,
                name + "_clinic", numClinics, numContacts);

        List<Contact> studyContactsSet1 = new ArrayList<Contact>();
        List<Contact> studyContactsSet2 = new ArrayList<Contact>();
        Set<Contact> expectedStudyContacts = new HashSet<Contact>();

        // get a contact id from each clinic
        for (Integer clinicId : clinicIds) {
            List<Contact> contacts =
                EXECUTOR.exec(new ClinicGetContactsAction(clinicId))
                    .getList();
            Assert.assertNotNull(contacts);
            Assert.assertNotNull(contacts.get(0));
            Assert.assertNotNull(contacts.get(1));
            studyContactsSet1.add(contacts.get(0));
            studyContactsSet2.add(contacts.get(1));
        }

        // add a contact one by one from set 1
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        for (Contact c : studyContactsSet1) {
            expectedStudyContacts.add(c);
            studyAddContacts(studyId, Arrays.asList(c));
            Assert.assertEquals(expectedStudyContacts,
                getStudyContacts(studyId));
        }

        // add contact set 2
        studyAddContacts(studyId, studyContactsSet2);
        expectedStudyContacts.addAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts,
            getStudyContacts(studyId));

        // remove all contacts from set 1 individually
        for (Contact c : studyContactsSet1) {
            expectedStudyContacts.remove(c);
            studyRemoveContacts(studyId, Arrays.asList(c));
            Assert.assertEquals(expectedStudyContacts,
                getStudyContacts(studyId));
        }

        // remove contact set 2
        studyRemoveContacts(studyId, studyContactsSet2);
        expectedStudyContacts.removeAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts,
            getStudyContacts(studyId));
        Assert.assertTrue(getStudyContacts(studyId).isEmpty());

        // test removing clinics - should fail
        studyAddContacts(studyId, studyContactsSet2);
        expectedStudyContacts.addAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts,
            getStudyContacts(studyId));

        for (Contact c : studyContactsSet2) {
            try {
                EXECUTOR.exec(new ClinicDeleteAction(c.getClinic()
                    .getId()));
                Assert
                    .fail("should not be allowed to delete a clinic with contact linked to study");
            } catch (ActionCheckException e) {
                Assert.assertTrue(true);
            }
        }

        // attempt to add an invalid contact ID
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        StudySaveAction studySave =
            StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySave.setContactIds(new HashSet<Integer>(Arrays.asList(-1)));
        try {
            EXECUTOR.exec(studySave);
            Assert.fail(
                "should not be allowed to add an invalid contact ID");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    private void studyAddContacts(Integer studyId, List<Contact> contacts)
        throws ApplicationException {
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        for (Contact c : contacts) {
            ClinicInfo clinicInfo =
                new ClinicInfo(c.getClinic(), 0L, 0L, Arrays.asList(c));
            studyInfo.clinicInfos.add(clinicInfo);
        }
        StudySaveAction studySave =
            StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        EXECUTOR.exec(studySave);
    }

    private void studyRemoveContacts(Integer studyId,
        List<Contact> contactsToRemove) throws ApplicationException {
        // get a list of contact IDs to remove
        List<Integer> idsToRemove = new ArrayList<Integer>();
        for (Contact c : contactsToRemove) {
            idsToRemove.add(c.getId());
        }

        // get a list of current contact IDs
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Set<Integer> studyContactIds = new HashSet<Integer>();
        for (ClinicInfo infos : studyInfo.clinicInfos) {
            for (Contact c : infos.getContacts()) {
                studyContactIds.add(c.getId());
            }
        }
        studyContactIds.removeAll(idsToRemove);

        StudySaveAction studySave =
            StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySave.setContactIds(studyContactIds);
        EXECUTOR.exec(studySave);
    }

    private Set<Contact> getStudyContacts(Integer studyId)
        throws ApplicationException {
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Set<Contact> contacts = new HashSet<Contact>();
        for (ClinicInfo clinicInfo : studyInfo.clinicInfos) {
            contacts.addAll(clinicInfo.getContacts());
        }
        return contacts;
    }

    private static Set<SourceSpecimenSaveInfo> getSourceSpecimens(
        int numSourceSpecimens, List<SpecimenType> specimenType)
        throws ApplicationException {
        Set<SourceSpecimenSaveInfo> result =
            new HashSet<SourceSpecimenSaveInfo>();
        for (int i = 0; i < numSourceSpecimens; ++i) {
            SourceSpecimenSaveInfo ssSaveInfo = new SourceSpecimenSaveInfo();
            ssSaveInfo.id = null;
            ssSaveInfo.needOriginalVolume = R.nextBoolean();
            SpecimenType spcType =
                specimenType.get(R.nextInt(specimenType.size()));
            ssSaveInfo.specimenTypeId = spcType.getId();
            result.add(ssSaveInfo);
        }
        return result;
    }

    private Long getSourceSpecimenCount(Integer studyId) {
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + SourceSpecimen.class.getName() + " ss WHERE ss.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        return result;
    }

    private Set<Integer> getSourceSpecimenSpecimenTypesFromSaveInfo(
        Collection<SourceSpecimenSaveInfo> sourceSpecimenSaveInfos) {
        Set<Integer> result = new HashSet<Integer>();
        for (SourceSpecimenSaveInfo ssSaveInfo : sourceSpecimenSaveInfos) {
            result.add(ssSaveInfo.specimenTypeId);
        }
        return result;
    }

    private Set<Integer> getSourceSpecimenSpecimenTypes(
        List<SourceSpecimen> sourceSpecimens) {
        Set<Integer> result = new HashSet<Integer>();
        for (SourceSpecimen ss : sourceSpecimens) {
            result.add(ss.getSpecimenType().getId());
        }
        return result;
    }

    @Test
    public void sourceSpecimens() throws Exception {
        Set<SourceSpecimenSaveInfo> ssSaveInfosAll =
            getSourceSpecimens(10, getSpecimenTypes());

        Set<SourceSpecimenSaveInfo> set1 =
            new HashSet<SourceSpecimenSaveInfo>();
        Set<SourceSpecimenSaveInfo> set2 =
            new HashSet<SourceSpecimenSaveInfo>();

        int count = 0;
        for (SourceSpecimenSaveInfo ssSaveInfo : ssSaveInfosAll) {
            if (count < 5) {
                set1.add(ssSaveInfo);
            } else {
                set2.add(ssSaveInfo);
            }
            ++count;
        }

        studySaveAction.setSourceSpecimenSaveInfo(ssSaveInfosAll);
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getSourceSpecimenSpecimenTypesFromSaveInfo(ssSaveInfosAll),
            getSourceSpecimenSpecimenTypes(studyInfo.sourceSpcs));

        // remove Set 2 from the study, Set 1 should be left
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction.setSourceSpecimenSaveInfo(set1);
        EXECUTOR.exec(studySaveAction);

        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getSourceSpecimenSpecimenTypesFromSaveInfo(set1),
            getSourceSpecimenSpecimenTypes(studyInfo.sourceSpcs));

        // remove all
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        EXECUTOR.exec(studySaveAction);

        studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.sourceSpcs.isEmpty());

        // check that this study no longer has any source specimens
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    private static Set<AliquotedSpecimenSaveInfo> addAliquotedSpecimens(
        int numAliquotedSpecimens, List<SpecimenType> specimenTypes)
        throws ApplicationException {
        Set<AliquotedSpecimenSaveInfo> result =
            new HashSet<AliquotedSpecimenSaveInfo>();
        for (int i = 0; i < numAliquotedSpecimens; ++i) {
            AliquotedSpecimenSaveInfo asSaveInfo =
                new AliquotedSpecimenSaveInfo();
            asSaveInfo.id = null;
            asSaveInfo.quantity = R.nextInt();
            asSaveInfo.volume = R.nextDouble();
            asSaveInfo.aStatusId = ActivityStatusEnum.ACTIVE.getId();
            asSaveInfo.specimenTypeId =
                specimenTypes.get(R.nextInt(specimenTypes.size())).getId();
            result.add(asSaveInfo);
        }
        return result;

    }

    private Long getAliquotedSpecimenCount(Integer studyId) {
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + AliquotedSpecimen.class.getName()
                + " aspc WHERE aspc.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        return result;
    }

    private Set<Integer> getAliquotedSpecimenSpecimenTypesFromSaveInfo(
        Collection<AliquotedSpecimenSaveInfo> aliquotedSpecimenSaveInfos) {
        Set<Integer> result = new HashSet<Integer>();
        for (AliquotedSpecimenSaveInfo asSaveInfo : aliquotedSpecimenSaveInfos) {
            result.add(asSaveInfo.specimenTypeId);
        }
        return result;
    }

    private Set<Integer> getAliquotedSpecimenSpecimenTypes(
        Collection<AliquotedSpecimen> aliquotedSpecimens) {
        Set<Integer> result = new HashSet<Integer>();
        for (AliquotedSpecimen as : aliquotedSpecimens) {
            result.add(as.getSpecimenType().getId());
        }
        return result;
    }

    @Test
    public void aliquotedSpecimens() throws Exception {
        Set<AliquotedSpecimenSaveInfo> asSaveInfosAll =
            addAliquotedSpecimens(10, getSpecimenTypes());
        Set<AliquotedSpecimenSaveInfo> set1 =
            new HashSet<AliquotedSpecimenSaveInfo>();
        Set<AliquotedSpecimenSaveInfo> set2 =
            new HashSet<AliquotedSpecimenSaveInfo>();

        int count = 0;
        for (AliquotedSpecimenSaveInfo id : asSaveInfosAll) {
            if (count < 5) {
                set1.add(id);
            } else {
                set2.add(id);
            }
            count++;
        }

        studySaveAction.setAliquotSpecimenSaveInfo(asSaveInfosAll);
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert
            .assertEquals(
                getAliquotedSpecimenSpecimenTypesFromSaveInfo(asSaveInfosAll),
                getAliquotedSpecimenSpecimenTypes(studyInfo.aliquotedSpcs));

        // remove Set 2 from the study, Set 1 should be left
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction.setAliquotSpecimenSaveInfo(set1);
        EXECUTOR.exec(studySaveAction);

        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getAliquotedSpecimenSpecimenTypesFromSaveInfo(set1),
            getAliquotedSpecimenSpecimenTypes(studyInfo.aliquotedSpcs));

        // remove all
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        EXECUTOR.exec(studySaveAction);

        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.aliquotedSpcs.isEmpty());

        // check that this study no longer has any aliquoted specimens
        Assert.assertTrue(getAliquotedSpecimenCount(studyId).equals(0L));
    }

    private Long getStudyEventAttrCount(Integer studyId) {
        // check that this study no longer has any study event attributes
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + StudyEventAttr.class.getName() + " sea WHERE sea.study.id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        return result;
    }

    private Map<String, GlobalEventAttr> getGlobalEvAttrs() {
        if (globalEventAttrs == null) {
            Query q =
                session.createQuery("FROM " + GlobalEventAttr.class.getName()
                    + " gea INNER JOIN FETCH gea.eventAttrType");
            globalEventAttrs = new HashMap<String, GlobalEventAttr>();
            for (Object o : q.list()) {
                GlobalEventAttr geAttr = (GlobalEventAttr) o;
                globalEventAttrs.put(geAttr.getLabel(), geAttr);
            }
        }
        return globalEventAttrs;
    }

    private Set<StudyEventAttrSaveInfo> getStudyEventAttrSaveInfosAll()
        throws ApplicationException {
        HashSet<StudyEventAttrSaveInfo> result =
            new HashSet<StudyEventAttrSaveInfo>();

        for (GlobalEventAttr gEvAttr : getGlobalEvAttrs().values()) {
            StudyEventAttrSaveInfo seAttrSave = new StudyEventAttrSaveInfo();

            seAttrSave.id = null;
            seAttrSave.globalEventAttrId = gEvAttr.getId();
            seAttrSave.required = R.nextBoolean();

            if (gEvAttr.getEventAttrType().getName().startsWith("select_")) {
                seAttrSave.permissible = "a;b;c;d;e;f";
            }
            seAttrSave.aStatusId = ActivityStatusEnum.ACTIVE.getId();
            result.add(seAttrSave);
        }
        return result;
    }

    @Test
    public void studyEventAttrs() throws Exception {
        // add a new study and add all global event attributes to study
        Set<StudyEventAttrSaveInfo> studyEventAttrSaveInfos =
            getStudyEventAttrSaveInfosAll();
        studySaveAction.setStudyEventAttrSaveInfo(studyEventAttrSaveInfos);
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();

        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getStudyEventAttrSaveInfoGlobalIds(studyEventAttrSaveInfos),
            getStudyEventAttrGlobalIds(studyInfo.studyEventAttrs));

        // attempt to add 1 of each global type again -> should not be allowed
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studyEventAttrSaveInfos.addAll(getStudyEventAttrSaveInfosAll());
        studySaveAction.setStudyEventAttrSaveInfo(studyEventAttrSaveInfos);
        try {
            EXECUTOR.exec(studySaveAction);
            Assert.fail("should not be allowed to add more than 1 global type");
        } catch (ActionCheckException e) {
            Assert.assertTrue(true);
        }

        // remove each study event attr
        studySaveAction = StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        EXECUTOR.exec(studySaveAction);

        studyInfo = EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.studyEventAttrs.isEmpty());

        // check that this study no longer has any study event attributes
        Assert.assertTrue(getStudyEventAttrCount(studyId).equals(0L));
    }

    private Set<Integer> getStudyEventAttrSaveInfoGlobalIds(
        Collection<StudyEventAttrSaveInfo> studyEventAttrSaveInfos) {
        Set<Integer> result = new HashSet<Integer>();
        for (StudyEventAttrSaveInfo seAttrSaveInfo : studyEventAttrSaveInfos) {
            result.add(seAttrSaveInfo.globalEventAttrId);
        }
        return result;
    }

    private Set<Integer> getStudyEventAttrGlobalIds(
        Collection<StudyEventAttr> studyEventAttrs) {
        Set<Integer> result = new HashSet<Integer>();
        for (StudyEventAttr seAttr : studyEventAttrs) {
            result.add(seAttr.getGlobalEventAttr().getId());
        }
        return result;
    }

    @Ignore("needs implementation")
    @Test
    public void comments() {
        // TODO: requires implementation
    }

    @Test
    public void delete() throws ApplicationException {
        // delete a study with no patients and no other associations
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        EXECUTOR.exec(new StudyDeleteAction(studyId));

        // hql query for study should return empty
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + Study.class.getName() + " WHERE id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        Assert.assertTrue(result.equals(0L));
    }

    @Test
    public void deleteWithPatients() throws ApplicationException {
        // add patients to study
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        PatientSaveAction patientSaveAction =
            new PatientSaveAction(null, studyId, Utils.getRandomString(
                5, 10),
                Utils.getRandomDate());
        Integer patientId = EXECUTOR.exec(patientSaveAction).getId();

        try {
            EXECUTOR.exec(new StudyDeleteAction(studyId));
            Assert
                .fail("should not be allowed to delete a study with patients");
        } catch (CollectionNotEmptyException e) {
            Assert.assertTrue(true);
        }
        EXECUTOR.exec(new PatientDeleteAction(patientId));
        EXECUTOR.exec(new StudyDeleteAction(studyId));
    }

    @Test
    public void deleteWithContacts() throws ApplicationException {
        // add contact to study - should be allowed to delete
        //
        // there should be none for this study after the delete
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        Integer clinicId =
            ClinicHelper.createClinic(EXECUTOR, name + "_clinic",
                ActivityStatusEnum.ACTIVE);
        ContactSaveAction contactSave = new ContactSaveAction();
        contactSave.setName(name + "_contact");
        contactSave.setClinicId(clinicId);
        Integer contactId = EXECUTOR.exec(contactSave).getId();
        StudySaveAction studySaveAction =
            StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction.setContactIds(new HashSet<Integer>(contactId));
        EXECUTOR.exec(studySaveAction);
        EXECUTOR.exec(new StudyDeleteAction(studyId));
    }

    @Test
    public void deleteWithSourceSpecimens() throws ApplicationException {
        // add source specimens to study
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        getSourceSpecimens(5, getSpecimenTypes());
        EXECUTOR.exec(new StudyDeleteAction(studyId));
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithAliquotedSpecimens() throws ApplicationException {
        // add source specimens to study
        //
        // there should be none for this study after the delete
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        addAliquotedSpecimens(5, getSpecimenTypes());
        EXECUTOR.exec(new StudyDeleteAction(studyId));
        Assert.assertTrue(getAliquotedSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithStudyEventAttrs() throws ApplicationException {
        // add study event attributes to study
        //
        // there should be none for this study after the delete
        studySaveAction
            .setStudyEventAttrSaveInfo(getStudyEventAttrSaveInfosAll());
        Integer studyId = EXECUTOR.exec(studySaveAction).getId();
        EXECUTOR.exec(new StudyDeleteAction(studyId));
        Assert.assertTrue(getStudyEventAttrCount(studyId).equals(0L));

    }
}
