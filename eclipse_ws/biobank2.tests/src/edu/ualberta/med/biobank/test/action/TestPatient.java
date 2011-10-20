package edu.ualberta.med.biobank.test.action;

import java.util.Date;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestPatient extends TestAction {

    private SiteWrapper site;
    private StudyWrapper study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // FIXME create new helpers using actions instead?
        site = SiteHelper.addSite("Site - Patient Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy("Study - Patient Test "
            + Utils.getRandomString(10));
    }

    @Test
    public void testSaveNew() throws Exception {
        final String pnumber = "testSaveNew" + r.nextInt();
        final Date date = Utils.getRandomDate();
        final Integer id = appService.doAction(new PatientSaveAction(null,
            study.getId(), pnumber, date));

        new HibernateCheck() {
            @Override
            public void check(Session session) throws Exception {
                // Check patient is in database with correct values
                Patient p = (Patient) session.get(Patient.class, id);
                Assert.assertEquals(pnumber, p.getPnumber());
                Assert
                    .assertTrue(compareDateInHibernate(date, p.getCreatedAt()));
            }
        }.run();
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

        new HibernateCheck() {
            @Override
            public void check(Session session) throws Exception {
                // Check patient is in database with correct values
                Patient p = (Patient) session.get(Patient.class, id);
                Assert.assertEquals(newPNumber, p.getPnumber());

                Assert.assertTrue(compareDateInHibernate(newDate,
                    p.getCreatedAt()));
            }
        }.run();
    }
}
