package test.ualberta.med.biobank;

import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class TestPvSampleSource extends TestDatabase {

    private PvSampleSourceWrapper w;
    
    @Before
    public void setUp() throws Exception {
    	super.setUp();
        w = newPvSampleSourceWrapper();
        SiteWrapper site = addSite("SiteName");
        StudyWrapper study = addStudy(site, "studyname");
        ClinicWrapper clinic = addClinic(site, "clinicname");
        PatientWrapper patient = addPatient("041234", study);
        PatientVisitWrapper pvw = addPatientVisit(patient, clinic, new Date(12334), new Date(12334), new Date(12334));
        w.setPatientVisit(pvw);
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService, new SampleSource());
        ssw.persist();
        w.setSampleSource(ssw.getWrappedObject());
    }
    
    public static PvSampleSourceWrapper newPvSampleSourceWrapper() {
    	 return new PvSampleSourceWrapper(appService, new PvSampleSource());
	}
    
    @Test(expected=BiobankCheckException.class)
    public void TestDeleteChecks() throws BiobankCheckException, Exception {
    	//not saved yet, should throw error
    	w.delete();
    }
     
    @Test
    public void TestGetSetPatientVisit() throws BiobankCheckException, Exception {
        PatientVisitWrapper oldWrapper = w.getPatientVisit();     
        PatientVisitWrapper newVisit = addPatientVisit(oldWrapper.getPatient(), oldWrapper.getClinic(), new Date(124), new Date(15234), new Date(12331));     
     
        w.setPatientVisit(newVisit);
        w.persist();
        PatientVisit pv = ModelUtils.getObjectWithId(appService, PatientVisit.class, newVisit.getId());
        //Db contains correct new pv
        Assert.assertTrue(pv!=null);
        Assert.assertTrue(!oldWrapper.getId().equals(w.getPatientVisit().getId()));
        
        w.setPatientVisit(oldWrapper);
        w.persist();
        Collection<PvSampleSource> pvsss=pv.getPvSampleSourceCollection();
        boolean found=false;
        for (PvSampleSource pvss: pvsss) {
            if (w.getId().equals(pvss.getId())) found=true;
        }
        //removed from the sample source list for the pv too
        Assert.assertFalse(found); 
    }

    @Test
    public void TestGetSetSampleSource() throws Exception {
        SampleSourceWrapper oldSource = w.getSampleSource();
        SampleSourceWrapper newSampleSource = TestSampleSource.addSampleSource();
       
        w.setSampleSource(newSampleSource.getWrappedObject());
        w.persist();
        SampleSource ss = ModelUtils.getObjectWithId(appService, SampleSource.class, newSampleSource.getId());
        Assert.assertTrue(ss!=null);
        Assert.assertTrue(!oldSource.getId().equals(w.getSampleSource().getId()));
        Assert.assertTrue(w.getSampleSource().getId().equals(newSampleSource.getId()));
    }
    
    @Test
    public void TestBasicGettersAndSetters() throws BiobankCheckException, Exception {
        testGettersAndSetters(w);
    }
}
