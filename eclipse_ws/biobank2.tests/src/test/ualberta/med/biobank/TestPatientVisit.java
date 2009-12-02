package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class TestPatientVisit extends TestDatabase {

    private SiteWrapper site;

    private StudyWrapper study;

    private ClinicWrapper clinic;

    private ShipmentWrapper shipment;

    private PatientWrapper patient;

    // the methods to skip in the getters and setters test
    private static final List<String> GETTER_SKIP_METHODS = Arrays
        .asList(new String[] { "getPvInfo", "getPvInfoType" });

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        site = SiteHelper.addSite("Site - Patient Visit Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy(site, "Study - Patient Visit Test "
            + Utils.getRandomString(10));
        clinic = ClinicHelper.addClinic(site, "Clinic - Patient Visit Test "
            + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Visit Test");
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
        shipment = ShipmentHelper.addShipment(clinic, patient);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        testGettersAndSetters(visit, GETTER_SKIP_METHODS);
    }

    @Test
    public void testCompareTo() throws Exception {
        // visit2's date processed is 1 day after visit1's
        Date dateProcessed = Utils.getRandomDate();
        PatientVisitWrapper visit1 = PatientVisitHelper.addPatientVisit(
            patient, shipment, dateProcessed);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateProcessed);
        cal.add(Calendar.DATE, 1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic, patient);

        PatientVisitWrapper visit2 = PatientVisitHelper.addPatientVisit(
            patient, shipment2, cal.getTime());

        Assert.assertEquals(-1, visit1.compareTo(visit2));

        // visit2's date processed is 1 day before visit1's
        cal.add(Calendar.DATE, -2);
        visit2.setDateProcessed(cal.getTime());
        visit2.persist();
        visit2.reload();
        Assert.assertEquals(1, visit1.compareTo(visit2));

        // check against itself
        Assert.assertEquals(0, visit1.compareTo(visit1));
    }

    @Test
    public void testReset() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.reload();
    }

    @Test
    public void testDelete() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.delete();
    }

    @Test
    public void testGetWrappedClass() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSampleCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfo() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoType() {
        fail("Not yet implemented");
    }

    @Test
    public void testPvInfoAllowedValues() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateDrawn() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateProcessed() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateReceived() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatient() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetShipment() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoLabels() {
        fail("Not yet implemented");
    }

    @Test
    public void testRemoveDeletedPvSampleSources() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckVisitDateDrawnUnique() {
        fail("Not yet implemented");
    }

    @Test
    public void testPersist() throws Exception {
        PatientVisitWrapper pv = PatientVisitHelper.newPatientVisit(patient,
            shipment, DateFormatter.dateFormatter.parse("2009-12-25 00:00"));
        pv.persist();
    }

    public void testPersistFail() throws Exception {
        Assert.fail("check for checkVisitDateProcessedUnique");
        Assert.fail("check for checkPatientClinicInSameStudy");
    }

    @Test
    public void testSetPvSampleSourceCollection() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());

        PvSampleSourceWrapper pvSampleSourceWrapper = new PvSampleSourceWrapper(
            appService);
        pvSampleSourceWrapper.setDateDrawn(Utils.getRandomDate());
        pvSampleSourceWrapper.setPatientVisit(visit);
        pvSampleSourceWrapper.setQuantity(2);
        pvSampleSourceWrapper.setSampleSource(SampleSourceWrapper
            .getAllSampleSources(appService).get(0));

        visit.setPvSampleSourceCollection(Arrays
            .asList(new PvSampleSourceWrapper[] { pvSampleSourceWrapper }));

        visit.persist();

        visit.reload();
        Assert.assertEquals(1, visit.getPvSampleSourceCollection().size());
    }
}
