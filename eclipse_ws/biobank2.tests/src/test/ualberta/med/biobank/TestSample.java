package test.ualberta.med.biobank;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;

public class TestSample extends TestDatabase {

    SampleWrapper sw;
    
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	SampleTypeWrapper sampleTypeWrapper = TestSampleType.addSampleTypeWrapper();
        sw = newSampleWrapper(sampleTypeWrapper);
    }
    
   
    private SampleWrapper newSampleWrapper(SampleTypeWrapper sampleType) {
    	SampleWrapper sw=new SampleWrapper(appService, new Sample());
    	sw.setSampleType(sampleType);
		return sw;
	}


	@Test(expected=BiobankCheckException.class)
    public void TestPersistChecks() throws BiobankCheckException, Exception {
    	sw.persist();
    }

    @Test(expected=BiobankCheckException.class)
    public void TestCheckInventoryIdUnique() throws BiobankCheckException, Exception {
        SampleWrapper duplicate = new SampleWrapper(appService, new Sample());
        duplicate.persist();
        sw.setInventoryId(duplicate.getInventoryId());
        sw.checkInventoryIdUnique();
    }

    @Test
    public void TestGetSetPatientVisit() {
       PatientVisitWrapper pvw = new PatientVisitWrapper(appService, new PatientVisit());
       sw.setPatientVisit(pvw.getWrappedObject());
       Assert.assertTrue(sw.getPatientVisit().getId()==pvw.getId());
    }

    @Test
    public void TestSetSamplePositionFromString() throws Exception {
    	SiteWrapper site = new SiteWrapper(appService, new Site());
    	site.setStreet1("stree1");
    	site.persist();
    	ContainerWrapper container = new ContainerWrapper(appService, new Container());
    	container.setSite(site);
    	ContainerTypeWrapper containerType = new ContainerTypeWrapper(appService, new ContainerType());
    	containerType.setSite(site);
    	containerType.persist();
    	container.setContainerType(containerType);
    	container.persist();
    	sw.setSamplePositionFromString("01AA", container);
    	Assert.assertTrue(sw.getPositionString().equals("01AA"));
    	RowColPos pos=sw.getPosition();
    	Assert.assertTrue(pos.col==0&&pos.row==0);
    }

    @Test
    public void TestGetSetPosition() {
        RowColPos position = new RowColPos();
        position.row=1;
        position.col=3;
        sw.setPosition(position);
        RowColPos newPosition = sw.getPosition();
        Assert.assertTrue(newPosition.row==position.row&&newPosition.col==position.col);
    }
    
    @Test
    public void TestGetSetParent() throws Exception {
       Assert.assertTrue(sw.getParent()==null);
       ContainerWrapper parent = new ContainerWrapper(appService, new Container());
       sw.setParent(parent);
       Assert.assertTrue(sw.getParent()!=null);
       Collection<SampleWrapper> sampleWrappers = parent.getSamples();
       boolean found=false;
       for (SampleWrapper sampleWrapper:sampleWrappers) {
           if (sampleWrapper.getId()==sw.getId()) found=true;
       }
       Assert.assertTrue(found);
    }
    
    @Test
    public void TestGetSetSampleType() {
    	SampleTypeWrapper stw=sw.getSampleType();
    	SampleTypeWrapper newStw= new SampleTypeWrapper(appService, new SampleType());
    	Assert.assertTrue(stw.getId()!=newStw.getId());
    	sw.setSampleType(newStw);
    	Assert.assertTrue(stw.getId()==sw.getSampleType().getId());
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
