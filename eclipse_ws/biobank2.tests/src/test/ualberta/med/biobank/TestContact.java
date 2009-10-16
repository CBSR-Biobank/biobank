package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestContact extends TestDatabase {
	private ContactWrapper cw;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		cw = new ContactWrapper(appService, new Contact());
		ClinicWrapper clinicWrapper = new ClinicWrapper(appService, new Clinic());
		SiteWrapper siteWrapper = new SiteWrapper(appService, new Site());
		siteWrapper.setCity("city");
		siteWrapper.setPostalCode("postal");
		siteWrapper.setProvince("ab");
		siteWrapper.setStreet1("stree1");
		siteWrapper.setStreet2("street2");
		siteWrapper.persist();
		clinicWrapper.setSite(siteWrapper);
		clinicWrapper.setStreet1("stree1");
		clinicWrapper.persist();
		cw.setClinicWrapper(clinicWrapper);
	}
	    
	@Test
	public void TestGetSetStudyCollection() throws BiobankCheckException, Exception {
	
	List<StudyWrapper> oldStudyCollection= new ArrayList<StudyWrapper>();
	List<StudyWrapper> modifiedStudyCollection= new ArrayList<StudyWrapper>();
	List<ContactWrapper> studyContactWrappers = new ArrayList<ContactWrapper>();
	StudyWrapper testWrapper = new StudyWrapper(appService);
	        
	Contact dbContact;
	Study dbStudy;
	Collection<Study> dbCollection;
	Collection<Contact> studyContacts;
	List<StudyWrapper> getCollection;
	        
	boolean found;
	       
	//simple add
	studyContactWrappers.add(cw);
	testWrapper.setContactCollection(studyContactWrappers);
	testWrapper.persist();
	modifiedStudyCollection.add(testWrapper);
	cw.setStudyCollection(modifiedStudyCollection);
	
	cw.persist();
			
	dbStudy = ModelUtils.getObjectWithId(appService, Study.class, testWrapper.getId());
	Assert.assertTrue(dbStudy!=null);
	studyContacts=dbStudy.getContactCollection();
	found=false;
	for (Contact contact: studyContacts) {
		if (contact.getId()==cw.getId()) found=true;
	}
	Assert.assertTrue(found);
	dbContact = ModelUtils.getObjectWithId(appService, Contact.class, cw.getId());
	dbCollection=dbContact.getStudyCollection();
	Assert.assertTrue((dbCollection.size()==modifiedStudyCollection.size())&&(dbCollection.size()==cw.getStudyCollection(false).size()));
	
	getCollection = cw.getStudyCollection(false);
	int i=0;
	for (Study study: dbCollection) {
		//set the database correctly
		Assert.assertTrue(study.getId().equals(modifiedStudyCollection.get(i).getId()));
		//get method matches
		Assert.assertTrue(study.getId().equals(getCollection.get(i).getId()));
		i++;
	}
			
	//simple delete
	modifiedStudyCollection.remove(modifiedStudyCollection.size()-1);
	cw.setStudyCollection(modifiedStudyCollection);
	cw.persist();
			
	dbStudy = ModelUtils.getObjectWithId(appService, Study.class, testWrapper.getId());
	Assert.assertTrue(dbStudy==null);
	studyContacts=dbStudy.getContactCollection();
	found=false;
	for (Contact contact: studyContacts) {
		if (contact.getId()==cw.getId()) found=true;
	}
	Assert.assertFalse(found);
	dbContact = ModelUtils.getObjectWithId(appService, Contact.class, cw.getId());
	dbCollection=dbContact.getStudyCollection();
	Assert.assertTrue(dbCollection.size()==oldStudyCollection.size()&&(dbCollection.size()==cw.getStudyCollection(false).size()));
	getCollection = cw.getStudyCollection(false);
	i=0;
	for (Study study: dbCollection) {
		//set the database correctly
		Assert.assertTrue(study.getId().equals(oldStudyCollection.get(i).getId()));
		//get method matches
		Assert.assertTrue(study.getId().equals(getCollection.get(i).getId()));
		i++;
}
		
		//set empty
		modifiedStudyCollection.clear();
		cw.setStudyCollection(modifiedStudyCollection);
		dbContact = ModelUtils.getObjectWithId(appService, Contact.class, cw.getId());
		dbStudy = ModelUtils.getObjectWithId(appService, Study.class, testWrapper.getId());
		Assert.assertTrue(dbStudy==null);
		dbCollection=dbContact.getStudyCollection();
		Assert.assertTrue(dbCollection.size()==0);
		
		//deleting from midpoint of list
		int middle = modifiedStudyCollection.size()+1;
		
		StudyWrapper testWrapper2= new StudyWrapper(appService);
		StudyWrapper testWrapper3= new StudyWrapper(appService);
		modifiedStudyCollection.add(testWrapper2);
		modifiedStudyCollection.add(testWrapper);
		modifiedStudyCollection.add(testWrapper3);
		cw.setStudyCollection(modifiedStudyCollection);
		cw.persist();
		modifiedStudyCollection.remove(middle);
		cw.persist();
		
		Study dbStudy1 = ModelUtils.getObjectWithId(appService, Study.class, testWrapper.getId());
		Study dbStudy2 = ModelUtils.getObjectWithId(appService, Study.class, testWrapper2.getId());
		Study dbStudy3 = ModelUtils.getObjectWithId(appService, Study.class, testWrapper3.getId());
		Assert.assertTrue(dbStudy1==null);
		Assert.assertTrue(dbStudy2!=null);
		Assert.assertTrue(dbStudy3!=null);
		
		studyContacts=dbStudy1.getContactCollection();
		found=false;
		for (Contact contact: studyContacts) {
			if (contact.getId()==cw.getId()) found=true;
		}
		Assert.assertFalse(found);
		
		studyContacts=dbStudy2.getContactCollection();
		found=false;
		for (Contact contact: studyContacts) {
			if (contact.getId()==cw.getId()) found=true;
		}
		Assert.assertTrue(found);
		
		studyContacts=dbStudy3.getContactCollection();
		found=false;
		for (Contact contact: studyContacts) {
			if (contact.getId()==cw.getId()) found=true;
		}
		Assert.assertTrue(found);
		
		dbContact = ModelUtils.getObjectWithId(appService, Contact.class, cw.getId());
		dbCollection=dbContact.getStudyCollection();
		Assert.assertTrue((dbCollection.size()==cw.getStudyCollection(false).size()));
		
		getCollection = cw.getStudyCollection(false);
		i=0;
		boolean firstfound=false, secondfound=false;
		for (Study study: dbCollection) {
			//find the ones we added
			if(study.getId()==dbStudy2.getId()) firstfound=true;
			if(study.getId()==dbStudy3.getId()) secondfound=true;
			//make sure the one we removed is gone
			Assert.assertTrue(study.getId()!=dbStudy1.getId());
			i++;
		}
		Assert.assertTrue(firstfound&&secondfound);
		
    }
    
	@Test
	public void TestGetSetClinicWrapper() throws BiobankCheckException, Exception {
		//make sure we have an object in db, retrieve
		if (cw.getId()==null) cw.persist();
		Contact contact = ModelUtils.getObjectWithId(appService, Contact.class, cw.getId());
		Assert.assertTrue(contact!=null);
		
		//find the clinic we added
		Clinic clinic = ModelUtils.getObjectWithId(appService, Clinic.class, contact.getClinic().getId());
		Assert.assertTrue(clinic!=null);
		
		//Set new clinic
		ClinicWrapper newClinicWrapper = new ClinicWrapper(appService, new Clinic());
		newClinicWrapper.setStreet1("stree1");
		SiteWrapper siteWrapper = new SiteWrapper(appService, new Site());
		siteWrapper.setStreet1("stresd");
		siteWrapper.persist();
		newClinicWrapper.setSite(siteWrapper);
		newClinicWrapper.persist();
		cw.setClinicWrapper(newClinicWrapper);
		cw.persist();
		
		Clinic retrievedClinic;
		
		//Check to make sure the old one is unlinked clinic-side
		retrievedClinic = ModelUtils.getObjectWithId(appService, Clinic.class, clinic.getId());
		Collection<Contact> contacts = retrievedClinic.getContactCollection();
		boolean found=false;
		for (Contact c: contacts) 
			if (c.getId()==cw.getId()) found = true;
		Assert.assertFalse(found);
		
		//Check to make sure the new one exists
		retrievedClinic = ModelUtils.getObjectWithId(appService, Clinic.class, newClinicWrapper.getId());
		Assert.assertTrue(retrievedClinic!=null);
		
		
		//make sure old is unlinked, new one is linked contactWrapper-side
		Assert.assertTrue(clinic.getId()!=cw.getClinicWrapper().getId());		
	}
	
    @Test
    public void TestCompareTo() {
        ContactWrapper contact1 = new ContactWrapper(appService, new Contact());
        contact1.setName("stuff");
        ContactWrapper contact2 = new ContactWrapper(appService, new Contact());
        contact2.setName("stuff");
        Assert.assertTrue(contact1.compareTo(contact2)==0);
    	contact1.setName("stuff1");
    	Assert.assertTrue(contact1.compareTo(contact2)>0);
    	contact1.setName("stuff");
    	contact2.setName("stuff1");
    	Assert.assertTrue(contact1.compareTo(contact2)<0);
    }
    
    @Test(expected = ApplicationException.class)
    public void TestPersistsNullClinic() throws Exception {
    	//null clinic
    	ContactWrapper c = new ContactWrapper(appService);
    	c.persist();	
    }
    
    @Test(expected = BiobankCheckException.class)
    public void TestPersistsNullSomethingElse() throws Exception {
    	//null clinic
    	ContactWrapper c = new ContactWrapper(appService);
    	ClinicWrapper cw = new ClinicWrapper(appService, new Clinic());
    	cw.persist();
    	c.setClinicWrapper(cw);
    	c.persist();	
    }
    
}
