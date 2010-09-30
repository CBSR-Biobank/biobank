package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper.STATE;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DispatchInfoHelper;
import edu.ualberta.med.biobank.test.internal.DispatchShipmentHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestDispatchShipment extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);

        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        testGettersAndSetters(shipment);
    }

    @Test
    public void testConstructor() throws Exception {
        DispatchShipment shipmentRaw = new DispatchShipment();
        DispatchShipmentWrapper shipment =
            new DispatchShipmentWrapper(appService, shipmentRaw);
        Assert.assertNotNull(shipment);
    }

    @Test
    public void testGetSetSender() throws Exception {
        String name = "testGetSetSender" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);

        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.newShipment(null, receiverSite, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        Assert.assertNull(shipment.getSender());

        shipment.setSender(senderSite);
        shipment.persist();

        Assert.assertEquals(senderSite, shipment.getSender());

        DispatchShipmentWrapper shipment2 =
            new DispatchShipmentWrapper(appService, shipment.getWrappedObject());

        Assert.assertEquals(senderSite, shipment2.getSender());

    }

    @Test
    public void testGetSetReceiver() throws Exception {
        String name = "testGetSetReceiver" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);

        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.newShipment(senderSite, null, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        Assert.assertNull(shipment.getReceiver());

        shipment.setReceiver(receiverSite);
        shipment.persist();

        Assert.assertEquals(receiverSite, shipment.getReceiver());

        DispatchShipmentWrapper shipment2 =
            new DispatchShipmentWrapper(appService, shipment.getWrappedObject());

        Assert.assertEquals(receiverSite, shipment2.getReceiver());
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper senderSite2 = SiteHelper.addSite(name + "_sender2");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        SiteWrapper receiverSite2 = SiteHelper.addSite(name + "_receiver2");

        StudyWrapper study = StudyHelper.addStudy(name);
        StudyWrapper study2 = StudyHelper.addStudy(name + "_study2");

        DispatchInfoHelper.addInfo(study, senderSite, receiverSite,
            receiverSite2);
        DispatchInfoHelper.addInfo(study2, senderSite2, receiverSite2);

        ShippingMethodWrapper method =
            ShippingMethodWrapper.getShippingMethods(appService).get(0);
        DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
            method, name, Utils.getRandomDate());

        // set waybill not unique for a shipment not yet database
        DispatchShipmentWrapper shipment2 =
            DispatchShipmentHelper.newShipment(senderSite, receiverSite, study,
                method, name, Utils.getRandomDate());

        try {
            shipment2.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a unique waybill: "
                    + shipment2.getWaybill());
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        shipment2.setWaybill(name + "2");
        shipment2.persist();

        // set waybill not unique for a shipment retrieved from database
        shipment2.setWaybill(name);
        try {
            shipment2.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a unique waybill: "
                    + shipment2.getWaybill());
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // set waybill to same for 2 different sending sites
        shipment2 =
            DispatchShipmentHelper.newShipment(senderSite2, receiverSite2,
                study2, method, name, Utils.getRandomDate());
        try {
            shipment2.persist();
            Assert.assertTrue(true);
        } catch (BiobankCheckException e) {
            Assert
                .fail("should be allowed to persist a dispatch shipment with a unique waybill");
        }

        // test no sender
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.newShipment(null, receiverSite, study,
                method, TestCommon.getNewWaybill(r), Utils.getRandomDate());
        try {
            shipment.persist();
            Assert
                .fail("should be allowed to persist a dispatch shipment without a sender");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // test no receiver
        shipment =
            DispatchShipmentHelper.newShipment(senderSite, null, study, method,
                TestCommon.getNewWaybill(r), Utils.getRandomDate());
        try {
            shipment.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a receiver");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // test sender can send to receiver
        shipment =
            DispatchShipmentHelper.newShipment(senderSite2, receiverSite,
                study, method, TestCommon.getNewWaybill(r),
                Utils.getRandomDate());
        try {
            shipment.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment where sender and receiver are not associated");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCompatreTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);

        ShippingMethodWrapper method =
            ShippingMethodWrapper.getShippingMethods(appService).get(0);

        DispatchShipmentWrapper shipment1 =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                method);
        shipment1.setDateReceived(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));

        DispatchShipmentWrapper shipment2 =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                method);
        shipment2.setDateReceived(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(shipment1.compareTo(shipment2) > 0);
        Assert.assertTrue(shipment2.compareTo(shipment1) < 0);

        Assert.assertTrue(shipment1.compareTo(null) == 0);
        Assert.assertTrue(shipment2.compareTo(null) == 0);
    }

    @Test
    public void testReset() throws Exception {
        String name = "testReset" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);

        ShippingMethodWrapper method =
            ShippingMethodWrapper.getShippingMethods(appService).get(0);

        // test reset for a new object
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.newShipment(senderSite, receiverSite, study,
                method, name, Utils.getRandomDate());

        shipment.reset();
        Assert.assertEquals(null, shipment.getWaybill());

        // test reset for an object already in database
        shipment =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                method, name, Utils.getRandomDate());
        shipment.setWaybill("QQQQ");
        shipment.reset();
        Assert.assertEquals(name, shipment.getWaybill());
    }

    @Test
    public void testReload() throws Exception {
        String name = "testReload" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name, Utils.getRandomDate());

        try {
            shipment.reload();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("cannot reload shipment");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.newShipment(null, null, null,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        Assert.assertEquals(DispatchShipment.class, shipment.getWrappedClass());
    }

    @Test
    public void testDetlete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        StudyWrapper study = StudyHelper.addStudy(name);
        DispatchInfoHelper.addInfo(study, senderSite, receiverSite);
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name, Utils.getRandomDate());

        int countBefore =
            appService.search(DispatchShipment.class, new DispatchShipment())
                .size();

        shipment.delete();

        int countAfter =
            appService.search(DispatchShipment.class, new DispatchShipment())
                .size();

        Assert.assertEquals(countBefore - 1, countAfter);
    }

    private List<AliquotWrapper> addAliquotsToContainerRow(
        PatientVisitWrapper visit, ContainerWrapper container, int row,
        List<SampleTypeWrapper> sampleTypes) throws Exception {
        int numSampletypes = sampleTypes.size();
        int colCapacity = container.getColCapacity();
        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (int i = 0; i < colCapacity; ++i) {
            aliquots.add(AliquotHelper.addAliquot(
                sampleTypes.get(r.nextInt(numSampletypes)),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, visit,
                row, i));
        }
        container.reload();
        visit.reload();
        return aliquots;
    }

    @Test
    public void testGetSetAliquotCollection() throws Exception {
        String name = "testGetSetAliquotCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        senderSite.addStudies(Arrays.asList(study));
        senderSite.persist();
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        receiverSite.addStudies(Arrays.asList(study));
        receiverSite.persist();

        senderSite.addStudyDispatchSites(study, Arrays.asList(receiverSite));
        senderSite.persist();
        senderSite.reload();
        DispatchShipmentWrapper shipment =
            DispatchShipmentHelper.addShipment(senderSite, receiverSite, study,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        List<SampleTypeWrapper> sampleTypes =
            SampleTypeWrapper.getAllSampleTypes(appService, false);
        ContainerTypeWrapper containerType =
            ContainerTypeHelper.addContainerType(senderSite, name, name, 1, 8,
                12, false);
        containerType.addSampleTypes(sampleTypes);
        containerType.persist();
        containerType.reload();
        ContainerTypeWrapper topContainerType =
            ContainerTypeHelper.addContainerTypeRandom(senderSite,
                name + "top", true);
        topContainerType.addChildContainerTypes(Arrays.asList(containerType));
        topContainerType.persist();
        topContainerType.reload();
        ContainerWrapper topContainer =
            ContainerHelper.addContainer(null, name + "top", null, senderSite,
                topContainerType);
        ContainerWrapper container =
            ContainerHelper.addContainer(null, name, topContainer, senderSite,
                containerType, 0, 0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        study.reload();
        ClinicShipmentWrapper clinicShipment =
            ClinicShipmentHelper.addShipment(senderSite, clinic,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                patient);
        PatientVisitWrapper visit =
            PatientVisitHelper.addPatientVisit(patient, clinicShipment,
                Utils.getRandomDate(), Utils.getRandomDate());

        List<AliquotWrapper> aliquotSet1 =
            addAliquotsToContainerRow(visit, container, 0, sampleTypes);
        List<AliquotWrapper> aliquotSet2 =
            addAliquotsToContainerRow(visit, container, 1, sampleTypes);

        shipment.addAliquots(aliquotSet1, STATE.NONE_STATE);
        shipment.persist();
        shipment.reload();

        List<AliquotWrapper> shipmentAliquots = shipment.getAliquotCollection();
        Assert.assertEquals(aliquotSet1.size(), shipmentAliquots.size());

        // add more aliquots to row 2

        shipment.addAliquots(aliquotSet2, STATE.NONE_STATE);
        shipment.persist();
        shipment.reload();

        shipmentAliquots = shipment.getAliquotCollection();
        Assert.assertEquals(aliquotSet1.size() + aliquotSet2.size(),
            shipmentAliquots.size());

        shipment.removeAliquots(aliquotSet1);
        shipment.persist();
        shipment.reload();

        shipmentAliquots = shipment.getAliquotCollection();
        Assert.assertEquals(aliquotSet2.size(), shipmentAliquots.size());
    }

}
