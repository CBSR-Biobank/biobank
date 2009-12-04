package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleHelper;
import test.ualberta.med.biobank.internal.SampleTypeHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;

public class TestSample extends TestDatabase {

    SampleWrapper sample;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("sitename" + r.nextInt());
        SampleTypeWrapper sampleTypeWrapper = SampleTypeHelper.addSampleType(
            site, "sampletype");
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            "ctType", "ct", 1, 4, 5, true);
        type.setSampleTypeCollection(Arrays
            .asList(new SampleTypeWrapper[] { sampleTypeWrapper }));
        type.persist();
        ContainerWrapper container = ContainerHelper.addContainer(
            "newcontainer", "cc", null, site, type);
        StudyWrapper study = StudyHelper.addStudy(site, "studyname");
        PatientWrapper patient = PatientHelper.addPatient("5684", study);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "ContactClinic");
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            shipment, null);
        sample = SampleHelper.newSample(sampleTypeWrapper, container, pv, 3, 3);
        container.reload();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        sample.persist();
        testGettersAndSetters(sample);
    }

    @Test
    public void testPersistCheckInventoryIdUnique()
        throws BiobankCheckException, Exception {
        sample.persist();

        SampleWrapper duplicate = SampleHelper.newSample(
            sample.getSampleType(), sample.getParent(), sample
                .getPatientVisit(), 2, 2);
        duplicate.setInventoryId(sample.getInventoryId());

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        duplicate.setInventoryId("qqqq" + r.nextInt());
        duplicate.persist();
    }

    @Test
    public void testPersistPositionAlreadyUsed() throws BiobankCheckException,
        Exception {
        sample.persist();

        SampleWrapper duplicate = SampleHelper.newSample(
            sample.getSampleType(), sample.getParent(), sample
                .getPatientVisit(), 3, 3);

        try {
            duplicate.persist();
            Assert.fail("Position in used !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        duplicate.setPosition(2, 3);
        duplicate.persist();

        duplicate.setInventoryId(Utils.getRandomString(5));
        duplicate.persist();
    }

    @Test
    public void testPersistCheckParentAcceptSampleType()
        throws BiobankCheckException, Exception {
        SampleTypeWrapper oldSampleType = sample.getSampleType();

        SampleTypeWrapper type2 = SampleTypeHelper.addSampleType(oldSampleType
            .getSite(), "sampletype_2");
        sample.setSampleType(type2);
        try {
            sample.persist();
            Assert.fail("Container can't hold this type !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        sample.setSampleType(oldSampleType);
        sample.persist();
    }

    @Test
    public void testGetSetPatientVisit() {
        PatientVisitWrapper pvw = new PatientVisitWrapper(appService,
            new PatientVisit());
        sample.setPatientVisit(pvw.getWrappedObject());
        Assert.assertTrue(sample.getPatientVisit().getId() == pvw.getId());
    }

    @Test
    public void testSetSamplePositionFromString() throws Exception {
        sample.setSamplePositionFromString("A1", sample.getParent());
        Assert.assertTrue(sample.getPositionString(false, false).equals("A1"));
        RowColPos pos = sample.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));

        sample.setSamplePositionFromString("C2", sample.getParent());
        Assert.assertTrue(sample.getPositionString(false, false).equals("C2"));
        pos = sample.getPosition();
        Assert.assertTrue((pos.col == 1) && (pos.row == 2));
    }

    @Test
    public void testGetSetPosition() {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        sample.setPosition(position);
        RowColPos newPosition = sample.getPosition();
        Assert.assertTrue((newPosition.row == position.row)
            && (newPosition.col == position.col));
    }

    @Test
    public void testGetSetParent() throws Exception {
        ContainerWrapper oldParent = sample.getParent();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(sample
            .getSite(), "newCtType", "ctNew", 1, 4, 5, true);
        type.setSampleTypeCollection(Arrays
            .asList(new SampleTypeWrapper[] { sample.getSampleType() }));
        ContainerWrapper parent = ContainerHelper.addContainer(
            "newcontainerParent", "ccNew", null, sample.getSite(), type);

        sample.setParent(parent);
        sample.persist();
        // check to make sure gone from old parent
        oldParent.reload();
        Assert.assertTrue(oldParent.getSamples().size() == 0);
        // check to make sure added to new parent
        parent.reload();
        Assert.assertTrue(sample.getParent() != null);
        Collection<SampleWrapper> sampleWrappers = parent.getSamples().values();
        boolean found = false;
        for (SampleWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId().equals(sample.getId()))
                found = true;
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testGetSetSampleType() throws BiobankCheckException, Exception {
        SampleTypeWrapper stw = sample.getSampleType();
        SampleTypeWrapper newStw = SampleTypeHelper.addSampleType(sample
            .getSite(), "newStw");
        stw.persist();
        Assert.assertTrue(stw.getId() != newStw.getId());
        sample.setSampleType(newStw);
        Assert.assertTrue(newStw.getId() == sample.getSampleType().getId());
    }

    @Test
    public void testCreateNewSample() throws BiobankCheckException, Exception {
        Collection<SampleStorageWrapper> ssCollection = new ArrayList<SampleStorageWrapper>();
        SampleStorageWrapper ss1 = new SampleStorageWrapper(appService);
        ss1.setSampleType(SampleTypeHelper.addSampleType(sample.getSite(),
            "ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss1.persist();
        SampleStorageWrapper ss2 = new SampleStorageWrapper(appService);
        ss2.setSampleType(SampleTypeHelper.addSampleType(sample.getSite(),
            "ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss2.persist();
        SampleStorageWrapper ss3 = new SampleStorageWrapper(appService);
        ss3.setSampleType(sample.getSampleType());
        ss3.setVolume(3.0);
        ss3.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss3.persist();
        ssCollection.add(ss1);
        ssCollection.add(ss2);
        ssCollection.add(ss3);
        SampleWrapper newSample = SampleWrapper.createNewSample(appService,
            "newid", sample.getPatientVisit(), sample.getSampleType(),
            ssCollection);
        newSample.persist();
        Sample dbSample = ModelUtils.getObjectWithId(appService, Sample.class,
            newSample.getId());
        Assert.assertTrue(dbSample.getSampleType().getId().equals(
            sample.getSampleType().getId()));
        Assert.assertTrue(dbSample.getQuantity().equals(3.0));
    }

    @Test
    public void testGetSetQuantityFromType() throws Exception {
        Double quantity = sample.getQuantity();
        sample.setQuantityFromType();
        // no sample storages defined yet, should be null
        Assert.assertTrue(quantity == null);
        List<SampleStorageWrapper> ssCollection = new ArrayList<SampleStorageWrapper>();
        SampleStorageWrapper ss1 = new SampleStorageWrapper(appService);
        ss1.setSampleType(SampleTypeHelper.addSampleType(sample.getSite(),
            "ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss1.persist();
        SampleStorageWrapper ss2 = new SampleStorageWrapper(appService);
        ss2.setSampleType(SampleTypeHelper.addSampleType(sample.getSite(),
            "ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss2.persist();
        SampleStorageWrapper ss3 = new SampleStorageWrapper(appService);
        ss3.setSampleType(sample.getSampleType());
        ss3.setVolume(3.0);
        ss3.setStudy(sample.getPatientVisit().getPatient().getStudy());
        ss3.persist();
        ssCollection.add(ss1);
        ssCollection.add(ss2);
        ssCollection.add(ss3);
        sample.getPatientVisit().getPatient().getStudy()
            .setSampleStorageCollection(ssCollection);
        // should be 3
        sample.setQuantityFromType();
        Assert.assertTrue(sample.getQuantity().equals(3.0));
    }

    @Test
    public void testCompareTo() throws BiobankCheckException, Exception {
        sample.setInventoryId("defgh");
        sample.persist();
        SampleWrapper sample2 = SampleHelper.newSample(sample.getSampleType(),
            sample.getParent(), sample.getPatientVisit(), 2, 3);
        sample2.setInventoryId("awert");
        sample2.persist();
        Assert.assertTrue(sample.compareTo(sample2) > 0);

        sample2.setInventoryId("qwerty");
        sample2.persist();
        Assert.assertTrue(sample.compareTo(sample2) < 0);
    }

    @Test
    public void testGetSamplesInSite() throws Exception {
        String name = "testGetSamplesInSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper sampleType = SampleTypeHelper.addSampleType(site,
            name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(5), study);
        ContactHelper.addContactsToStudy(study, name);

        ShipmentWrapper shipment = ShipmentHelper.addShipment(study
            .getClinicCollection().get(0), patient);
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            name, name, 1, 4, 5, true);
        type.setSampleTypeCollection(Arrays
            .asList(new SampleTypeWrapper[] { sampleType }));
        ContainerWrapper container = ContainerHelper.addContainer(name, name,
            null, site, type);
        SampleHelper.addSample(sampleType, container, pv, 0, 0);
        SampleWrapper sample = SampleHelper.newSample(sampleType, container,
            pv, 2, 3);
        sample.setInventoryId(Utils.getRandomString(5));
        sample.persist();
        SampleHelper.addSample(sampleType, container, pv, 3, 3);

        List<SampleWrapper> samples = SampleWrapper.getSamplesInSite(
            appService, sample.getInventoryId(), site);
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals(samples.get(0), sample);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        sample.persist();
        String old = sample.getInventoryId();
        sample.setInventoryId("toto");
        sample.reset();
        Assert.assertEquals(old, sample.getInventoryId());
    }

    @Test
    public void testResetNew() throws Exception {
        sample.setInventoryId("toto");
        sample.reset();
        Assert.assertEquals(null, sample.getInventoryId());
    }

    @Test
    public void testCheckPosition() throws BiobankCheckException, Exception {
        sample.persist();

        SampleWrapper sample2 = new SampleWrapper(appService);
        sample2.setPosition(3, 3);
        sample2.isPositionFree(sample.getParent());
    }

}
