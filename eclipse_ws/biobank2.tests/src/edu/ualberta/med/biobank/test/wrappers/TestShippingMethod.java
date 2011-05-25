package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.OriginInfoHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
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

        List<SpecimenTypeWrapper> spcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        SpecimenWrapper parentSpc = SpecimenHelper.newSpecimen(DbHelper
            .chooseRandomlyInList(spcTypes));
        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(site, patient1, 1, parentSpc);
        parentSpc = cevent1.getOriginalSpecimenCollection(false).get(0);
        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.addShipmentInfo(site,
            method, TestCommon.getNewWaybill(r), Utils.getRandomDate(),
            parentSpc);

        method.reload();
        try {
            method.delete();
            Assert.fail("one cevent in the collection");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        shipInfo.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
            appService).get(0));
        shipInfo.persist();
        method.reload();
        method.delete();
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
        String[] names = new String[] { "testIsUsed1_" + r.nextInt(),
            "testIsUsed2_" + r.nextInt() };
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
        ClinicWrapper clinic1 = ClinicHelper.addClinic("clinic1" + name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic("clinic2" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name);
        study.addToContactCollection(Arrays.asList(contact1, contact2));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        List<SpecimenTypeWrapper> spcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        SpecimenWrapper parentSpc1 = SpecimenHelper.newSpecimen(DbHelper
            .chooseRandomlyInList(spcTypes));
        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(clinic1, patient1, 1, parentSpc1);
        parentSpc1 = cevent1.getOriginalSpecimenCollection(false).get(0);
        String waybill = "waybill_" + name;
        ShipmentInfoWrapper shipInfo1 = ShipmentInfoHelper.addShipmentInfo(clinic1,
            methods[0], waybill, Utils.getRandomDate(), parentSpc1);

        Assert.assertTrue(methods[0].isUsed());
        Assert.assertFalse(methods[1].isUsed());

        SpecimenWrapper parentSpc2 = SpecimenHelper.newSpecimen(DbHelper
            .chooseRandomlyInList(spcTypes));
        CollectionEventWrapper cevent2 = CollectionEventHelper
            .addCollectionEvent(clinic2, patient1, 2, parentSpc2);
        parentSpc1 = cevent2.getOriginalSpecimenCollection(false).get(0);
        ShipmentInfoWrapper shipInfo2 = ShipmentInfoHelper.addShipmentInfo(clinic2,
            methods[1], waybill, Utils.getRandomDate(), parentSpc2);

        methods[0].reload();
        methods[1].reload();
        Assert.assertTrue(methods[0].isUsed());
        Assert.assertTrue(methods[1].isUsed());

        shipInfo1.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
            appService).get(0));
        shipInfo1.persist();

        methods[0].reload();
        methods[1].reload();
        Assert.assertFalse(methods[0].isUsed());
        Assert.assertTrue(methods[1].isUsed());

        shipInfo2.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
            appService).get(0));
        shipInfo2.persist();

        methods[0].reload();
        methods[1].reload();
        Assert.assertFalse(methods[0].isUsed());
        Assert.assertFalse(methods[1].isUsed());
    }
}
