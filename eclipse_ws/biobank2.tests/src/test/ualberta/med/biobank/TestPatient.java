package test.ualberta.med.biobank;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class TestPatient extends TestDatabase {

    private Map<String, PatientWrapper> patientMap;

    private SiteWrapper site;

    private StudyWrapper study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        patientMap = new HashMap<String, PatientWrapper>();
        site = SiteHelper.addSite("Site - Container Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy(site, "Study - Container Test "
            + Utils.getRandomString(10));
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientWrapper container = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        testGettersAndSetters(container);
    }

    @Test
    public void testCompareTo() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testReset() throws Exception {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testReload() throws Exception {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testGetWrappedClass() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testDelete() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testGetStudy() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testCheckPatientNumberUnique() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testGetPatientVisitCollection() {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void testGetPatientInSite() {
        Assert.fail("Not yet implemented");
    }

}
