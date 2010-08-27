package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DispatchContainerHelper;
import edu.ualberta.med.biobank.test.internal.DispatchInfoHelper;
import edu.ualberta.med.biobank.test.internal.DispatchShipmentHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestDispatchContainer extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        ContainerTypeWrapper containerType = ContainerTypeHelper
            .addContainerTypeRandom(senderSite, name, false);
        DispatchContainerWrapper container = DispatchContainerHelper
            .addContainer(name, shipment, containerType);

        testGettersAndSetters(container);
    }

    @Test
    public void testCompatreTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        DispatchContainerWrapper container1 = DispatchContainerHelper
            .addContainer(name + "_c1", shipment, type);

        DispatchContainerWrapper container2 = DispatchContainerHelper
            .addContainer(name + "_c2", shipment, type);

        Assert.assertTrue(container1.compareTo(container2) < 0);
        Assert.assertTrue(container2.compareTo(container1) > 0);

        Assert.assertTrue(container1.compareTo(null) == 0);
        Assert.assertTrue(container2.compareTo(null) == 0);
    }

    @Test
    public void testReset() throws Exception {
        String name = "testReset" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        // test reset for a new object
        DispatchContainerWrapper container = DispatchContainerHelper
            .newContainer(name, shipment, type);
        container.reset();

        // test reset for an object already in database
        container = DispatchContainerHelper.newContainer(name, shipment, type);
        container.reset();
    }

    @Test
    public void testReload() throws Exception {
        String name = "testReload" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        DispatchContainerWrapper container = DispatchContainerHelper
            .addContainer(name, shipment, type);

        try {
            container.reload();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("cannot reload container");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        String name = "testGetWrappedClass" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);
        DispatchContainerWrapper container = DispatchContainerHelper
            .addContainer(name, shipment, type);
        Assert.assertEquals(DispatchContainer.class,
            container.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);
        DispatchContainerWrapper container = DispatchContainerHelper
            .addContainer(name, shipment, type);

        int countBefore = appService.search(DispatchContainer.class,
            new DispatchContainer()).size();

        container.delete();

        int countAfter = appService.search(DispatchContainer.class,
            new DispatchContainer()).size();

        Assert.assertEquals(countBefore - 1, countAfter);

    }

    @Test
    public void testPersist() throws Exception {
        String name = "testGetSetShipment" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        // don't add a shipment yet
        DispatchContainerWrapper container = DispatchContainerHelper
            .newContainer(name, null, type);

        try {
            container.persist();
            Assert
                .fail("should not be allowed to persist a dispatch container without a shipment: ");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        container = DispatchContainerHelper.newContainer(null, shipment, type);

        try {
            container.persist();
            Assert
                .fail("should not be allowed to persist a dispatch container without a product barcode: ");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testGetSetShipment() throws Exception {
        String name = "testGetSetShipment" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        // don't add a shipment yet
        DispatchContainerWrapper container = DispatchContainerHelper
            .newContainer(name, null, type);

        Assert.assertNull(container.getShipment());

        // add the shipment
        container.setShipment(shipment);
        container.persist();
        container.reload();

        Assert.assertEquals(shipment, container.getShipment());

        DispatchContainer rawContainer = new DispatchContainer();
        rawContainer.setId(container.getId());
        container = new DispatchContainerWrapper(appService,
            (DispatchContainer) appService.search(DispatchContainer.class,
                rawContainer).get(0));

        Assert.assertEquals(shipment, container.getShipment());

        // delete the shipment
        shipment.delete();
        container.reload();

        Assert.assertNull(container.getShipment());
    }

    @Test
    public void testGetSetSite() throws Exception {
        String name = "testGetSetShipment" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            senderSite, name, false);

        // test with null shipment
        DispatchContainerWrapper container = DispatchContainerHelper
            .newContainer(name, null, type);

        Assert.assertNull(container.getSite());

        // test with shipment
        container = DispatchContainerHelper.addContainer(name, shipment, type);

        Assert.assertEquals(senderSite, container.getSite());
    }

    private List<AliquotWrapper> addRandomAliquots(String name,
        PatientVisitWrapper pv, int num) throws Exception {
        AliquotWrapper aliquot;
        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (int i = 0; i < num; ++i) {

            aliquot = new AliquotWrapper(appService);
            aliquot.setSampleType(SampleTypeWrapper.getAllSampleTypes(
                appService, false).get(0));
            // aliquot.setInventoryId(name + "_a" + i);
            aliquot.setPatientVisit(pv);
            aliquot.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            aliquot.persist();
            aliquots.add(aliquot);
        }
        return aliquots;
    }

    @Test
    public void testGetAddAliquots() throws Exception {
        String name = "testGetSetAliquots" + r.nextInt();

        SiteWrapper sender = SiteHelper.addSite(name + "_SENDER");
        SiteWrapper receiver = SiteHelper.addSite(name + "_RECEIVER");

        StudyWrapper study = StudyHelper.addStudy(name);

        DispatchInfoHelper.addInfo(study, sender, receiver);

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        ClinicWrapper clinic = ClinicHelper.addClinic(name);

        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.addContacts(Arrays.asList(contact));
        study.persist();

        ClinicShipmentWrapper clinicShipment = ClinicShipmentHelper
            .newShipment(sender, clinic);
        clinicShipment.addPatients(Arrays.asList(patient));
        clinicShipment.persist();

        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            clinicShipment, new Date(), new Date());

        List<AliquotWrapper> aliquotSet1 = addRandomAliquots(name, pv,
            r.nextInt(10) + 1);
        List<AliquotWrapper> aliquotSet2 = addRandomAliquots(name, pv,
            r.nextInt(10) + 1);

        DispatchShipmentWrapper dispShipment = DispatchShipmentHelper
            .addShipment(sender, receiver, name, Utils.getRandomDate());

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(
            sender, name, name, 1, 8, 12, false);

        DispatchContainerWrapper dispContainer = DispatchContainerHelper
            .newContainer(name, dispShipment, type);
        DispatchContainerHelper.addAliquots(dispContainer, aliquotSet1, 0, 0);

        // add aliquots at non empty positions
        try {
            DispatchContainerHelper.addAliquots(dispContainer, aliquotSet1, 0,
                0);
            Assert.fail("failed adding aliquots in non empty positions");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        Collection<AliquotWrapper> dispContainerAliquots = dispContainer
            .getAliquots().values();
        Assert.assertEquals(aliquotSet1.size(), dispContainerAliquots.size());
        Assert.assertTrue(dispContainerAliquots.containsAll(aliquotSet1));

        DispatchContainerHelper.addAliquots(dispContainer, aliquotSet2, 3, 0);

        Assert.assertEquals(aliquotSet1.size() + aliquotSet2.size(),
            dispContainer.getAliquots().size());
        Assert.assertTrue(dispContainerAliquots.containsAll(aliquotSet2));

        for (AliquotWrapper aliquot : aliquotSet1) {
            aliquot.delete();
        }

        // make sure first aliquot not there anymore
        Assert.assertNull(dispContainer.getAliquot(0, 0));

        Assert.assertEquals(aliquotSet2.size(), dispContainer.getAliquots()
            .size());
        Assert.assertTrue(dispContainerAliquots.containsAll(aliquotSet2));
    }

    @Test
    public void testDispatchContainerCascade() throws Exception {
        String name = "testDispatchContainerCascade" + r.nextInt();

        SiteWrapper sender = SiteHelper.addSite(name + "_SENDER");
        SiteWrapper receiver = SiteHelper.addSite(name + "_RECEIVER");

        StudyWrapper study = StudyHelper.addStudy(name);

        DispatchInfoHelper.addInfo(study, sender, receiver);

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        ClinicWrapper clinic = ClinicHelper.addClinic(name);

        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.addContacts(Arrays.asList(contact));
        study.persist();

        ClinicShipmentWrapper shipment = ClinicShipmentHelper.newShipment(
            sender, clinic);
        shipment.addPatients(Arrays.asList(patient));
        shipment.persist();

        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            shipment, new Date(), new Date());

        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(SampleTypeWrapper.getAllSampleTypes(appService,
            false).get(0));
        aliquot.setInventoryId(name);
        aliquot.setPatientVisit(pv);
        aliquot.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        aliquot.persist();

        DispatchShipmentWrapper ship = new DispatchShipmentWrapper(appService);
        ship.setReceiver(receiver);
        ship.setSender(sender);
        ship.persist();

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(
            sender, name + "_shipping", name + "ship", 1, 8, 12, false);

        DispatchContainerWrapper cont = new DispatchContainerWrapper(appService);
        cont.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        cont.setContainerType(type);
        cont.setShipment(ship);
        cont.setProductBarcode(name);
        cont.persist();

        cont.addAliquot(0, 0, aliquot);
        cont.persist();

        cont.reload();
        Assert.assertEquals(1, cont.getAliquots().size());
    }

}
