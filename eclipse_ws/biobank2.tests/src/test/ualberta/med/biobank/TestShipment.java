package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class TestShipment extends TestDatabase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        testGettersAndSetters(study);
    }

    @Test
    public void testGetClinic() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetClinicClinic() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetClinicClinicWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetShptSampleSourceCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetShptSampleSourceCollectionCollectionOfShptSampleSourceBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetShptSampleSourceCollectionCollectionOfShptSampleSourceWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientVisitCollection() {
        fail("Not yet implemented");
    }

    // @Test
    // public void testGetPatientVisitCollection() throws Exception {
    // String name = "testGetPatientVisitCollection" + r.nextInt();
    // SiteWrapper site = SiteHelper.addSite(name);
    //
    // ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
    // ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
    // + "CONTACT1");
    //
    // ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
    // ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
    // + "CONTACT2");
    //
    // List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
    // contacts.add(contact1);
    // contacts.add(contact2);
    //
    // StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
    // study1.setContactCollection(contacts);
    // study1.persist();
    // PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
    // int nbClinic1Study1 = PatientVisitHelper.addPatientVisits(patient1,
    // clinic1);
    // int nbClinic2Study1 = PatientVisitHelper.addPatientVisits(patient1,
    // clinic2);
    //
    // StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
    // study2.setContactCollection(contacts);
    // study2.persist();
    // PatientWrapper patient2 = PatientHelper.addPatient(name, study2);
    // int nbClinic1Study2 = PatientVisitHelper.addPatientVisits(patient2,
    // clinic1);
    // int nbClinic2Study2 = PatientVisitHelper.addPatientVisits(patient2,
    // clinic2);
    //
    // clinic1.reload();
    // Assert.assertEquals(nbClinic1Study1 + nbClinic1Study2, clinic1
    // .getPatientVisitCollection().size());
    // clinic2.reload();
    // Assert.assertEquals(nbClinic2Study1 + nbClinic2Study2, clinic2
    // .getPatientVisitCollection().size());
    // }

    @Test
    public void testSetPatientVisitCollectionCollectionOfPatientVisitBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPatientVisitCollectionCollectionOfPatientVisitWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetComment() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetComment() {
        fail("Not yet implemented");
    }

}
