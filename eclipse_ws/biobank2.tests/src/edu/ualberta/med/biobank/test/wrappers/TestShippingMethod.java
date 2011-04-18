package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.OriginInfoHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestShippingMethod extends TestDatabase {
    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();

        ShippingMethodWrapper method = ShippingMethodHelper
            .addShippingMethod(name);
        testGettersAndSetters(method);
    }

    @Test
    public void testGetShipmentCollection() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        SpecimenTypeWrapper specimenType = SpecimenTypeHelper
            .addSpecimenType("shipST");

        ShippingMethodWrapper method1 = ShippingMethodHelper
            .addShippingMethod(name);
        ShippingMethodWrapper method2 = ShippingMethodHelper
            .addShippingMethod(name + "_2");

        ShipmentInfoWrapper ship1 = new ShipmentInfoWrapper(appService,
            new ShipmentInfo());
        ship1.setShippingMethod(method1);
        OriginInfoWrapper oi1 = OriginInfoHelper.addOriginInfo(site);
        oi1.setShipmentInfo(ship1);

        ShipmentInfoWrapper ship2 = new ShipmentInfoWrapper(appService,
            new ShipmentInfo());
        ship2.setShippingMethod(method2);
        OriginInfoWrapper oi2 = OriginInfoHelper.addOriginInfo(site);
        oi2.setShipmentInfo(ship2);

        ShipmentInfoWrapper ship3 = new ShipmentInfoWrapper(appService,
            new ShipmentInfo());
        ship3.setShippingMethod(method2);
        OriginInfoWrapper oi3 = OriginInfoHelper.addOriginInfo(site);
        oi3.setShipmentInfo(ship3);

        oi1.persist();
        oi2.persist();
        oi3.persist();

        Assert.assertEquals(1,
            ShipmentInfoWrapper
                .getAllShipmentInfosByMethod(appService, method1).size());
        Assert.assertEquals(2,
            ShipmentInfoWrapper
                .getAllShipmentInfosByMethod(appService, method2).size());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGetShipmentCollectionBoolean() throws Exception {
        String name = "testGetShipmentCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ShippingMethodWrapper method = ShippingMethodHelper
            .addShippingMethod(name);

        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent1.setWaybill("QWERTY" + name);
        // cevent1.persist();
        // CollectionEventWrapper cevent2 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent1.setWaybill("ASDFG" + name);
        // cevent2.persist();
        // CollectionEventWrapper cevent3 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent1.setWaybill("ghrtghd" + name);
        // cevent3.persist();
        //
        // method.reload();
        // List<CollectionEventWrapper> shipments = method
        // .getCollectionEventCollection(true);
        // if (shipments.size() > 1) {
        // for (int i = 0; i < shipments.size() - 1; i++) {
        // ShipmentInfoWrapper s1 = shipments.get(i);
        // ShipmentInfoWrapper s2 = shipments.get(i + 1);
        // Assert.assertTrue(s1.compareTo(s2) <= 0);
        // }
        // }
    }

    @Test
    public void testGetShippingMethods() throws Exception {
        String name = "testGetShippingMethods" + r.nextInt();
        int sizeBefore = ShippingMethodWrapper.getShippingMethods(appService)
            .size();

        ShippingMethodHelper.addShippingMethod(name);
        ShippingMethodHelper.addShippingMethod(name + "_2");

        int sizeAfter = ShippingMethodWrapper.getShippingMethods(appService)
            .size();

        Assert.assertEquals(sizeBefore + 2, sizeAfter);
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        ShippingMethodWrapper method = ShippingMethodHelper
            .newShippingMethod(name);
        method.persist();
        ShippingMethodHelper.createdShipMethods.add(method);

        ShippingMethod shipComp = new ShippingMethod();
        shipComp.setId(method.getId());
        Assert.assertEquals(1, appService
            .search(ShippingMethod.class, shipComp).size());

        ShippingMethodWrapper sm;

        // add 5 shipping methods that will eventually be deleted
        int before = ShippingMethodWrapper.getShippingMethods(appService)
            .size();
        List<ShippingMethodWrapper> toDelete = new ArrayList<ShippingMethodWrapper>();
        for (int i = 0; i < 5; ++i) {
            name = "testPersist" + i + r.nextInt();
            sm = new ShippingMethodWrapper(appService);
            sm.setName(name);
            sm.persist();
            sm.reload();
            toDelete.add(sm);
        }

        List<ShippingMethodWrapper> shipMethods = ShippingMethodWrapper
            .getShippingMethods(appService);
        int after = shipMethods.size();
        Assert.assertEquals(before + 5, after);
        Assert.assertTrue(shipMethods.containsAll(toDelete));

        // create 3 new shipping methods
        before = after;
        List<ShippingMethodWrapper> toAdd = new ArrayList<ShippingMethodWrapper>();
        for (int i = 0; i < 3; ++i) {
            name = "testPersist" + i + r.nextInt();
            sm = new ShippingMethodWrapper(appService);
            sm.setName(name);
            toAdd.add(sm);
        }
        ShippingMethodHelper.createdShipMethods.addAll(toAdd);

        // now delete the ones previously added and add the new ones
        ShippingMethodWrapper.persistShippingMethods(toAdd, toDelete);

        shipMethods = ShippingMethodWrapper.getShippingMethods(appService);
        after = shipMethods.size();
        Assert.assertEquals(before - 5 + 3, after);
        Assert.assertTrue(shipMethods.containsAll(toAdd));
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ShippingMethodWrapper method = ShippingMethodHelper.addShippingMethod(
            name, false);

        ShippingMethod shipComp = new ShippingMethod();
        shipComp.setId(method.getId());
        Assert.assertEquals(1, appService
            .search(ShippingMethod.class, shipComp).size());

        method.delete();

        Assert.assertEquals(0, appService
            .search(ShippingMethod.class, shipComp).size());
    }

    @Test
    public void testDeleteFailNoShipments() throws Exception {
        String name = "testDeleteFailNoShipments" + r.nextInt();
        ShippingMethodWrapper method = ShippingMethodHelper.addShippingMethod(
            name, false);

        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent1.persist();
        // method.reload();
        //
        // try {
        // method.delete();
        // Assert.fail("one cevent in the collection");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
        //
        // cevent1.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
        // appService).get(0));
        // cevent1.persist();
        // method.reload();
        // method.delete();
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ShippingMethodWrapper method = ShippingMethodHelper
            .addShippingMethod(name);
        method.setName("QQQQ");
        method.reset();
        Assert.assertEquals(name, method.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetNew" + r.nextInt();
        ShippingMethodWrapper method = ShippingMethodHelper
            .newShippingMethod(name);
        method.setName("QQQQ");
        method.reset();
        Assert.assertEquals(null, method.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        ShippingMethodWrapper method1 = ShippingMethodHelper
            .addShippingMethod("QWERTY" + name);
        ShippingMethodWrapper method2 = ShippingMethodHelper
            .addShippingMethod("ASDFG" + name);
        Assert.assertTrue(method1.compareTo(method2) > 0);
        Assert.assertTrue(method2.compareTo(method1) < 0);
    }

    @Test
    public void testIsUsed() throws Exception {
        String[] names = new String[] { "testIsUsed1", "testIsUsed2" };
        ShippingMethodWrapper[] methods = new ShippingMethodWrapper[] { null,
            null };

        int count = 0;
        for (String name : names) {
            ShippingMethodWrapper method = ShippingMethodHelper
                .addShippingMethod(name);
            method.setName("QQQQ");
            method.reset();
            Assert.assertEquals(name, method.getName());
            methods[count] = method;
            count++;
        }

        Assert.assertFalse(methods[0].isUsed());
        Assert.assertFalse(methods[1].isUsed());

        String name = "testIsUsed" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // methods[0],
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent1.setWaybill("QWERTY" + name);
        // cevent1.persist();
        //
        // Assert.assertTrue(methods[0].isUsed());
        // Assert.assertFalse(methods[1].isUsed());
        //
        // CollectionEventWrapper cevent2 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // methods[1],
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent2.setWaybill(name + "QWERTY");
        // cevent2.persist();
        //
        // Assert.assertTrue(methods[0].isUsed());
        // Assert.assertTrue(methods[1].isUsed());
        //
        // DbHelper.deleteFromList(cevent1.getSourceVesselCollection(false));
        // cevent1.delete();
        //
        // Assert.assertFalse(methods[0].isUsed());
        // Assert.assertTrue(methods[1].isUsed());
        //
        // DbHelper.deleteFromList(cevent2.getSourceVesselCollection(false));
        // cevent2.delete();
        //
        // Assert.assertFalse(methods[0].isUsed());
        // Assert.assertFalse(methods[1].isUsed());
    }
}
