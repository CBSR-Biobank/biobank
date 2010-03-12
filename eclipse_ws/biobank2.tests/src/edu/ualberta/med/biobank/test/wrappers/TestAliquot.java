package edu.ualberta.med.biobank.test.wrappers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestAliquot extends TestDatabase {

    private AliquotWrapper aliquot;

    private Integer siteId;

    private ContainerWrapper topContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("sitename" + r.nextInt());
        siteId = site.getId();
        SampleTypeWrapper sampleTypeWrapper = SampleTypeHelper.addSampleType(
            site, "sampletype" + r.nextInt());

        ContainerTypeWrapper typeChild = ContainerTypeHelper.addContainerType(
            site, "ctTypeChild" + r.nextInt(), "ctChild", 1, 4, 5, false);
        typeChild.addSampleTypes(Arrays.asList(sampleTypeWrapper));
        typeChild.persist();

        ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
            site, "topType" + r.nextInt(), "ct", 1, 4, 5, true);
        topType.addChildContainerTypes(Arrays.asList(typeChild));
        topType.persist();

        topContainer = ContainerHelper.addContainer("top" + r.nextInt(), "cc",
            null, site, topType);

        ContainerWrapper container = ContainerHelper.addContainer(null, "2nd",
            topContainer, site, typeChild, 0, 0);

        StudyWrapper study = StudyHelper.addStudy(site, "studyname"
            + r.nextInt());
        PatientWrapper patient = PatientHelper.addPatient("5684", study);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "ContactClinic");
        study.addContacts(Arrays.asList(contact));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            shipment, null);
        aliquot = AliquotHelper.newAliquot(sampleTypeWrapper, container, pv, 3,
            3);
        container.reload();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        aliquot.persist();
        testGettersAndSetters(aliquot);
    }

    @Test
    public void testPersistCheckInventoryIdUnique()
        throws BiobankCheckException, Exception {
        aliquot.persist();

        AliquotWrapper duplicate = AliquotHelper.newAliquot(aliquot
            .getSampleType(), aliquot.getParent(), aliquot.getPatientVisit(),
            2, 2);
        duplicate.setInventoryId(aliquot.getInventoryId());

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        duplicate.setInventoryId("qqqq" + r.nextInt());
        duplicate.persist();

        duplicate.setInventoryId(aliquot.getInventoryId());
        try {
            duplicate.persist();
            Assert
                .fail("still can't save it with  the same inventoryId after a first add with anotehr inventoryId!");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistPositionAlreadyUsed() throws BiobankCheckException,
        Exception {
        aliquot.persist();

        AliquotWrapper duplicate = AliquotHelper.newAliquot(aliquot
            .getSampleType(), aliquot.getParent(), aliquot.getPatientVisit(),
            3, 3);

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
        SampleTypeWrapper oldSampleType = aliquot.getSampleType();

        SampleTypeWrapper type2 = SampleTypeHelper.addSampleType(oldSampleType
            .getSite(), "sampletype_2");
        aliquot.setSampleType(type2);
        try {
            aliquot.persist();
            Assert.fail("Container can't hold this type !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        aliquot.setSampleType(oldSampleType);
        aliquot.persist();

        ContainerWrapper container = new ContainerWrapper(appService);
        AliquotWrapper sample = new AliquotWrapper(appService);
        sample.setParent(container);
        try {
            sample.persist();
            Assert.fail("container has no container type");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckPatientVisitNotNull() throws BiobankCheckException,
        Exception {
        aliquot.setPatientVisit(null);
        try {
            aliquot.persist();
            Assert.fail("Patient visit should be set!");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckParentFromSameSite() throws BiobankCheckException,
        Exception {
        String name = "testCheckParentFromSameSite" + r.nextInt();
        SiteWrapper newSite = SiteHelper.addSite(name);
        StudyWrapper newStudy = StudyHelper.addStudy(newSite, name);
        PatientWrapper newPatient = PatientHelper.addPatient(name, newStudy);
        ClinicWrapper clinic = ClinicHelper.addClinic(newSite, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        newStudy.addContacts(Arrays.asList(contact));
        newStudy.persist();
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic,
            newPatient);
        PatientVisitWrapper newVisit = PatientVisitHelper.addPatientVisit(
            newPatient, shipment, null);

        aliquot.setPatientVisit(newVisit);
        try {
            aliquot.persist();
            Assert.fail("visit not from same site that parent");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        aliquot.persist();
        SampleTypeWrapper type1 = aliquot.getSampleType();
        SampleTypeWrapper type2 = SampleTypeHelper.addSampleType(aliquot
            .getSite(), "sampletype_2");
        SampleTypeHelper.removeFromCreated(type2);
        type2.delete();

        try {
            type1.delete();
            Assert.fail("cannot delete a type use by a sample");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        aliquot.delete();
        SampleTypeHelper.removeFromCreated(type1);
        type1.delete();
    }

    @Test
    public void testGetSetPatientVisit() {
        PatientVisitWrapper pvw = new PatientVisitWrapper(appService,
            new PatientVisit());
        aliquot.setPatientVisit(pvw);
        Assert.assertTrue(aliquot.getPatientVisit().getId() == pvw.getId());
    }

    @Test
    public void testSetAliquotPositionFromString() throws Exception {
        aliquot.setAliquotPositionFromString("A1", aliquot.getParent());
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("A1"));
        RowColPos pos = aliquot.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));

        aliquot.setAliquotPositionFromString("C2", aliquot.getParent());
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("C2"));
        pos = aliquot.getPosition();
        Assert.assertTrue((pos.col == 1) && (pos.row == 2));

        try {
            aliquot.setAliquotPositionFromString("79", aliquot.getParent());
            Assert.fail("invalid position");
        } catch (Exception bce) {
            Assert.assertTrue(true);
        }

        AliquotWrapper sample = new AliquotWrapper(appService);
        Assert.assertNull(sample.getPositionString());
    }

    @Test
    public void testGetSite() {
        Assert.assertEquals(siteId, aliquot.getSite().getId());

        AliquotWrapper sample1 = new AliquotWrapper(appService);
        Assert.assertNull(sample1.getSite());
    }

    @Test
    public void testGetPositionString() throws Exception {
        aliquot.setAliquotPositionFromString("A1", aliquot.getParent());
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("A1"));
        String parentLabel = aliquot.getParent().getLabel();
        Assert.assertTrue(aliquot.getPositionString(true, false).equals(
            parentLabel + "A1"));
        Assert.assertTrue(aliquot.getPositionString().equals(
            parentLabel + "A1 ("
                + topContainer.getContainerType().getNameShort() + ")"));

    }

    @Test
    public void testGetSetPosition() {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        aliquot.setPosition(position);
        RowColPos newPosition = aliquot.getPosition();
        Assert.assertTrue((newPosition.row == position.row)
            && (newPosition.col == position.col));
    }

    @Test
    public void testGetSetParent() throws Exception {
        ContainerWrapper oldParent = aliquot.getParent();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(
            aliquot.getSite(), "newCtType", "ctNew", 1, 4, 5, true);
        type.addSampleTypes(Arrays.asList(aliquot.getSampleType()));
        type.persist();
        ContainerWrapper parent = ContainerHelper.addContainer(
            "newcontainerParent", "ccNew", null, aliquot.getSite(), type);

        aliquot.setParent(parent);
        aliquot.persist();
        // check to make sure gone from old parent
        oldParent.reload();
        Assert.assertTrue(oldParent.getSamples().size() == 0);
        // check to make sure added to new parent
        parent.reload();
        Assert.assertTrue(aliquot.getParent() != null);
        Collection<AliquotWrapper> sampleWrappers = parent.getSamples()
            .values();
        boolean found = false;
        for (AliquotWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId().equals(aliquot.getId()))
                found = true;
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testGetSetSampleType() throws BiobankCheckException, Exception {
        SampleTypeWrapper stw = aliquot.getSampleType();
        SampleTypeWrapper newType = SampleTypeHelper.addSampleType(aliquot
            .getSite(), "newStw");
        stw.persist();
        Assert.assertTrue(stw.getId() != newType.getId());
        aliquot.setSampleType(newType);
        Assert.assertTrue(newType.getId() == aliquot.getSampleType().getId());

        AliquotWrapper sample1 = new AliquotWrapper(appService);
        sample1.setSampleType(null);
        Assert.assertNull(sample1.getSampleType());
    }

    @Test
    public void testGetSetQuantityFromType() throws Exception {
        Double quantity = aliquot.getQuantity();
        aliquot.setQuantityFromType();
        // no sample storages defined yet, should be null
        Assert.assertTrue(quantity == null);
        SampleStorageWrapper ss1 = new SampleStorageWrapper(appService);
        ss1.setSampleType(SampleTypeHelper.addSampleType(aliquot.getSite(),
            "ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(aliquot.getPatientVisit().getPatient().getStudy());
        ss1.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        ss1.persist();
        SampleStorageWrapper ss2 = new SampleStorageWrapper(appService);
        ss2.setSampleType(SampleTypeHelper.addSampleType(aliquot.getSite(),
            "ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(aliquot.getPatientVisit().getPatient().getStudy());
        ss2.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        ss2.persist();
        SampleStorageWrapper ss3 = new SampleStorageWrapper(appService);
        ss3.setSampleType(aliquot.getSampleType());
        ss3.setVolume(3.0);
        ss3.setStudy(aliquot.getPatientVisit().getPatient().getStudy());
        ss3.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        ss3.persist();
        aliquot.getPatientVisit().getPatient().getStudy().addSampleStorage(
            Arrays.asList(ss1, ss2, ss3));
        // should be 3
        aliquot.setQuantityFromType();
        Assert.assertTrue(aliquot.getQuantity().equals(3.0));
    }

    @Test
    public void testGetFormattedLinkDate() throws Exception {
        Date date = Utils.getRandomDate();
        aliquot.setLinkDate(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(
            aliquot.getFormattedLinkDate()));
    }

    @Test
    public void testCompareTo() throws BiobankCheckException, Exception {
        aliquot.setInventoryId("defgh");
        aliquot.persist();
        AliquotWrapper sample2 = AliquotHelper.newAliquot(aliquot
            .getSampleType(), aliquot.getParent(), aliquot.getPatientVisit(),
            2, 3);
        sample2.setInventoryId("awert");
        sample2.persist();
        Assert.assertTrue(aliquot.compareTo(sample2) > 0);

        sample2.setInventoryId("qwerty");
        sample2.persist();
        Assert.assertTrue(aliquot.compareTo(sample2) < 0);
    }

    @Test
    public void testGetAliquotsInSite() throws Exception {
        String name = "testGetAliquotsInSite" + r.nextInt();
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
        type.addSampleTypes(Arrays.asList(sampleType));
        type.persist();
        ContainerWrapper container = ContainerHelper.addContainer(name, name,
            null, site, type);
        AliquotHelper.addAliquot(sampleType, container, pv, 0, 0);
        AliquotWrapper sample = AliquotHelper.newAliquot(sampleType, container,
            pv, 2, 3);
        sample.setInventoryId(Utils.getRandomString(5));
        sample.persist();
        AliquotHelper.addAliquot(sampleType, container, pv, 3, 3);

        List<AliquotWrapper> samples = AliquotWrapper.getAliquotsInSite(
            appService, sample.getInventoryId(), site);
        Assert.assertEquals(1, samples.size());
        Assert.assertEquals(samples.get(0), sample);
    }

    @Test
    public void testGetAliquotsInSiteWithPositionLabel() throws Exception {
        String name = "testGetAliquotsInSiteWithPositionLabel" + r.nextInt();
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
        type.addSampleTypes(Arrays.asList(sampleType));
        type.persist();
        ContainerWrapper container = ContainerHelper.addContainer(name, name,
            null, site, type);
        AliquotHelper.addAliquot(sampleType, container, pv, 0, 0);
        AliquotWrapper aliquot = AliquotHelper.newAliquot(sampleType, container,
            pv, 2, 3);
        aliquot.setInventoryId(Utils.getRandomString(5));
        aliquot.persist();
        AliquotHelper.addAliquot(sampleType, container, pv, 3, 3);

        List<AliquotWrapper> aliquots = AliquotWrapper
            .getAliquotsInSiteWithPositionLabel(appService, site, aliquot
                .getPositionString(true, false));
        Assert.assertEquals(1, aliquots.size());
        Assert.assertEquals(aliquots.get(0), aliquot);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        aliquot.persist();
        String old = aliquot.getInventoryId();
        aliquot.setInventoryId("toto");
        aliquot.reset();
        Assert.assertEquals(old, aliquot.getInventoryId());
    }

    @Test
    public void testResetNew() throws Exception {
        aliquot.setInventoryId("toto");
        aliquot.reset();
        Assert.assertEquals(null, aliquot.getInventoryId());
    }

    @Test
    public void testCheckPosition() throws BiobankCheckException, Exception {
        aliquot.persist();

        AliquotWrapper sample2 = new AliquotWrapper(appService);
        sample2.setPosition(3, 3);

        Assert.assertFalse(sample2.isPositionFree(aliquot.getParent()));

        sample2.setPosition(2, 3);
        Assert.assertTrue(sample2.isPositionFree(aliquot.getParent()));
    }

    @Test
    public void testDebugRandomMethods() throws Exception {
        String name = "testDebugRandomMethods" + r.nextInt();
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
        type.addSampleTypes(Arrays.asList(sampleType));
        type.persist();
        ContainerWrapper container = ContainerHelper.addContainer(name, name,
            null, site, type);
        AliquotHelper.addAliquot(sampleType, container, pv, 0, 0);
        AliquotWrapper sample = AliquotHelper.newAliquot(sampleType, container,
            pv, 2, 3);
        sample.setInventoryId(Utils.getRandomString(5));
        sample.persist();
        AliquotHelper.addAliquot(sampleType, null, pv, null, null);

        DebugUtil.getRandomSamplesAlreadyLinked(appService, siteId);
        DebugUtil.getRandomSamplesAlreadyAssigned(appService, siteId);
        DebugUtil.getRandomSamplesNotAssigned(appService, siteId);
    }
}
