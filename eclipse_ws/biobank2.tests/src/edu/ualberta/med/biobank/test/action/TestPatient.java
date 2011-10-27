package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.ActionUtil;
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
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestPatient extends TestAction {

    private StudyWrapper study;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "PatientTest" + r.nextInt();

        // FIXME should not use wrappers for set up
        study = StudyHelper.addStudy(name + Utils.getRandomString(10));
        siteId = SiteHelper.createSite(appService, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(study.getId()));
    }

    @Test
    public void testSaveNew() throws Exception {
        final String pnumber = "testSaveNew" + r.nextInt();
        final Date date = Utils.getRandomDate();
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        openHibernateSession();
        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertNotNull(p);
        Assert.assertEquals(pnumber, p.getPnumber());
        Assert.assertTrue(compareDateInHibernate(date, p.getCreatedAt()));
        closeHibernateSession();
    }

    @Test
    public void testUpdate() throws Exception {
        final String pnumber = "testUpdate" + r.nextInt();
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        final String newPNumber = "testSaveExisting-2" + r.nextInt();
        final Date newDate = Utils.getRandomDate();
        // update this patient
        appService.doAction(new PatientSaveAction(id, study.getId(),
            newPNumber, newDate));

        openHibernateSession();
        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertEquals(newPNumber, p.getPnumber());
        Assert.assertTrue(compareDateInHibernate(newDate, p.getCreatedAt()));
        closeHibernateSession();
    }

    @Test
    public void testSaveSamePnumber() throws Exception {
        final String pnumber = "testSaveSamePnumber" + r.nextInt();
        final Date date = Utils.getRandomDate();
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        openHibernateSession();
        // Check patient is in database with correct values
        Patient p = (Patient) session.get(Patient.class, id);
        Assert.assertNotNull(p);
        closeHibernateSession();

        // try to save with same pnumber
        try {
            appService.doAction(new PatientSaveAction(null, study.getId(),
                pnumber, new Date()));
            Assert.fail("should not be able to use the same pnumber twice");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        final String pnumber = "testDelete" + r.nextInt();
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        // delete the patient
        appService.doAction(new PatientDeleteAction(id));

        openHibernateSession();
        Patient patient = (Patient) session.get(Patient.class, id);
        Assert.assertNull(patient);
        closeHibernateSession();
    }

    @Test
    public void testDeleteWithCevents() throws Exception {
        final String pnumber = "testDeleteWithCevents" + r.nextInt();
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer patientId = appService.doAction(new PatientSaveAction(
            null, study.getId(), pnumber, date));
        // add a cevent to the patient:
        appService.doAction(new CollectionEventSaveAction(null, patientId, r
            .nextInt(20), 1, null, siteId, null,
            null));

        // delete the patient
        try {
            appService.doAction(new PatientDeleteAction(patientId));
            Assert
                .fail("should throw an exception since the patient still has on cevent");
        } catch (CollectionNotEmptyException ae) {
            Assert.assertTrue(true);
        }

        openHibernateSession();
        Patient patient = (Patient) session.get(Patient.class, patientId);
        Assert.assertNotNull(patient);
        closeHibernateSession();
    }

    @Test
    public void testMerge() throws Exception {
        final String string = "testMerge" + r.nextInt();

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType(string).getId();

        // create a new patient 1
        final Integer patientId1 = appService.doAction(new PatientSaveAction(
            null, study.getId(), string + "1", Utils.getRandomDate()));
        // create cevents in patient1
        createCEventWithSpecimens(patientId1, 1, typeId, 4);
        createCEventWithSpecimens(patientId1, 2, typeId, 2);

        // create a new patient 2
        final Integer patientId2 = appService.doAction(new PatientSaveAction(
            null, study.getId(), string + "2", Utils.getRandomDate()));
        // create cevents in patient2
        createCEventWithSpecimens(patientId2, 1, typeId, 5);
        createCEventWithSpecimens(patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        appService.doAction(new PatientMergeAction(patientId1, patientId2));

        openHibernateSession();
        Patient p1 = ActionUtil.sessionGet(session, Patient.class, patientId1);
        Assert.assertNotNull(p1);
        Patient p2 = ActionUtil.sessionGet(session, Patient.class, patientId2);
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

        closeHibernateSession();
    }

    @Test
    public void testMergeDifferentStudies() throws Exception {
        final String string = "testMergeDifferentStudies" + r.nextInt();

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType(string).getId();

        // create a new patient 1
        final Integer patientId1 = appService.doAction(new PatientSaveAction(
            null, study.getId(), string + "1", Utils.getRandomDate()));
        // create cevents in patient1
        createCEventWithSpecimens(patientId1, 1, typeId, 4);
        createCEventWithSpecimens(patientId1, 2, typeId, 2);

        // create a new patient 2
        StudyWrapper study2 = StudyHelper.addStudy(string
            + Utils.getRandomString(10));
        final Integer patientId2 = appService.doAction(new PatientSaveAction(
            null, study2.getId(), string + "2", Utils.getRandomDate()));
        // create cevents in patient2
        createCEventWithSpecimens(patientId2, 1, typeId, 5);
        createCEventWithSpecimens(patientId2, 3, typeId, 7);

        // merge patient1 into patient2
        try {
            appService.doAction(new PatientMergeAction(patientId1, patientId2));
            Assert
                .fail("Should not be able to merge when patients are from different studies");
        } catch (PatientMergeException pme) {
            Assert.assertTrue(true);
        }

    }

    private void createCEventWithSpecimens(Integer patientId,
        Integer visitNber, Integer specType, int specNber)
        throws ApplicationException {
        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(specNber, specType);
        // Save a new cevent
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            visitNber, 1, null, siteId,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));
    }

    @Test
    public void testPatientGetSimpleCEventInfoAction() throws Exception {
        final Integer patientId = PatientHelper.createPatient(appService,
            "testPatientGetSimpleCEventInfoAction", study.getId());

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testSaveWithSpecs" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent with specimens
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, r
                .nextInt(20), 1, null, siteId,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        HashMap<Integer, SimpleCEventInfo> ceventInfos = appService
            .doAction(new PatientGetSimpleCollectionEventInfosAction(patientId));
        Assert.assertEquals(1, ceventInfos.size());
        SimpleCEventInfo info = ceventInfos.get(ceventId);
        Assert.assertNotNull(info);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());
        Date minDate = null;
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (minDate == null)
                minDate = sp.timeDrawn;
            else if (sp.timeDrawn.compareTo(minDate) < 0) {
                minDate = sp.timeDrawn;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
    }

    @Test
    public void testPatientGetCEventInfoAction() throws Exception {
        Integer patientId = PatientHelper
            .createPatient(appService, "testPatientGetCEventInfoAction",
                study.getId());

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testSaveWithSpecs" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent with specimens
        appService.doAction(new CollectionEventSaveAction(null, patientId, r
            .nextInt(20), 1, null, siteId,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        ArrayList<PatientCEventInfo> infos = appService
            .doAction(new PatientGetCollectionEventInfosAction(patientId));
        Assert.assertEquals(1, infos.size());
        PatientCEventInfo info = infos.get(0);
        // no aliquoted specimens added:
        Assert.assertEquals(0, info.aliquotedSpecimenCount.intValue());
        Date minDate = null;
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (minDate == null)
                minDate = sp.timeDrawn;
            else if (sp.timeDrawn.compareTo(minDate) < 0) {
                minDate = sp.timeDrawn;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());

        // FIXME test also with aliquoted specimens
    }

    @Test
    public void testGetInfoAction() throws Exception {
        String name = "testGetInfoAction" + r.nextInt();
        Date date = Utils.getRandomDate();
        Integer patientId = appService.doAction(new PatientSaveAction(null,
            study.getId(), name, date));

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType(name).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent with specimens
        Integer visitNumber = r.nextInt(20);
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            visitNumber, 1, null, siteId,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));
        // Save a second new cevent without specimens
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            visitNumber + 1, 1, null, siteId,
            null, null));

        // method to test:
        PatientInfo pinfo = appService.doAction(new PatientGetInfoAction(
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
    public void testNextVisitNumber() throws Exception {
        Integer patientId = PatientHelper.createPatient(appService,
            "testNextVisitNumber", study.getId());

        Integer visitNumber = r.nextInt(20);
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            visitNumber, 1, null, siteId, null,
            null));

        Integer next = appService.doAction(new PatientNextVisitNumberAction(
            patientId));
        Assert.assertEquals(visitNumber + 1, next.intValue());
    }

    @Test
    public void testSearch() throws Exception {
        final String pnumber = "testSearch" + r.nextInt();
        final Date date = Utils.getRandomDate();
        final Integer patientId = appService.doAction(new PatientSaveAction(
            null, study.getId(), pnumber, date));

        // add 2 cevents to this patient:
        int vnber = r.nextInt(20);
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            vnber, 1, null, siteId, null, null));
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            vnber + 1, 1, null, siteId, null,
            null));

        openHibernateSession();
        // Check patient is in database
        Patient p = (Patient) session.get(Patient.class, patientId);
        Assert.assertNotNull(p);
        closeHibernateSession();

        // search for it using the pnumber:
        SearchedPatientInfo info = appService.doAction(new PatientSearchAction(
            pnumber));
        Assert.assertNotNull(info.patient);
        Assert.assertEquals(patientId, info.patient.getId());
        Assert.assertEquals(2, info.ceventsCount.intValue());
        Assert.assertNotNull(info.study);
        Assert.assertEquals(study.getId(), info.study.getId());

    }
}
