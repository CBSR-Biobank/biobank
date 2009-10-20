package test.ualberta.med.biobank;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleType;

public class TestSample extends TestDatabase {

    SampleWrapper sample;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SampleTypeWrapper sampleTypeWrapper = TestSampleType
            .addSampleTypeWrapper();
        SiteHelper.deletedCreatedSites();
        SiteWrapper site = SiteHelper.addSite("sitename", true);
        ContainerWrapper container = ContainerHelper.addContainerRandom(site,
            "newcontainer");
        StudyWrapper study = StudyHelper.addStudy(site, "studyname");
        PatientWrapper patient = PatientHelper.addPatient("5684", study);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, "clinicname");
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            clinic, null, null, null);
        sample = SampleHelper.newSample(sampleTypeWrapper, container, pv, 3, 3);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        SiteHelper.deletedCreatedSites();
    }

    @Test(expected = BiobankCheckException.class)
    public void TestCheckInventoryIdUnique() throws BiobankCheckException,
        Exception {
        SampleWrapper duplicate = SampleHelper.addSample(
            sample.getSampleType(), sample.getParent(), sample
                .getPatientVisit(), 3, 3);
        // should be allowed same position?
        duplicate.persist();
        sample.setInventoryId(duplicate.getInventoryId());
        sample.checkInventoryIdUnique();

    }

    @Test
    public void TestGetSetPatientVisit() {
        PatientVisitWrapper pvw = new PatientVisitWrapper(appService,
            new PatientVisit());
        sample.setPatientVisit(pvw.getWrappedObject());
        Assert.assertTrue(sample.getPatientVisit().getId() == pvw.getId());
    }

    @Test
    public void TestSetSamplePositionFromString() throws Exception {
        sample.setSamplePositionFromString("01AA", sample.getParent());
        Assert.assertTrue(sample.getPositionString().equals("01AA"));
        RowColPos pos = sample.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));
    }

    @Test
    public void TestGetSetPosition() {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        sample.setPosition(position);
        RowColPos newPosition = sample.getPosition();
        Assert.assertTrue((newPosition.row == position.row)
            && (newPosition.col == position.col));
    }

    @Test
    public void TestGetSetParent() throws Exception {
        Assert.assertTrue(sample.getParent() == null);
        ContainerWrapper parent = ContainerHelper.addContainer("newcontainer",
            "barcode", sample.getParent(), sample.getSite(), sample.getParent()
                .getContainerType());
        sample.setParent(parent);
        Assert.assertTrue(sample.getParent() != null);
        Collection<SampleWrapper> sampleWrappers = parent.getSamples();
        boolean found = false;
        for (SampleWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId() == sample.getId())
                found = true;
        }
        Assert.assertTrue(found);
    }

    @Test
    public void TestGetSetSampleType() {
        SampleTypeWrapper stw = sample.getSampleType();
        SampleTypeWrapper newStw = new SampleTypeWrapper(appService,
            new SampleType());
        Assert.assertTrue(stw.getId() != newStw.getId());
        sample.setSampleType(newStw);
        Assert.assertTrue(stw.getId() == sample.getSampleType().getId());
    }

    @Test
    public void TestCreateNewSample() {
    }

    @Test
    public void TestGetSetQuantityFromType() {

    }

    @Test
    public void TestLoadAttributes() {

    }

    @Test
    public void TestDeleteChecks() {

    }

    @Test
    public void TestCompareTo() {

    }
}
