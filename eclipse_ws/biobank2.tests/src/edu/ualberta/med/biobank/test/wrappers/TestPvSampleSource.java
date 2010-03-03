package edu.ualberta.med.biobank.test.wrappers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSourceVessel;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.PvSampleSourceHelper;
import edu.ualberta.med.biobank.test.internal.SampleSourceHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestPvSampleSource extends TestDatabase {

    private PvSourceVesselWrapper pvSampleSource;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomPatient(
            clinic, Utils.getRandomString(10));
        PatientWrapper patient = shipment.getPatientCollection().get(0);
        PatientVisitWrapper pvw = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());

        pvSampleSource = PvSampleSourceHelper.addPvSampleSource(Utils
            .getRandomString(10), pvw);
    }

    @Test
    public void testGetSetSampleSource() throws Exception {
        PvSourceVesselWrapper pvss = new PvSourceVesselWrapper(appService);
        Assert.assertNull(pvss.getSourceVessel());

        pvss.setSourceVessel(null);
        Assert.assertNull(pvss.getSourceVessel());

        SampleSourceWrapper oldSource = pvSampleSource.getSourceVessel();
        String name = "testGetSetSampleSource" + r.nextInt();
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(name);

        pvSampleSource.setSourceVessel(newSampleSource);
        pvSampleSource.persist();

        SourceVessel ss = ModelUtils.getObjectWithId(appService,
            SourceVessel.class, newSampleSource.getId());
        Assert.assertTrue(ss != null);
        Assert.assertFalse(oldSource.equals(pvSampleSource.getSourceVessel()));
        Assert.assertEquals(pvSampleSource.getSourceVessel(), newSampleSource);
    }

    @Test
    public void testGetSetPatientVisit() throws Exception {
        PvSourceVesselWrapper pvss = new PvSourceVesselWrapper(appService);
        Assert.assertNull(pvss.getPatientVisit());

        PatientVisitWrapper oldPv = pvSampleSource.getPatientVisit();
        PatientVisitWrapper newPv = PatientVisitHelper.addPatientVisit(oldPv
            .getPatient(), oldPv.getShipment(), Utils.getRandomDate());
        pvSampleSource.setPatientVisit(newPv);
        pvSampleSource.persist();

        PatientVisit pv = ModelUtils.getObjectWithId(appService,
            PatientVisit.class, newPv.getId());
        Assert.assertTrue(pv != null);
        Assert.assertFalse(oldPv.equals(pvSampleSource.getPatientVisit()));
        Assert.assertEquals(pvSampleSource.getPatientVisit(), newPv);
    }

    @Test
    public void testGetDateDrawn() throws Exception {
        Date date = Utils.getRandomDate();
        pvSampleSource.setDateDrawn(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(
            pvSampleSource.getFormattedDateDrawn()));
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(pvSampleSource);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SampleSourceWrapper ss1 = SampleSourceHelper.addSampleSource("QWERTY"
            + name);
        pvSampleSource.setSourceVessel(ss1);
        pvSampleSource.persist();

        SampleSourceWrapper ss2 = SampleSourceHelper.addSampleSource("ASDFG"
            + name);
        PvSourceVesselWrapper pvSampleSource2 = new PvSourceVesselWrapper(
            appService);
        pvSampleSource2.setPatientVisit(pvSampleSource.getPatientVisit());
        pvSampleSource2.setSourceVessel(ss2);

        Assert.assertTrue(pvSampleSource.compareTo(pvSampleSource2) > 0);
        Assert.assertTrue(pvSampleSource2.compareTo(pvSampleSource) < 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        Integer oldQuantity = pvSampleSource.getQuantity();
        pvSampleSource.setQuantity(5);
        pvSampleSource.reset();
        Assert.assertEquals(oldQuantity, pvSampleSource.getQuantity());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetNew" + r.nextInt();
        PvSourceVesselWrapper pvSs = PvSampleSourceHelper.newPvSampleSource(
            name, pvSampleSource.getPatientVisit());
        pvSs.setQuantity(5);
        pvSs.reset();
        Assert.assertEquals(null, pvSs.getQuantity());
    }

    @Test
    public void testDelete() throws Exception {
        pvSampleSource.persist();
        int count = appService.search(PvSourceVessel.class,
            new PvSourceVessel()).size();
        pvSampleSource.delete();
        int countAfter = appService.search(PvSourceVessel.class,
            new PvSourceVessel()).size();
        Assert.assertEquals(count - 1, countAfter);
    }
}
