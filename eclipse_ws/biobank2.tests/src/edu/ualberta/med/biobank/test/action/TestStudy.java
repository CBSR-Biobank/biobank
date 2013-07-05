package edu.ualberta.med.biobank.test.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetContactsAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyDeleteAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.AliquotedSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.SourceSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

/**
 * This test suite assumes that the association between Sites and Studies is tested in the Site test
 * class.
 * 
 */
public class TestStudy extends TestAction {

    private String name;
    private StudySaveAction studySaveAction;

    private Map<String, GlobalEventAttr> globalEventAttrs = null;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        studySaveAction =
            StudyHelper.getSaveAction(name, name, ActivityStatus.ACTIVE);
    }

    @Test
    public void saveNew() throws Exception {
        studySaveAction.setName(name);
        studySaveAction.setNameShort(name);
        studySaveAction.setActivityStatus(ActivityStatus.ACTIVE);

        studySaveAction.setContactIds(null);
        try {
            exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }

        studySaveAction.setContactIds(new HashSet<Integer>());
        studySaveAction.setSourceSpecimenSaveInfo(null);
        try {
            exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }

        studySaveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        studySaveAction.setAliquotSpecimenSaveInfo(null);
        try {
            exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }

        studySaveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        studySaveAction.setStudyEventAttrSaveInfo(null);
        try {
            exec(studySaveAction);
            Assert
                .fail("should not be allowed to add study with null site ids");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }

        // success path
        studySaveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        exec(studySaveAction);
    }

    @Test
    public void checkGetAction() throws Exception {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        CollectionEventHelper.createCEventWithSourceSpecimens(getExecutor(),
            provisioning.patientIds.get(0), provisioning.getClinic());

        Set<SourceSpecimenSaveInfo> ssSaveInfosAll =
            getSourceSpecimens(getR().nextInt(5) + 1, getSpecimenTypes());
        Set<AliquotedSpecimenSaveInfo> asSaveInfosAll =
            addAliquotedSpecimens(getR().nextInt(5) + 1, getSpecimenTypes());
        Set<StudyEventAttrSaveInfo> studyEventAttrSaveInfos =
            getStudyEventAttrSaveInfosAll();

        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(provisioning.studyId));
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction.setSourceSpecimenSaveInfo(ssSaveInfosAll);
        studySaveAction.setAliquotSpecimenSaveInfo(asSaveInfosAll);
        studySaveAction.setStudyEventAttrSaveInfo(studyEventAttrSaveInfos);
        exec(studySaveAction);

        studyInfo = exec(new StudyGetInfoAction(provisioning.studyId));

        Assert.assertEquals(ActivityStatus.ACTIVE,
            studyInfo.getStudy().getActivityStatus());
        Assert.assertEquals(new Long(1), studyInfo.getPatientCount());
        Assert.assertEquals(new Long(1), studyInfo.getCollectionEventCount());
        Assert.assertEquals(ssSaveInfosAll.size(), studyInfo
            .getSourceSpecimens().size());
        Assert.assertEquals(asSaveInfosAll.size(),
            studyInfo.getAliquotedSpcs().size());
        Assert.assertEquals(studyEventAttrSaveInfos.size(),
            studyInfo.getStudyEventAttrs().size());
    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing study
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        studyInfo.getStudy().setName(name + "_2");
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        exec(studySaveAction);

        // ensure we can change short name on existing study
        studyInfo = exec(new StudyGetInfoAction(studyId));
        studyInfo.getStudy().setNameShort(name + "_2");
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        exec(studySaveAction);
    }

    @Test
    public void contactCollection() throws Exception {
        int numClinics = getR().nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds =
            ClinicHelper.createClinicsWithContacts(getExecutor(),
                name + "_clinic", numClinics, numContacts);

        List<Integer> studyContactIdsSet1 = new ArrayList<Integer>();
        List<Integer> studyContactIdsSet2 = new ArrayList<Integer>();
        Set<Integer> expectedStudyContactIds = new HashSet<Integer>();

        // get a contact id from each clinic
        for (Integer clinicId : clinicIds) {
            List<Contact> contacts =
                exec(new ClinicGetContactsAction(clinicId))
                    .getList();
            Assert.assertNotNull(contacts);
            Assert.assertNotNull(contacts.get(0));
            Assert.assertNotNull(contacts.get(1));
            studyContactIdsSet1.add(contacts.get(0).getId());
            studyContactIdsSet2.add(contacts.get(1).getId());
        }

        // add a contact one by one from set 1
        Integer studyId = exec(studySaveAction).getId();
        for (Integer id : studyContactIdsSet1) {
            expectedStudyContactIds.add(id);
            studyAddContacts(studyId, Arrays.asList(id));
            Assert.assertEquals(expectedStudyContactIds,
                getStudyContactIds(studyId));
        }

        // add contact set 2
        studyAddContacts(studyId, studyContactIdsSet2);
        expectedStudyContactIds.addAll(studyContactIdsSet2);
        Assert.assertEquals(expectedStudyContactIds,
            getStudyContactIds(studyId));

        // remove all contacts from set 1 individually
        for (Integer id : studyContactIdsSet1) {
            expectedStudyContactIds.remove(id);
            studyRemoveContacts(studyId, Arrays.asList(id));
            Assert.assertEquals(expectedStudyContactIds,
                getStudyContactIds(studyId));
        }

        // remove contact set 2
        studyRemoveContacts(studyId, studyContactIdsSet2);
        expectedStudyContactIds.removeAll(studyContactIdsSet2);
        Assert.assertEquals(expectedStudyContactIds,
            getStudyContactIds(studyId));
        Assert.assertTrue(getStudyContactIds(studyId).isEmpty());

        // test removing clinics - should fail
        studyAddContacts(studyId, studyContactIdsSet2);
        expectedStudyContactIds.addAll(studyContactIdsSet2);
        Assert.assertEquals(expectedStudyContactIds,
            getStudyContactIds(studyId));

        for (Integer clinicId : clinicIds) {
            ClinicGetInfoAction.ClinicInfo clinicInfo =
                exec(new ClinicGetInfoAction(clinicId));
            try {
                exec(new ClinicDeleteAction(clinicInfo.clinic));
                Assert
                    .fail("should not be allowed to delete a clinic with contact linked to study");
            } catch (ConstraintViolationException e) {
                Assert.assertTrue(true);
            }
        }

        // attempt to add an invalid contact ID
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        StudySaveAction studySave =
            StudyHelper.getSaveAction(studyInfo);
        studySave.setContactIds(new HashSet<Integer>(Arrays.asList(-1)));
        try {
            exec(studySave);
            Assert.fail(
                "should not be allowed to add an invalid contact ID");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    private void studyAddContacts(Integer studyId, List<Integer> newContactIds) {
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        Set<Integer> contactIds = new HashSet<Integer>();
        for (ClinicInfo clinicInfo : studyInfo.getClinicInfos()) {
            for (Contact c : clinicInfo.getContacts()) {
                contactIds.add(c.getId());
            }
        }
        contactIds.addAll(newContactIds);

        StudySaveAction studySave = StudyHelper.getSaveAction(studyInfo);
        studySave.setContactIds(contactIds);
        exec(studySave);
    }

    private void studyRemoveContacts(Integer studyId,
        List<Integer> contactIdsToRemove) {
        // get a list of current contact IDs
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        Set<Integer> studyContactIds = new HashSet<Integer>();
        for (ClinicInfo infos : studyInfo.getClinicInfos()) {
            for (Contact c : infos.getContacts()) {
                studyContactIds.add(c.getId());
            }
        }
        studyContactIds.removeAll(contactIdsToRemove);

        StudySaveAction studySave =
            StudyHelper.getSaveAction(studyInfo);
        studySave.setContactIds(studyContactIds);
        exec(studySave);
    }

    private Set<Integer> getStudyContactIds(Integer studyId) {
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        Set<Integer> contactIds = new HashSet<Integer>();
        for (ClinicInfo clinicInfo : studyInfo.getClinicInfos()) {
            for (Contact c : clinicInfo.getContacts()) {
                contactIds.add(c.getId());
            }
        }
        return contactIds;
    }

    private static Set<SourceSpecimenSaveInfo> getSourceSpecimens(
        int numSourceSpecimens, List<SpecimenType> specimenType) {
        Set<SourceSpecimenSaveInfo> result =
            new HashSet<SourceSpecimenSaveInfo>();
        for (int i = 0; i < numSourceSpecimens; ++i) {
            SourceSpecimenSaveInfo ssSaveInfo = new SourceSpecimenSaveInfo();
            ssSaveInfo.id = null;
            ssSaveInfo.needOriginalVolume = getR().nextBoolean();
            SpecimenType spcType =
                specimenType.get(getR().nextInt(specimenType.size()));
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
        Set<SourceSpecimen> set) {
        Set<Integer> result = new HashSet<Integer>();
        for (SourceSpecimen ss : set) {
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
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getSourceSpecimenSpecimenTypesFromSaveInfo(ssSaveInfosAll),
            getSourceSpecimenSpecimenTypes(studyInfo.getSourceSpecimens()));

        // remove Set 2 from the study, Set 1 should be left
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction.setSourceSpecimenSaveInfo(set1);
        exec(studySaveAction);

        studyInfo = exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getSourceSpecimenSpecimenTypesFromSaveInfo(set1),
            getSourceSpecimenSpecimenTypes(studyInfo.getSourceSpecimens()));

        // remove all
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        exec(studySaveAction);

        studyInfo =
            exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.getSourceSpecimens().isEmpty());

        // check that this study no longer has any source specimens
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    private static Set<AliquotedSpecimenSaveInfo> addAliquotedSpecimens(
        int numAliquotedSpecimens, List<SpecimenType> specimenTypes) {
        Set<AliquotedSpecimenSaveInfo> result =
            new HashSet<AliquotedSpecimenSaveInfo>();
        for (int i = 0; i < numAliquotedSpecimens; ++i) {
            AliquotedSpecimenSaveInfo asSaveInfo =
                new AliquotedSpecimenSaveInfo();
            asSaveInfo.id = null;
            asSaveInfo.quantity = getR().nextInt();
            asSaveInfo.volume = new BigDecimal(getR().nextInt(10) + 1);
            asSaveInfo.activityStatus = ActivityStatus.ACTIVE;
            asSaveInfo.specimenTypeId =
                specimenTypes.get(getR().nextInt(specimenTypes.size())).getId();
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
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        Assert
            .assertEquals(
                getAliquotedSpecimenSpecimenTypesFromSaveInfo(asSaveInfosAll),
                getAliquotedSpecimenSpecimenTypes(studyInfo.getAliquotedSpcs()));

        // remove Set 2 from the study, Set 1 should be left
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction.setAliquotSpecimenSaveInfo(set1);
        exec(studySaveAction);

        studyInfo = exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getAliquotedSpecimenSpecimenTypesFromSaveInfo(set1),
            getAliquotedSpecimenSpecimenTypes(studyInfo.getAliquotedSpcs()));

        // remove all
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        exec(studySaveAction);

        studyInfo = exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.getAliquotedSpcs().isEmpty());

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

    private Set<StudyEventAttrSaveInfo> getStudyEventAttrSaveInfosAll() {
        HashSet<StudyEventAttrSaveInfo> result =
            new HashSet<StudyEventAttrSaveInfo>();

        for (GlobalEventAttr gEvAttr : getGlobalEvAttrs().values()) {
            StudyEventAttrSaveInfo seAttrSave = new StudyEventAttrSaveInfo();

            seAttrSave.id = null;
            seAttrSave.globalEventAttrId = gEvAttr.getId();
            seAttrSave.required = getR().nextBoolean();

            if (gEvAttr.getEventAttrType().getName().startsWith("select_")) {
                seAttrSave.permissible = "a;b;c;d;e;f";
            }
            seAttrSave.activityStatus = ActivityStatus.ACTIVE;
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
        Integer studyId = exec(studySaveAction).getId();

        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(
            getStudyEventAttrSaveInfoGlobalIds(studyEventAttrSaveInfos),
            getStudyEventAttrGlobalIds(studyInfo.getStudyEventAttrs()));

        // attempt to add 1 of each global type again -> should not be allowed
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studyEventAttrSaveInfos.addAll(getStudyEventAttrSaveInfosAll());
        studySaveAction.setStudyEventAttrSaveInfo(studyEventAttrSaveInfos);
        try {
            exec(studySaveAction);
            Assert.fail("should not be allowed to add more than 1 global type");
        } catch (ActionException e) {
            Assert.assertTrue(true);
        }

        // remove each study event attr
        studySaveAction = StudyHelper.getSaveAction(studyInfo);
        studySaveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        exec(studySaveAction);

        studyInfo = exec(new StudyGetInfoAction(studyId));
        Assert.assertTrue(studyInfo.getStudyEventAttrs().isEmpty());

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

    @Test
    public void comments() {
        // save with no comments
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        Assert.assertEquals(0, studyInfo.getStudy().getComments().size());

        studyInfo = addComment(studyId);
        Assert.assertEquals(1, studyInfo.getStudy().getComments().size());

        studyInfo = addComment(studyId);
        Assert.assertEquals(2, studyInfo.getStudy().getComments().size());

        // TODO: check full name on each comment's user
        // for (Comment comment : studyInfo.study.getCommentCollection()) {
        //
        // }
    }

    private StudyInfo addComment(Integer studyId) {
        StudySaveAction studySaveAction = StudyHelper.getSaveAction(
            exec(new StudyGetInfoAction(studyId)));
        studySaveAction.setCommentText(Utils.getRandomString(20, 30));
        exec(studySaveAction).getId();
        return exec(new StudyGetInfoAction(studyId));
    }

    @Test
    public void delete() {
        // delete a study with no patients and no other associations
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        exec(new StudyDeleteAction(studyInfo.getStudy()));

        // hql query for study should return empty
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + Study.class.getName() + " WHERE id=?");
        q.setParameter(0, studyId);
        Long result = HibernateUtil.getCountFromQuery(q);
        Assert.assertTrue(result.equals(0L));
    }

    @Test
    public void deleteWithPatients() {
        // add patients to study
        Integer studyId = exec(studySaveAction).getId();
        PatientSaveAction patientSaveAction =
            new PatientSaveAction(null, studyId, Utils.getRandomString(
                5, 10),
                Utils.getRandomDate(), null);
        Integer patientId = exec(patientSaveAction).getId();

        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        try {
            exec(new StudyDeleteAction(studyInfo.getStudy()));
            Assert
                .fail("should not be allowed to delete a study with patients");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }
        PatientInfo patientInfo =
            exec(new PatientGetInfoAction(patientId));
        exec(new PatientDeleteAction(patientInfo.patient));
        exec(new StudyDeleteAction(studyInfo.getStudy()));
    }

    @Test
    public void deleteWithContacts() {
        // add contact to study - should be allowed to delete
        //
        // there should be none for this study after the delete
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(studyId));
        ClinicSaveAction clinicSaveAction = ClinicHelper.getSaveAction(
            name + "_clinic", name + "_clinic", ActivityStatus.ACTIVE, true);
        ContactSaveInfo contactSaveInfo = new ContactSaveInfo();
        contactSaveInfo.setName(name + "_contact");
        HashSet<ContactSaveInfo> contactSaveInfos =
            new HashSet<ContactSaveInfo>();
        contactSaveInfos.add(contactSaveInfo);
        clinicSaveAction.setContactSaveInfos(contactSaveInfos);
        Integer clinicId = exec(clinicSaveAction).getId();

        edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo clinicInfo =
            exec(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(1, clinicInfo.contacts.size());
        Integer contactId = clinicInfo.contacts.get(0).getId();

        StudySaveAction studySaveAction =
            StudyHelper.getSaveAction(studyInfo);
        studySaveAction.setContactIds(new HashSet<Integer>(contactId));
        exec(studySaveAction);
        studyInfo = exec(new StudyGetInfoAction(studyId));
        exec(new StudyDeleteAction(studyInfo.getStudy()));
    }

    @Test
    public void deleteWithSourceSpecimens() {
        // add source specimens to study
        Integer studyId = exec(studySaveAction).getId();
        getSourceSpecimens(5, getSpecimenTypes());
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        exec(new StudyDeleteAction(studyInfo.getStudy()));
        Assert.assertTrue(getSourceSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithAliquotedSpecimens() {
        // add source specimens to study
        //
        // there should be none for this study after the delete
        Integer studyId = exec(studySaveAction).getId();
        addAliquotedSpecimens(5, getSpecimenTypes());
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        exec(new StudyDeleteAction(studyInfo.getStudy()));
        Assert.assertTrue(getAliquotedSpecimenCount(studyId).equals(0L));
    }

    @Test
    public void deleteWithStudyEventAttrs() {
        // add study event attributes to study
        //
        // there should be none for this study after the delete
        studySaveAction
            .setStudyEventAttrSaveInfo(getStudyEventAttrSaveInfosAll());
        Integer studyId = exec(studySaveAction).getId();
        StudyInfo studyInfo = exec(new StudyGetInfoAction(studyId));
        exec(new StudyDeleteAction(studyInfo.getStudy()));
        Assert.assertTrue(getStudyEventAttrCount(studyId).equals(0L));

    }

    @Test
    public void getAllStudiesAction() {

        StudyGetAllAction action = new StudyGetAllAction();
        List<Study> infos = exec(action).getList();

        Integer startSize = infos.size();

        Integer firstStudy = StudyHelper.createStudy(getExecutor(),
            name + Utils.getRandomNumericString(15), ActivityStatus.ACTIVE);

        infos = exec(action).getList();
        Assert.assertTrue(infos.size() == startSize + 1);

        StudyHelper.createStudy(getExecutor(),
            name + Utils.getRandomNumericString(15), ActivityStatus.ACTIVE);

        infos = exec(action).getList();
        Assert.assertTrue(infos.size() == startSize + 2);

        Study study = new Study();
        study.setId(firstStudy);
        exec(new StudyDeleteAction(study));

        infos = exec(action).getList();
        Assert.assertTrue(infos.size() == startSize + 1);
    }
}
