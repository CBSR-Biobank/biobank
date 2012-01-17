package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientMergeAction;
import edu.ualberta.med.biobank.common.action.patient.PatientMergeException;
import edu.ualberta.med.biobank.common.action.patient.PatientNextVisitNumberAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestPatient extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private Integer studyId;

    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        studyId = StudyHelper.createStudy(actionExecutor, name,
            ActivityStatusEnum.ACTIVE);
        siteId = SiteHelper.createSite(actionExecutor, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(studyId));
    }

    @Test
    public void saveNew() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        final Integer id = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date)).getId();

        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertNotNull(p);
        Assert.assertEquals(pnumber, p.getPnumber());
        Assert.assertEquals(date, p.getCreatedAt());
    }

    @Test
    public void uppdate() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date)).getId();

        final String newPNumber = name + "_2";
        final Date newDate = Utils.getRandomDate();
        // update this patient
        actionExecutor.exec(new PatientSaveAction(id, studyId,
            newPNumber, newDate));

        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertEquals(newPNumber, p.getPnumber());
        Assert.assertEquals(newDate, p.getCreatedAt());
    }

    @Test
    public void saveSamePnumber() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        final Integer id = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date)).getId();

        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertNotNull(p);

        // try to save with same pnumber
        try {
            actionExecutor.exec(new PatientSaveAction(null, studyId,
                pnumber, new Date()));
            Assert.fail("should not be able to use the same pnumber twice");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void delete() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date)).getId();

        // delete the patient
        actionExecutor.exec(new PatientDeleteAction(id));

        Patient patient = (Patient) session.get(Patient.class, id);
        Assert.assertNull(patient);
    }

    @Test
    public void deleteWithCevents() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer patientId = actionExecutor.exec(new PatientSaveAction(
            null, studyId, pnumber, date)).getId();
        // add a cevent to the patient:
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId, r
            .nextInt(20), 1, null, null, null));

        // delete the patient
        try {
            actionExecutor.exec(new PatientDeleteAction(patientId));
            Assert
                .fail("should throw an exception since the patient still has on cevent");
        } catch (CollectionNotEmptyException ae) {
            Assert.assertTrue(true);
        }

        Patient patient = (Patient) session.get(Patient.class, patientId);
        Assert.assertNotNull(patient);
    }

    @Test
    public void merge() throws Exception {
        final String string = name;

        // add specimen type
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        // create a new patient 1
        final Integer patientId1 = actionExecutor.exec(new PatientSaveAction(
            null, studyId, string + "1", Utils.getRandomDate())).getId();
        // create cevents in patient1
        createCEventWithSpecimens(patientId1, 1, typeId, 4);
        createCEventWithSpecimens(patientId1, 2, typeId, 2);

        // create a new patient 2
        final Integer patientId2 = actionExecutor.exec(new PatientSaveAction(
            null, studyId, string + "2", Utils.getRandomDate())).getId();
        // create cevents in patient2
        createCEventWithSpecimens(patientId2, 1, typeId, 5);
        createCEventWithSpecimens(patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        actionExecutor.exec(new PatientMergeAction(patientId1, patientId2));

        ActionContext actionContext =
            new ActionContext(actionExecutor.getUser(), session);
        Patient p1 = actionContext.get(Patient.class, patientId1);
        Assert.assertNotNull(p1);
        Patient p2 = actionContext.get(Patient.class, patientId2);
        Assert.assertNull(p2);
        Collection<CollectionEvent> cevents = p1.getCollectionEventCollection();
        Assert.assertEquals(3, cevents.size());
        for (CollectionEvent cevent : cevents) {
            switch (cevent.getVisitNumber()) {
            case 1:
                Assert
                    .assertEquals(9, cevent.getAllSpecimenCollection().size());
                break;
            case 2:
                Assert
                    .assertEquals(2, cevent.getAllSpecimenCollection().size());
                break;
            case 3:
                Assert
                    .assertEquals(7, cevent.getAllSpecimenCollection().size());
                break;
            default:
                Assert.fail("wrong visit number");
            }
        }
    }

    @Test
    public void mergeDifferentStudies() throws Exception {
        // add specimen type
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        // create a new patient 1
        final Integer patientId1 = actionExecutor.exec(new PatientSaveAction(
            null, studyId, name + "1", Utils.getRandomDate())).getId();
        // create cevents in patient1
        createCEventWithSpecimens(patientId1, 1, typeId, 4);
        createCEventWithSpecimens(patientId1, 2, typeId, 2);

        // create a new patient 2
        Integer studyId2 = StudyHelper.createStudy(actionExecutor, name + "_2",
            ActivityStatusEnum.ACTIVE);
        final Integer patientId2 = actionExecutor.exec(new PatientSaveAction(
            null, studyId2, name + "2", Utils.getRandomDate())).getId();
        // create cevents in patient2
        createCEventWithSpecimens(patientId2, 1, typeId, 5);
        createCEventWithSpecimens(patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        try {
            actionExecutor.exec(new PatientMergeAction(patientId1, patientId2));
            Assert
                .fail("Should not be able to merge when patients are from different studies");
        } catch (PatientMergeException pme) {
            Assert.assertTrue(true);
        }

    }

    private void createCEventWithSpecimens(Integer patientId,
        Integer visitNber, Integer specType, int specNber)
        throws ApplicationException {
        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper
                .createSaveCEventSpecimenInfoRandomList(specNber, specType,
                    actionExecutor.getUser().getId(), siteId);
        // Save a new cevent
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            visitNber, 1, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));
    }

    @Test
    public void patientGetSimpleCEventInfoAction() throws Exception {
        final Integer patientId = PatientHelper.createPatient(actionExecutor,
            name, studyId);

        // add specimen type
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                actionExecutor.getUser().getId(), siteId);

        // Save a new cevent with specimens
        final Integer ceventId =
            actionExecutor
                .exec(
                    new CollectionEventSaveAction(null, patientId, r
                        .nextInt(20), 1, null,
                        new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                        null)).getId();

        Map<Integer, SimpleCEventInfo> ceventInfos =
            actionExecutor
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
        Integer patientId =
            PatientHelper.createPatient(actionExecutor, name, studyId);

        // add specimen type
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                actionExecutor.getUser().getId(), siteId);

        // Save a new cevent with specimens
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId, r
            .nextInt(20), 1, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        ArrayList<PatientCEventInfo> infos =
            actionExecutor
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
    public void getInfoAction() throws Exception {
        Date date = Utils.getRandomDate();
        Integer patientId = actionExecutor.exec(new PatientSaveAction(null,
            studyId, name, date)).getId();

        // add specimen type
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                actionExecutor.getUser().getId(), siteId);

        // Save a new cevent with specimens
        Integer visitNumber = r.nextInt(20);
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            visitNumber, 1, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));
        // Save a second new cevent without specimens
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            visitNumber + 1, 1, null, null, null));

        // method to test:
        PatientInfo pinfo = actionExecutor.exec(new PatientGetInfoAction(
            patientId));
        Assert.assertNotNull(pinfo.patient);
        Assert.assertEquals(name, pinfo.patient.getPnumber());
        Assert.assertEquals(date, pinfo.patient.getCreatedAt());
        // no aliquoted specimens added:
        Assert.assertEquals(0, pinfo.aliquotedSpecimenCount.intValue());
        Assert.assertEquals(2, pinfo.cevents.size());
        Assert.assertEquals(specs.size(), pinfo.sourceSpecimenCount.intValue());

        // FIXME test also with aliquoted specimens
    }

    @Test
    public void nextVisitNumber() throws Exception {
        Integer patientId =
            PatientHelper.createPatient(actionExecutor, name, studyId);

        Integer visitNumber = r.nextInt(20);
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            visitNumber, 1, null, null, null));

        Integer next = actionExecutor.exec(new PatientNextVisitNumberAction(
            patientId)).getNextVisitNumber();
        Assert.assertEquals(visitNumber + 1, next.intValue());
    }

    @Test
    public void search() throws Exception {
        final String pnumber = name;
        final Date date = Utils.getRandomDate();
        final Integer patientId = actionExecutor.exec(new PatientSaveAction(
            null, studyId, pnumber, date)).getId();

        // add 2 cevents to this patient:
        int vnber = r.nextInt(20);
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            vnber, 1, null, null, null));
        actionExecutor.exec(new CollectionEventSaveAction(null, patientId,
            vnber + 1, 1, null, null, null));

        // Check patient is in database
        Patient p = (Patient) session.get(Patient.class, patientId);
        Assert.assertNotNull(p);

        // search for it using the pnumber:
        SearchedPatientInfo info = actionExecutor.exec(new PatientSearchAction(
            pnumber));
        Assert.assertNotNull(info.patient);
        Assert.assertEquals(patientId, info.patient.getId());
        Assert.assertEquals(2, info.ceventsCount.intValue());
        Assert.assertNotNull(info.study);
        Assert.assertEquals(studyId, info.study.getId());

    }
}
