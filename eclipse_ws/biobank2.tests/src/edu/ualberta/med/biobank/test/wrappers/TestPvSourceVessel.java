package edu.ualberta.med.biobank.test.wrappers;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSourceVessel;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.PvSourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;

public class TestPvSourceVessel extends TestDatabase {

    private PvSourceVesselWrapper pvSourceVessel;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        ClinicWrapper clinic = ClinicHelper.addClinic("clinicname");
        ShipmentWrapper shipment = ShipmentHelper
            .addShipmentWithRandomPatient(site, clinic,
                Utils.getRandomString(10));
        PatientWrapper patient = shipment.getPatientCollection().get(0);
        PatientVisitWrapper pvw = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());

        pvSourceVessel = PvSourceVesselHelper.addPvSourceVessel(
            Utils.getRandomString(10), pvw);
    }

    @Test
    public void testGetSetSourceVessel() throws Exception {
        PvSourceVesselWrapper pvss = new PvSourceVesselWrapper(appService);
        Assert.assertNull(pvss.getSourceVessel());

        pvss.setSourceVessel(null);
        Assert.assertNull(pvss.getSourceVessel());

        SourceVesselWrapper oldSource = pvSourceVessel.getSourceVessel();
        String name = "testGetSetSourceVessel" + r.nextInt();
        SourceVesselWrapper newSourceVessel = SourceVesselHelper
            .addSourceVessel(name);

        pvSourceVessel.setSourceVessel(newSourceVessel);
        pvSourceVessel.persist();

        SourceVessel ss = ModelUtils.getObjectWithId(appService,
            SourceVessel.class, newSourceVessel.getId());
        Assert.assertTrue(ss != null);
        Assert.assertFalse(oldSource.equals(pvSourceVessel.getSourceVessel()));
        Assert.assertEquals(pvSourceVessel.getSourceVessel(), newSourceVessel);
    }

    @Test
    public void testGetSetPatientVisit() throws Exception {
        PvSourceVesselWrapper pvss = new PvSourceVesselWrapper(appService);
        Assert.assertNull(pvss.getPatientVisit());

        PatientVisitWrapper oldPv = pvSourceVessel.getPatientVisit();
        PatientVisitWrapper newPv = PatientVisitHelper.addPatientVisit(
            oldPv.getPatient(), oldPv.getShipment(), Utils.getRandomDate(),
            Utils.getRandomDate());
        pvSourceVessel.setPatientVisit(newPv);
        pvSourceVessel.persist();

        PatientVisit pv = ModelUtils.getObjectWithId(appService,
            PatientVisit.class, newPv.getId());
        Assert.assertTrue(pv != null);
        Assert.assertFalse(oldPv.equals(pvSourceVessel.getPatientVisit()));
        Assert.assertEquals(pvSourceVessel.getPatientVisit(), newPv);
    }

    @Test
    public void testGetTimeDrawn() throws Exception {
        Date date = Utils.getRandomDate();
        pvSourceVessel.setTimeDrawn(date);

        Assert.assertTrue(DateFormatter.formatAsTime(date).equals(
            pvSourceVessel.getFormattedTimeDrawn()));
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(pvSourceVessel);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SourceVesselWrapper ss1 = SourceVesselHelper.addSourceVessel("QWERTY"
            + name);
        pvSourceVessel.setSourceVessel(ss1);
        pvSourceVessel.persist();

        SourceVesselWrapper ss2 = SourceVesselHelper.addSourceVessel("ASDFG"
            + name);
        PvSourceVesselWrapper pvSourceVessel2 = new PvSourceVesselWrapper(
            appService);
        pvSourceVessel2.setPatientVisit(pvSourceVessel.getPatientVisit());
        pvSourceVessel2.setSourceVessel(ss2);

        Assert.assertTrue(pvSourceVessel.compareTo(pvSourceVessel2) > 0);
        Assert.assertTrue(pvSourceVessel2.compareTo(pvSourceVessel) < 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        Integer oldQuantity = pvSourceVessel.getQuantity();
        pvSourceVessel.setQuantity(5);
        pvSourceVessel.reset();
        Assert.assertEquals(oldQuantity, pvSourceVessel.getQuantity());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetNew" + r.nextInt();
        PvSourceVesselWrapper pvSs = PvSourceVesselHelper.newPvSourceVessel(
            name, pvSourceVessel.getPatientVisit());
        pvSs.setQuantity(5);
        pvSs.reset();
        Assert.assertEquals(null, pvSs.getQuantity());
    }

    @Test
    public void testDelete() throws Exception {
        pvSourceVessel.persist();
        int count = appService.search(PvSourceVessel.class,
            new PvSourceVessel()).size();
        pvSourceVessel.delete();
        int countAfter = appService.search(PvSourceVessel.class,
            new PvSourceVessel()).size();
        Assert.assertEquals(count - 1, countAfter);
    }
}
