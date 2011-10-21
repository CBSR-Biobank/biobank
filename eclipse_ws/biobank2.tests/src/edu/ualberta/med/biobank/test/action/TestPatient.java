package edu.ualberta.med.biobank.test.action;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.SQLQuery;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.patient.PatientDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientMergeAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestPatient extends TestAction {

    private StudyWrapper study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // FIXME create new helpers using actions instead?
        study = StudyHelper.addStudy("Study - Patient Test "
            + Utils.getRandomString(10));
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
}
