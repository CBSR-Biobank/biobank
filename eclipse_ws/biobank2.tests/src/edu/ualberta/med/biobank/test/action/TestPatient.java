package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.SQLQuery;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.patient.GetPatientCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.patient.GetPatientCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.GetSimplePatientCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.patient.GetSimplePatientCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientMergeAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestPatient extends TestAction {

    private StudyWrapper study;
    private SiteWrapper site;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "PatientTest" + r.nextInt();
        // FIXME should not use wrappers for set up
        site = SiteHelper.addSite(name);
        study = StudyHelper.addStudy(name + Utils.getRandomString(10));
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
    public void testSaveExisting() throws Exception {
        final String pnumber = "testSaveExisting" + r.nextInt();
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

    @SuppressWarnings("unchecked")
    @Test
    public void testDelete() throws Exception {
        final String pnumber = "testDelete" + r.nextInt();
        final Date date = Utils.getRandomDate();
        // create a new patient
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        openHibernateSession();
        SQLQuery qry = session.createSQLQuery("select * from patient where id="
            + id);
        List<Object[]> res = qry.list();
        Assert.assertEquals(1, res.size());
        closeHibernateSession();

        // delete the patient
        appService.doAction(new PatientDeleteAction(id));

        openHibernateSession();
        qry = session.createSQLQuery("select * from patient where id=" + id);
        res = qry.list();
        Assert.assertEquals(0, res.size());
        closeHibernateSession();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMerge() throws Exception {
        final String string = "testMerge" + r.nextInt();

        // create a new patient 1
        final Integer id1 = appService.doAction(new PatientSaveAction(null,
            study.getId(), string + "1", Utils.getRandomDate()));

        // create cevents in patient1

        // create a new patient 2
        final Integer id2 = appService.doAction(new PatientSaveAction(null,
            study.getId(), string + "2", Utils.getRandomDate()));

        // create cevents in patient2

        openHibernateSession();
        SQLQuery qry = session.createSQLQuery("select * from patient where id="
            + id1);
        List<Object[]> res = qry.list();
        Assert.assertEquals(1, res.size());

        // FIXME check cevents and specimens inside

        qry = session.createSQLQuery("select * from patient where id=" + id2);
        res = qry.list();
        Assert.assertEquals(1, res.size());

        // FIXME check cevents and specimens inside
        closeHibernateSession();

        // merge patient1 into patient2
        appService.doAction(new PatientMergeAction(id1, id2));

        openHibernateSession();
        qry = session.createSQLQuery("select * from patient where id=" + id2);
        res = qry.list();
        Assert.assertEquals(0, res.size());

        // check cevents of patient1
        closeHibernateSession();
    }

    @Test
    public void testGetSimplePatientCEventInfoAction() throws Exception {
        final Integer patientId = createPatient("testGetSimplePatientCEventInfoAction");

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testSaveWithSpecs" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent with specimens
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, r
                .nextInt(20), 1, Utils.getRandomString(8, 50), site.getId(),
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        HashMap<Integer, SimpleCEventInfo> ceventInfos = appService
            .doAction(new GetSimplePatientCollectionEventInfosAction(patientId));
        Assert.assertEquals(1, ceventInfos.size());
        SimpleCEventInfo info = ceventInfos.get(ceventId);
        Assert.assertNotNull(info);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());
        Date minDate = new Date();
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (sp.timeDrawn.compareTo(minDate) < 0) {
                minDate = sp.timeDrawn;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
    }

    protected Integer createPatient(String s) throws ApplicationException {
        final String pnumber = s + r.nextInt();
        final Date date = Utils.getRandomDate();
        final Integer patientId = appService.doAction(new PatientSaveAction(
            null, study.getId(), pnumber, date));
        return patientId;
    }

    @Test
    public void testGetPatientCEventInfoAction() throws Exception {
        Integer patientId = createPatient("testGetPatientCEventInfoAction");

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testSaveWithSpecs" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent with specimens
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, r
                .nextInt(20), 1, Utils.getRandomString(8, 50), site.getId(),
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        ArrayList<PatientCEventInfo> infos = appService
            .doAction(new GetPatientCollectionEventInfosAction(patientId));
        Assert.assertEquals(1, infos.size());
        PatientCEventInfo info = infos.get(0);
        // FIXME test with aliquoted specimens
        Assert.assertEquals(0, info.aliquotedSpecimenCount.intValue());
        Date minDate = new Date();
        for (SaveCEventSpecimenInfo sp : specs.values()) {
            if (sp.timeDrawn.compareTo(minDate) < 0) {
                minDate = sp.timeDrawn;
            }
        }
        Assert.assertEquals(minDate, info.minSourceSpecimenDate);
        Assert.assertEquals(specs.size(), info.sourceSpecimenCount.intValue());
    }

}
