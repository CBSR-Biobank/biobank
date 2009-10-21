package test.ualberta.med.biobank;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.PvSampleSourceHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;

public class TestPvSampleSource extends TestDatabase {

    private PvSampleSourceWrapper w;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SiteWrapper site = SiteHelper.addSite("SiteName");
        StudyWrapper study = StudyHelper.addStudy(site, "studyname");
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        PatientWrapper patient = PatientHelper.addPatient("041234", study);
        PatientVisitWrapper pvw = PatientVisitHelper.addPatientVisit(patient,
            clinic, Utils.getRandomDate(), Utils.getRandomDate(), Utils
                .getRandomDate());
        w = PvSampleSourceHelper.addPvSampleSource(Utils.getRandomString(10),
            pvw);
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService,
            new SampleSource());
        ssw.persist();
        w.setSampleSource(ssw.getWrappedObject());
    }

    @Test(expected = BiobankCheckException.class)
    public void TestDeleteChecks() throws BiobankCheckException, Exception {
        // not saved yet, should throw error
        w.delete();
    }

    @Test
    public void TestGetSetPatientVisit() throws BiobankCheckException,
        Exception {
        PatientVisitWrapper oldWrapper = w.getPatientVisit();
        PatientVisitWrapper newVisit = PatientVisitHelper.addPatientVisit(
            oldWrapper.getPatient(), oldWrapper.getClinic(), Utils
                .getRandomDate(), Utils.getRandomDate(), Utils.getRandomDate());

        w.setPatientVisit(newVisit);
        w.persist();
        PatientVisit pv = ModelUtils.getObjectWithId(appService,
            PatientVisit.class, newVisit.getId());
        // Db contains correct new pv
        Assert.assertTrue(pv != null);
        Assert.assertTrue(!oldWrapper.getId().equals(
            w.getPatientVisit().getId()));

        w.setPatientVisit(oldWrapper);
        w.persist();
        Collection<PvSampleSource> pvsss = pv.getPvSampleSourceCollection();
        boolean found = false;
        for (PvSampleSource pvss : pvsss) {
            if (w.getId().equals(pvss.getId()))
                found = true;
        }
        // removed from the sample source list for the pv too
        Assert.assertFalse(found);
    }

    @Test
    public void TestGetSetSampleSource() throws Exception {
        SampleSourceWrapper oldSource = w.getSampleSource();
        SampleSourceWrapper newSampleSource = PvSampleSourceHelper
            .addPvSampleSource(Utils.getRandomString(10), w.getPatientVisit())
            .getSampleSource();

        w.setSampleSource(newSampleSource.getWrappedObject());
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
    public void TestBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        testGettersAndSetters(w);
    }
}
