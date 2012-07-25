package edu.ualberta.med.biobank.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.action.patient.PatientGetCollectionEventInfosAction;
import edu.ualberta.med.biobank.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.action.patient.PatientGetSimpleCollectionEventInfosAction;
import edu.ualberta.med.biobank.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.action.patient.PatientMergeAction;
import edu.ualberta.med.biobank.action.patient.PatientMergeException;
import edu.ualberta.med.biobank.action.patient.PatientNextVisitNumberAction;
import edu.ualberta.med.biobank.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.action.study.StudyInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.action.helper.SiteHelper.Provisioning;
import edu.ualberta.med.biobank.action.helper.StudyHelper;

public class TestPatient extends ActionTest {

    @Rule
    public TestName testname = new TestName();

    private String name;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
    }

    @Test
    public void saveNew() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        final Integer id = exec(new PatientSaveAction(null,
            provisioning.studyId, pnumber, date, null)).getId();

        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertNotNull(p);
        Assert.assertEquals(pnumber, p.getPnumber());
        Assert.assertEquals(date, p.getCreatedAt());
    }

    @Test
    public void update() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = exec(new PatientSaveAction(null,
            provisioning.studyId, pnumber, date, null)).getId();

        final String newPNumber = name + "_2";
        final Date newDate = Utils.getRandomDate();
        // update this patient
        exec(new PatientSaveAction(id, provisioning.studyId,
            newPNumber, newDate, null));

        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertEquals(newPNumber, p.getPnumber());
        Assert.assertEquals(newDate, p.getCreatedAt());
    }

    @Test
    public void checkGetAction() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(getExecutor(),
                provisioning.patientIds.get(0), provisioning.clinicId);
        CEventInfo ceventInfo =
            exec(new CollectionEventGetInfoAction(ceventId));
        List<SpecimenInfo> sourceSpecs = ceventInfo.sourceSpecimenInfos;

        // save some comments on the colection event
        CollectionEventSaveAction ceventSaveAction =
            CollectionEventHelper.getSaveAction(ceventInfo);
        ceventSaveAction.setCommentText(Utils.getRandomString(20, 30));
        exec(ceventSaveAction);
        ceventInfo = exec(new CollectionEventGetInfoAction(ceventId));

        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(provisioning.studyId));
        PatientInfo patientInfo =
            exec(new PatientGetInfoAction(provisioning.patientIds
                .get(0)));

        Assert.assertEquals(studyInfo.getStudy().getName(), patientInfo.patient
            .getStudy().getName());
        Assert.assertEquals(1, patientInfo.ceventInfos.size());
        Assert.assertEquals(new Long(sourceSpecs.size()),
            patientInfo.sourceSpecimenCount);
        Assert.assertEquals(new Long(0), patientInfo.aliquotedSpecimenCount);

        PatientCEventInfo patientCeventInfo = patientInfo.ceventInfos.get(0);

        Assert.assertEquals(new Long(sourceSpecs.size()),
            patientCeventInfo.sourceSpecimenCount);
        Assert.assertEquals(new Long(0),
            patientCeventInfo.aliquotedSpecimenCount);
        Assert.assertEquals(ceventInfo.cevent.getComments().size(),
            patientCeventInfo.cevent.getComments().size());
    }

    @Test
    public void delete() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = exec(new PatientSaveAction(null,
            provisioning.studyId, pnumber, date, null)).getId();

        // delete the patient
        PatientInfo patientInfo = exec(new PatientGetInfoAction(id));
        exec(new PatientDeleteAction(patientInfo.patient));

        Patient patient = (Patient) session.get(Patient.class, id);
        Assert.assertNull(patient);
    }

    @Test
    public void merge() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final String string = name;

        // add specimen type
        final Integer typeId =
            exec(new SpecimenTypeSaveAction(name, name)).getId();

        // create a new patient 1
        final Integer patientId1 = provisioning.patientIds.get(0);
        // create cevents in patient1
        createCEventWithSpecimens(provisioning, patientId1, 1, typeId, 4);
        createCEventWithSpecimens(provisioning, patientId1, 2, typeId, 2);

        // create a new patient 2
        final Integer patientId2 = exec(
            new PatientSaveAction(
                null, provisioning.studyId, string + "2", Utils
                    .getRandomDate(), null)).getId();
        // create cevents in patient2
        createCEventWithSpecimens(provisioning, patientId2, 1, typeId, 5);
        createCEventWithSpecimens(provisioning, patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        exec(new PatientMergeAction(patientId1, patientId2, "testcomment"));

        Patient p1 = (Patient) session.get(Patient.class, patientId1);
        Assert.assertNotNull(p1);
        Patient p2 = (Patient) session.get(Patient.class, patientId2);
        Assert.assertNull(p2);
        Collection<CollectionEvent> cevents = p1.getCollectionEvents();
        Assert.assertEquals(3, cevents.size());
        for (CollectionEvent cevent : cevents) {
            switch (cevent.getVisitNumber()) {
            case 1:
                Assert
                    .assertEquals(9, cevent.getAllSpecimens().size());
                break;
            case 2:
                Assert
                    .assertEquals(2, cevent.getAllSpecimens().size());
                break;
            case 3:
                Assert
                    .assertEquals(7, cevent.getAllSpecimens().size());
                break;
            default:
                Assert.fail("wrong visit number");
            }
        }
    }

    @Test
    public void mergeDifferentStudies() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        // add specimen type
        final Integer typeId =
            exec(new SpecimenTypeSaveAction(name, name)).getId();

        // create a new patient 1
        final Integer patientId1 = provisioning.patientIds.get(0);
        // create cevents in patient1
        createCEventWithSpecimens(provisioning, patientId1, 1, typeId, 4);
        createCEventWithSpecimens(provisioning, patientId1, 2, typeId, 2);

        // create a new patient 2
        Integer studyId2 = StudyHelper.createStudy(getExecutor(), name + "_2",
            ActivityStatus.ACTIVE);
        final Integer patientId2 = exec(new PatientSaveAction(
            null, studyId2, name + "2", Utils.getRandomDate(), null)).getId();
        // create cevents in patient2
        createCEventWithSpecimens(provisioning, patientId2, 1, typeId, 5);
        createCEventWithSpecimens(provisioning, patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        try {
            exec(new PatientMergeAction(patientId1, patientId2, "testcomment"));
            Assert
                .fail("Should not be able to merge when patients are from different studies");
        } catch (PatientMergeException pme) {
            Assert.assertTrue(true);
        }

    }

    private void createCEventWithSpecimens(Provisioning provisioning,
        Integer patientId, Integer visitNber, Integer specType, int specNber) {
        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper
                .createSaveCEventSpecimenInfoRandomList(specNber, specType,
                    getExecutor().getUserId(), provisioning.siteId);
        // Save a new cevent
        exec(new CollectionEventSaveAction(null, patientId,
            visitNber, ActivityStatus.ACTIVE, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));
    }

    @Test
    public void patientGetSimpleCEventInfoAction() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final Integer patientId = provisioning.patientIds.get(0);

        // add specimen type
        final Integer typeId =
            exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                getExecutor().getUserId(), provisioning.siteId);

        // Save a new cevent with specimens
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, patientId,
                getR().nextInt(20) + 1, ActivityStatus.ACTIVE, null,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                null)).getId();

        Map<Integer, SimpleCEventInfo> ceventInfos =
            getExecutor()
                .exec(new PatientGetSimpleCollectionEventInfosAction(
                    patientId)).getMap();
        Assert.assertEquals(1, ceventInfos.size());
        SimpleCEventInfo info = ceventInfos.get(ceventId);
        Assert.assertNotNull(info);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());
        Date minDate = null;
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (minDate == null)
                minDate = sp.createdAt;
            else if (sp.createdAt.compareTo(minDate) < 0) {
                minDate = sp.createdAt;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
    }

    @Test
    public void patientGetCEventInfoAction() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final Integer patientId = provisioning.patientIds.get(0);

        // add specimen type
        final Integer typeId =
            exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                getExecutor().getUserId(), provisioning.siteId);

        // Save a new cevent with specimens
        exec(new CollectionEventSaveAction(null, patientId, getR()
            .nextInt(20), ActivityStatus.ACTIVE, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        List<PatientCEventInfo> infos =
            getExecutor()
                .exec(new PatientGetCollectionEventInfosAction(patientId))
                .getList();
        Assert.assertEquals(1, infos.size());
        PatientCEventInfo info = infos.get(0);
        // no aliquoted specimens added:
        Assert.assertEquals(0, info.aliquotedSpecimenCount.intValue());
        Date minDate = null;
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (minDate == null)
                minDate = sp.createdAt;
            else if (sp.createdAt.compareTo(minDate) < 0) {
                minDate = sp.createdAt;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());

        // FIXME test also with aliquoted specimens
    }

    @Test
    public void nextVisitNumber() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final Integer patientId = provisioning.patientIds.get(0);

        Integer visitNumber = getR().nextInt(20) + 1;
        exec(new CollectionEventSaveAction(null, patientId,
            visitNumber, ActivityStatus.ACTIVE, null, null, null));

        Integer next = exec(new PatientNextVisitNumberAction(
            patientId)).getNextVisitNumber();
        Assert.assertEquals(visitNumber + 1, next.intValue());
    }

    @Test
    public void search() throws Exception {
        Provisioning provisioning = new Provisioning(getExecutor(), name);
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        final Integer patientId = exec(new PatientSaveAction(
            null, provisioning.studyId, pnumber, date, null)).getId();

        // add 2 cevents to this patient:
        int vnber = getR().nextInt(20) + 1;
        exec(new CollectionEventSaveAction(null, patientId,
            vnber, ActivityStatus.ACTIVE, null, null, null));
        exec(new CollectionEventSaveAction(null, patientId,
            vnber + 1, ActivityStatus.ACTIVE, null, null, null));

        // Check patient is in database
        Patient p = (Patient) session.get(Patient.class, patientId);
        Assert.assertNotNull(p);

        // search for it using the pnumber:
        SearchedPatientInfo info = exec(new PatientSearchAction(
            pnumber));
        Assert.assertNotNull(info.patient);
        Assert.assertEquals(patientId, info.patient.getId());
        Assert.assertEquals(2, info.ceventsCount.intValue());
        Assert.assertNotNull(info.study);
        Assert.assertEquals(provisioning.studyId, info.study.getId());

    }
}
