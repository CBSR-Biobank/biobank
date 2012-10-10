package edu.ualberta.med.biobank.test.wrappers;

import edu.ualberta.med.biobank.test.TestDatabase;

@Deprecated
public class TestOriginInfo extends TestDatabase {
    //
    // @Test
    // public void testGetSetShippingMethod() throws Exception {
    // String name = "testGetSetShippingMethod" + r.nextInt();
    // ClinicWrapper clinic = ClinicHelper.addClinic("clinic1" + name);
    // StudyWrapper study = StudyHelper.addStudy(name);
    // PatientWrapper patient = PatientHelper.addPatient(name, study);
    // SpecimenWrapper spc = SpecimenHelper.newSpecimen(name);
    //
    // ShippingMethodWrapper method = ShippingMethodHelper
    // .addShippingMethod(name);
    //
    // CollectionEventHelper.addCollectionEvent(clinic, patient, 1, spc);
    // ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.addShipmentInfo(
    // clinic, method, Utils.getRandomString(20), Utils.getRandomDate(),
    // spc);
    //
    // Assert.assertEquals(method, shipInfo.getShippingMethod());
    // }
    //
    // @Test
    // public void testGetShipmentsByDateReceived() throws Exception {
    // String name = "testGetShipmentsByDateReceived" + r.nextInt();
    // ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
    //
    // OriginInfoWrapper oi = OriginInfoHelper.newOriginInfo(clinic);
    // Date dateReceived = Utils.getRandomDate();
    // ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.newShipmentInfo(
    // clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0),
    // Utils.getRandomString(20), dateReceived);
    // oi.setShipmentInfo(shipInfo);
    // oi.persist();
    //
    // List<OriginInfoWrapper> res = OriginInfoWrapper
    // .getShipmentsByDateReceived(appService, dateReceived, clinic);
    // Assert.assertEquals(1, res.size());
    // Assert.assertEquals(oi, res.get(0));
    //
    // res = OriginInfoWrapper.getShipmentsByDateReceived(appService,
    // Utils.getRandomDate(), clinic);
    // Assert.assertEquals(0, res.size());
    // }
    //
    // @Test
    // public void testGetTodayShipments() throws Exception {
    // String name = "testGetTodayShipments" + r.nextInt();
    // ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
    //
    // OriginInfoWrapper oi = OriginInfoHelper.newOriginInfo(clinic);
    // ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.newShipmentInfo(
    // clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0),
    // Utils.getRandomString(20), new Date());
    // oi.setShipmentInfo(shipInfo);
    // oi.persist();
    //
    // List<OriginInfoWrapper> res = OriginInfoWrapper
    // .getShipmentsByDateReceived(appService, new Date(), clinic);
    // Assert.assertEquals(1, res.size());
    // Assert.assertEquals(oi, res.get(0));
    //
    // res = OriginInfoWrapper.getShipmentsByDateReceived(appService,
    // Utils.getRandomDate(), clinic);
    // Assert.assertEquals(0, res.size());
    // }
    //
    // @Test
    // public void testGetPatientCollection() throws Exception {
    // Assert.fail("to be implemented");
    // }
    //
    // @Test
    // public void testDeleteDepenciesCheck() throws Exception {
    // Assert.fail("to be implemented");
    // }
    //
    // @Test
    // public void testPersistChecks() throws Exception {
    // Assert.fail("to be implemented");
    // }
    //
    // @Test
    // public void testGetShipmentsByWaybill() throws Exception {
    // Assert.fail("to be implemented");
    // }
    //
    // @Test
    // public void testGetShipmentsByDateSent() throws Exception {
    // Assert.fail("to be implemented");
    // }
    //
    // @Test
    // public void testGetSecuritySpecificCenter() throws Exception {
    // Assert.fail("to be implemented");
    // // ?????. need user ?
    // }
    //
    // @Test
    // public void testGetLog() throws Exception {
    // Assert.fail("to be implemented");
    // // need to persist or update with a shipmentinfo
    // }
}
