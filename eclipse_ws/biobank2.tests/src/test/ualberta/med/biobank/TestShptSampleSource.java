package test.ualberta.med.biobank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.SampleSourceHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShptSampleSourceHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.SampleSource;

public class TestShptSampleSource extends TestDatabase {

    private ShptSampleSourceWrapper w;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic);
        w = ShptSampleSourceHelper.addShptSampleSource(Utils
            .getRandomString(10), shipment);
        SampleSourceWrapper ssw = SampleSourceHelper.addSampleSource("setUp");
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
