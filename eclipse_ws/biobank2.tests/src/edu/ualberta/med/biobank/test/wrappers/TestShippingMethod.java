package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AbstractShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ShippingMethodWrapper method1 = ShippingMethodHelper
            .addShippingMethod(name);
        ShippingMethodWrapper method2 = ShippingMethodHelper
            .addShippingMethod(name + "_2");

        ClinicShipmentWrapper shipment1 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment1.setShippingMethod(method1);
        shipment1.persist();
        ClinicShipmentWrapper shipment2 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment2.setShippingMethod(method2);
        shipment2.persist();
        ClinicShipmentWrapper shipment3 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment3.setShippingMethod(method2);
        shipment3.persist();

        method1.reload();
        method2.reload();
        Assert.assertEquals(1, method1.getShipmentCollection().size());
        Assert.assertEquals(2, method2.getShipmentCollection().size());
    }

    @Test
    public void testGetShipmentCollectionBoolean() throws Exception {
        String name = "testGetShipmentCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ShippingMethodWrapper method = ShippingMethodHelper
            .addShippingMethod(name);

        ClinicShipmentWrapper shipment1 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment1.setShippingMethod(method);
        shipment1.setWaybill("QWERTY" + name);
        shipment1.persist();
        ClinicShipmentWrapper shipment2 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment2.setShippingMethod(method);
        shipment1.setWaybill("ASDFG" + name);
        shipment2.persist();
        ClinicShipmentWrapper shipment3 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment3.setShippingMethod(method);
        shipment1.setWaybill("ghrtghd" + name);
        shipment3.persist();

        method.reload();
        // FIXME we should have a method in ShippingMethodWrapper to retrieve
        // shipments of type ClinicShipment and not all of them
        List<AbstractShipmentWrapper> shipments = method
            .getShipmentCollection(true);
        if (shipments.size() > 1) {
            for (int i = 0; i < shipments.size() - 1; i++) {
                AbstractShipmentWrapper s1 = shipments.get(i);
                AbstractShipmentWrapper s2 = shipments.get(i + 1);
                Assert.assertTrue(s1.compareTo(s2) <= 0);
            }
        }
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
        ShippingMethodHelper.createdCompanies.add(method);

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

        List<ShippingMethodWrapper> statuses = ShippingMethodWrapper
            .getShippingMethods(appService);
        int after = statuses.size();
        Assert.assertEquals(before + 5, after);
        Assert.assertTrue(statuses.containsAll(toDelete));

        // create 3 new shipping methods
        before = after;
        List<ShippingMethodWrapper> toAdd = new ArrayList<ShippingMethodWrapper>();
        for (int i = 0; i < 3; ++i) {
            name = "testPersist" + i + r.nextInt();
            sm = new ShippingMethodWrapper(appService);
            sm.setName(name);
            toAdd.add(sm);
        }

        ShippingMethodWrapper.persistShippingMethods(toAdd, toDelete);

        // now delete the ones previously added and add the new ones
        statuses = ShippingMethodWrapper.getShippingMethods(appService);
        after = statuses.size();
        Assert.assertEquals(before - 5 + 3, after);
        Assert.assertTrue(statuses.containsAll(toAdd));
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

        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ClinicShipmentWrapper shipment1 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment1.setShippingMethod(method);
        shipment1.persist();
        method.reload();

        try {
            method.delete();
            Assert.fail("one shipment in the collection");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        shipment1.setShippingMethod(null);
        shipment1.persist();
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ClinicShipmentWrapper shipment1 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment1.setShippingMethod(methods[0]);
        shipment1.setWaybill("QWERTY" + name);
        shipment1.persist();

        Assert.assertTrue(methods[0].isUsed());
        Assert.assertFalse(methods[1].isUsed());

        ClinicShipmentWrapper shipment2 = ShipmentHelper.addShipment(site,
            clinic, patient1);
        shipment2.setShippingMethod(methods[1]);
        shipment2.setWaybill(name + "QWERTY");
        shipment2.persist();

        Assert.assertTrue(methods[0].isUsed());
        Assert.assertTrue(methods[1].isUsed());

        shipment1.delete();

        Assert.assertFalse(methods[0].isUsed());
        Assert.assertTrue(methods[1].isUsed());

        shipment2.delete();

        Assert.assertFalse(methods[0].isUsed());
        Assert.assertFalse(methods[1].isUsed());
    }

}
