package test.ualberta.med.biobank;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;

public class TestPatient extends TestDatabase {

    private SiteWrapper site;

    private StudyWrapper study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        site = SiteHelper.addSite("Site - Patient Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy(site, "Study - Patient Test "
            + Utils.getRandomString(10));
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        testGettersAndSetters(patient);
    }

    @Test
    public void testCompareTo() throws Exception {
        // create patient1 and patient2 with patient 2 being the second when
        // sorted
        String pnumber = "12345";
        PatientWrapper patient1 = PatientHelper.addPatient(pnumber, study);
        pnumber = "12346";
        PatientWrapper patient2 = PatientHelper.addPatient(pnumber, study);

        Assert.assertEquals(-1, patient1.compareTo(patient2));

        // now set patient2's number to be first when sorted
        patient2.setNumber("12344");
        patient2.persist();

        Assert.assertEquals(1, patient1.compareTo(patient2));

        // compare patient1 to itself
        Assert.assertEquals(0, patient1.compareTo(patient1));
    }

    @Test
    public void testReset() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.reload();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        Assert.assertEquals(Patient.class, patient.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.delete();

        ClinicWrapper clinic = ClinicHelper.addClinic(site,
            "Clinic - Patient Test " + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);

        List<PatientVisitWrapper> visitsAdded = PatientVisitHelper
            .addPatientVisits(patient, shipment);
    }

    @Test
    public void testGetStudy() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        Assert.assertEquals(study, patient.getStudy());
    }

    @Test
    public void testCheckPatientNumberUnique() throws Exception {
        String pnumber = "12345";
        PatientHelper.addPatient(pnumber, study);
        PatientWrapper patient2 = PatientHelper.newPatient(pnumber, study);

        try {
            patient2.persist();
            Assert
                .fail("should not be allowed to add patient because of duplicate name");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetPatientVisitCollection() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        List<PatientVisitWrapper> list = patient.getPatientVisitCollection();
        Assert.assertEquals(null, list);

        ClinicWrapper clinic = ClinicHelper.addClinic(site,
            "Clinic - Patient Test " + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);

        List<PatientVisitWrapper> visitsAdded = PatientVisitHelper
            .addPatientVisits(patient, shipment);

        patient.reload();
        List<PatientVisitWrapper> visits = patient.getPatientVisitCollection();
        Assert.assertTrue(visits.containsAll(visitsAdded));

        // delete some random visits, ensure at least one left
        int numToDelete = r.nextInt(visitsAdded.size() - 1);
        for (int i = 0; i < numToDelete; ++i) {
            PatientVisitWrapper v = visitsAdded.get(r.nextInt(visitsAdded
                .size()));
            visitsAdded.remove(v);
            v.delete();
        }

        // make sure patient now only has the visits that were not deleted
        patient.reload();
        visits = patient.getPatientVisitCollection();
        Assert.assertTrue(visits.containsAll(visitsAdded));

        // now remove all patient visits
        while (visitsAdded.size() > 0) {
            PatientVisitWrapper v = visitsAdded.get(0);
            v.delete();
            visitsAdded.remove(0);
        }

        // make sure patient does not have any patient visits
        patient.reload();
        visits = patient.getPatientVisitCollection();
        Assert.assertEquals(0, visits.size());
    }

    @Test
    public void testGetPatientInSite() throws Exception {
        String pnumber = Utils.getRandomNumericString(20);
        PatientWrapper patient = PatientHelper.addPatient(pnumber, study);

        PatientWrapper patient2 = PatientWrapper.getPatientInSite(appService,
            pnumber, site);
        Assert.assertEquals(patient, patient2);
    }

}
