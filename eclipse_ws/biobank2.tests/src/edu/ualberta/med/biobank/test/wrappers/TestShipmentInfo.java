package edu.ualberta.med.biobank.test.wrappers;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestShipmentInfo extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SpecimenWrapper spc = SpecimenHelper.addParentSpecimen();

        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.addShipment(site,
            ShippingMethodWrapper.getShippingMethods(appService).get(0), spc);
        testGettersAndSetters(shipInfo);
    }

    @Test
    public void testReceivedToday() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SpecimenWrapper spc = SpecimenHelper.addParentSpecimen();

        Date dateNow = new Date();

        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.addShipment(site,
            ShippingMethodWrapper.getShippingMethods(appService).get(0),
            TestCommon.getNewWaybill(r), dateNow, spc);

        Assert.assertTrue(shipInfo.isReceivedToday());

        // set date to 1 day in future
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateNow);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        shipInfo.setReceivedAt(cal.getTime());
        Assert.assertFalse(shipInfo.isReceivedToday());

        // set date to 1 day ago
        cal = Calendar.getInstance();
        cal.setTime(dateNow);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        shipInfo.setReceivedAt(cal.getTime());
        Assert.assertFalse(shipInfo.isReceivedToday());
    }

    @Test
    public void testTemp() throws Exception {
        String name = "testPersistFailWaybillNull" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name + "_study");
        ClinicWrapper clinic = ClinicHelper.addClinicWithShipments(name
            + "_clinic");
        PatientWrapper patient = PatientHelper.addPatient(name + "_p", study);
        SpecimenWrapper spc = SpecimenHelper.newSpecimen(name + "_spc");

        CollectionEventHelper.newCollectionEvent(clinic, patient, 1, spc);

        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(appService);
        shipInfo.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
            appService).get(0));
        shipInfo.setPackedAt(Utils.getRandomDate());
        shipInfo.setReceivedAt(Utils.getRandomDate());
        spc.getOriginInfo().setShipmentInfo(shipInfo);

        try {
            shipInfo.persist();
            Assert.fail("cevent with waybill null");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

}
