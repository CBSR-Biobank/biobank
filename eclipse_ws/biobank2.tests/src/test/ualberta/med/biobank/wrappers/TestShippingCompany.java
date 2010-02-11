package test.ualberta.med.biobank.wrappers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import test.ualberta.med.biobank.TestDatabase;
import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShippingCompanyHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ShippingCompany;

public class TestShippingCompany extends TestDatabase {
    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();

        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name);
        testGettersAndSetters(company);
    }

    @Test
    public void testGetShipmentCollection() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ShippingCompanyWrapper company1 = ShippingCompanyHelper
            .addShippingCompany(name);
        ShippingCompanyWrapper company2 = ShippingCompanyHelper
            .addShippingCompany(name + "_2");

        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment1.setShippingCompany(company1);
        shipment1.persist();
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment2.setShippingCompany(company2);
        shipment2.persist();
        ShipmentWrapper shipment3 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment3.setShippingCompany(company2);
        shipment3.persist();

        company1.reload();
        company2.reload();
        Assert.assertEquals(1, company1.getShipmentCollection().size());
        Assert.assertEquals(2, company2.getShipmentCollection().size());
    }

    @Test
    public void testGetShipmentCollectionBoolean() throws Exception {
        String name = "testGetShipmentCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name);

        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment1.setShippingCompany(company);
        shipment1.setWaybill("QWERTY" + name);
        shipment1.persist();
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment2.setShippingCompany(company);
        shipment1.setWaybill("ASDFG" + name);
        shipment2.persist();
        ShipmentWrapper shipment3 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment3.setShippingCompany(company);
        shipment1.setWaybill("ghrtghd" + name);
        shipment3.persist();

        company.reload();
        List<ShipmentWrapper> shipments = company.getShipmentCollection(true);
        if (shipments.size() > 1) {
            for (int i = 0; i < shipments.size() - 1; i++) {
                ShipmentWrapper s1 = shipments.get(i);
                ShipmentWrapper s2 = shipments.get(i + 1);
                Assert.assertTrue(s1.compareTo(s2) <= 0);
            }
        }
    }

    @Test
    public void testGetShippingCompanies() throws Exception {
        String name = "testGetShippingCompanies" + r.nextInt();
        int sizeBefore = ShippingCompanyWrapper
            .getShippingCompanies(appService).size();

        ShippingCompanyHelper.addShippingCompany(name);
        ShippingCompanyHelper.addShippingCompany(name + "_2");

        int sizeAfter = ShippingCompanyWrapper.getShippingCompanies(appService)
            .size();

        Assert.assertEquals(sizeBefore + 2, sizeAfter);
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .newShippingCompany(name);
        company.persist();
        ShippingCompanyHelper.createdCompanies.add(company);

        ShippingCompany shipComp = new ShippingCompany();
        shipComp.setId(company.getId());
        Assert.assertEquals(1, appService.search(ShippingCompany.class,
            shipComp).size());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name, false);

        ShippingCompany shipComp = new ShippingCompany();
        shipComp.setId(company.getId());
        Assert.assertEquals(1, appService.search(ShippingCompany.class,
            shipComp).size());

        company.delete();

        Assert.assertEquals(0, appService.search(ShippingCompany.class,
            shipComp).size());
    }

    @Test
    public void testDeleteFailNoShipments() throws Exception {
        String name = "testDeleteFailNoShipments" + r.nextInt();
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name, false);

        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment1.setShippingCompany(company);
        shipment1.persist();
        company.reload();

        try {
            company.delete();
            Assert.fail("one shipment in the collection");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        shipment1.setShippingCompany(null);
        shipment1.persist();
        company.reload();
        company.delete();
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name);
        company.setName("QQQQ");
        company.reset();
        Assert.assertEquals(name, company.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetNew" + r.nextInt();
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .newShippingCompany(name);
        company.setName("QQQQ");
        company.reset();
        Assert.assertEquals(null, company.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        ShippingCompanyWrapper company1 = ShippingCompanyHelper
            .addShippingCompany("QWERTY" + name);
        ShippingCompanyWrapper company2 = ShippingCompanyHelper
            .addShippingCompany("ASDFG" + name);
        Assert.assertTrue(company1.compareTo(company2) > 0);
        Assert.assertTrue(company2.compareTo(company1) < 0);
    }

}
