package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.SampleSourceHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShptSampleSourceHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SampleSource;

public class TestShptSampleSource extends TestDatabase {

    private ShptSampleSourceWrapper w;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic);
        w = ShptSampleSourceHelper.addShptSampleSource(Utils
            .getRandomString(10), shipment);
        SampleSourceWrapper ssw = SampleSourceHelper.addSampleSource("setUp");
        w.setSampleSource(ssw.getWrappedObject());
    }

    @Test
    public void checkModification() {
        Assert.fail("Check nothing missing after model modification");
    }

    @Test
    public void testGetSetSampleSource() throws Exception {
        SampleSourceWrapper oldSource = w.getSampleSource();
        String name = "testGetSetSampleSource";
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(name);

        w.setSampleSource(newSampleSource);
        w.persist();

        SampleSource ss = ModelUtils.getObjectWithId(appService,
            SampleSource.class, newSampleSource.getId());
        Assert.assertTrue(ss != null);
        Assert.assertTrue(!oldSource.getId()
            .equals(w.getSampleSource().getId()));
        Assert.assertTrue(w.getSampleSource().getId().equals(
            newSampleSource.getId()));
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(w);
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic);
        ShptSampleSourceWrapper sss = ShptSampleSourceHelper
            .addShptSampleSource(name, shipment);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        patients.add(PatientHelper.addPatient(name, study));
        sss.setPatientCollection(patients);
        sss.persist();
        sss.reload();

        List<PatientWrapper> patientsCollection = sss.getPatientCollection();
        int sizeFound = patientsCollection.size();

        Assert.assertEquals(1, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        ContactHelper.addContactsToStudy(study, name);

        List<ContactWrapper> contacts = study.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < contacts.size() - 1; i++) {
                ContactWrapper contact1 = contacts.get(i);
                ContactWrapper contact2 = contacts.get(i + 1);
                Assert.assertTrue(contact1.compareTo(contact2) <= 0);
            }
        }
    }

    @Test
    public void testAddInContactCollection() throws Exception {
        String name = "testAddInContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        List<ClinicWrapper> clinics = site.getClinicCollection();
        for (ContactWrapper contact : contacts) {
            clinics.remove(contact.getClinicWrapper());
        }
        ClinicWrapper clinicNotAdded = DbHelper.chooseRandomlyInList(clinics);
        ContactWrapper contactToAdd = DbHelper
            .chooseRandomlyInList(clinicNotAdded.getContactCollection());
        contacts.add(contactToAdd);
        study.setContactCollection(contacts);
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber + 1, study.getContactCollection().size());
    }
}
