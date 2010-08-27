package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

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
import edu.ualberta.med.biobank.model.DispatchShipment;
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

        Assert.assertTrue(container1.compareTo(container2) > 0);
        Assert.assertTrue(container2.compareTo(container1) < 0);

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

    // FIXME copied from dispatch container
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

        int countAfter = appService.search(DispatchShipment.class,
            new DispatchShipment()).size();

        Assert.assertEquals(countBefore - 1, countAfter);

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
