package test.ualberta.med.biobank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
import edu.ualberta.med.biobank.model.SampleSource;

public class TestPvSampleSource extends TestDatabase {

    private PvSampleSourceWrapper w;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, Utils.getRandomString(10));
        PatientWrapper patient = shipment.getPatientCollection().get(0);
        PatientVisitWrapper pvw = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());

        w = PvSampleSourceHelper.addPvSampleSource(Utils.getRandomString(10),
            pvw);
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService,
            new SampleSource());
        ssw.persist();
        w.setSampleSource(ssw.getWrappedObject());
    }

    @Test
    public void checkModification() {
        Assert.fail("Check nothing missing after model modification");
    }

    @Test
    public void testGetSetSampleSource() throws Exception {
        SampleSourceWrapper oldSource = w.getSampleSource();
        String name = "testGetSetSampleSource";
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(name);

        w.setSampleSource(newSampleSource);
        w.persist();

        SampleSource ss = ModelUtils.getObjectWithId(appService,
            SampleSource.class, newSampleSource.getId());
        Assert.assertTrue(ss != null);
        Assert.assertTrue(!oldSource.getId()
            .equals(w.getSampleSource().getId()));
        Assert.assertTrue(w.getSampleSource().getId().equals(
            newSampleSource.getId()));
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(w);
    }

}
