package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

public class TestStudy extends TestDatabase {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() {
		try {
			deletedCreatedSites();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Assert.fail();
		}
	}

	@Test
	public void testGettersAndSetters() throws Exception {
		SiteWrapper site = addSite("testGettersAndSetters");
		StudyWrapper study = addStudy(site, "testGettersAndSetters");
		testGettersAndSetters(study);
	}

	@Test
	public void testGetSite() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSiteSite() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetContactCollectionBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetContactCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetContactCollectionCollectionOfContactBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetContactCollectionListOfContactWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSampleStorageCollectionBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSampleStorageCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSampleStorageCollectionCollectionOfSampleStorageBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSampleStorageCollectionListOfSampleStorageWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSampleSourceCollectionBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSampleSourceCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSampleSourceCollectionCollectionOfSampleSourceBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSampleSourceCollectionListOfSampleSourceWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPvInfoLabels() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPvInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPvInfoType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPvInfoAllowedValues() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPvInfoAllowedValues() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClinicCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientCollectionBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPatientCollectionCollectionOfPatientBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPatientCollectionListOfPatientWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientCountForClinic() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientVisitCountForClinic() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientVisitCount() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() throws Exception {
		StudyWrapper study = addStudy(addSite("testDelete"), "testDelete");
		// object is in database
		Assert.assertNotNull(study);
		study.delete();
		Study studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
				study.getId());
		// object is not anymore in database
		Assert.assertNull(studyInDB);
	}

	@Test
	public void testResetAlreadyInDatabase() throws Exception {
		StudyWrapper study = addStudy(addSite("testResetAlreadyInDatabase"),
				"testResetAlreadyInDatabase");
		study.reload();
		String oldName = study.getName();
		study.setName("toto");
		study.reset();
		Assert.assertEquals(oldName, study.getName());
	}

	@Test
	public void testResetNew() throws Exception {
		StudyWrapper newStudy = new StudyWrapper(appService);
		newStudy.setName("titi");
		newStudy.reset();
		Assert.assertEquals(null, newStudy.getName());
	}

}
