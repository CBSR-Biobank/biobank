package test.ualberta.med.biobank.wrappers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.TestDatabase;
import test.ualberta.med.biobank.Utils;
import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.PvSampleSourceHelper;
import test.ualberta.med.biobank.internal.SampleSourceHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;

public class TestPvSampleSource extends TestDatabase {

    private PvSampleSourceWrapper pvSampleSource;

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
        SampleSourceWrapper oldSource = pvSampleSource.getSampleSource();
        String name = "testGetSetSampleSource" + r.nextInt();
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(name);

        pvSampleSource.setSampleSource(newSampleSource);
        pvSampleSource.persist();

        SampleSource ss = ModelUtils.getObjectWithId(appService,
            SampleSource.class, newSampleSource.getId());
        Assert.assertTrue(ss != null);
        Assert.assertFalse(oldSource.equals(pvSampleSource.getSampleSource()));
        Assert.assertEquals(pvSampleSource.getSampleSource(), newSampleSource);
    }

    @Test
    public void testGetSetPatientVisit() throws Exception {
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
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(pvSampleSource);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SampleSourceWrapper ss1 = SampleSourceHelper.addSampleSource("QWERTY"
            + name);
        pvSampleSource.setSampleSource(ss1);
        pvSampleSource.persist();

        SampleSourceWrapper ss2 = SampleSourceHelper.addSampleSource("ASDFG"
            + name);
        PvSampleSourceWrapper pvSampleSource2 = new PvSampleSourceWrapper(
            appService);
        pvSampleSource2.setPatientVisit(pvSampleSource.getPatientVisit());
        pvSampleSource2.setSampleSource(ss2);

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
        PvSampleSourceWrapper pvSs = PvSampleSourceHelper.newPvSampleSource(
            name, pvSampleSource.getPatientVisit());
        pvSs.setQuantity(5);
        pvSs.reset();
        Assert.assertEquals(null, pvSs.getQuantity());
    }

    @Test
    public void testDelete() throws Exception {
        pvSampleSource.persist();
        int count = appService.search(PvSampleSource.class,
            new PvSampleSource()).size();
        pvSampleSource.delete();
        int countAfter = appService.search(PvSampleSource.class,
            new PvSampleSource()).size();
        Assert.assertEquals(count - 1, countAfter);
    }
}
