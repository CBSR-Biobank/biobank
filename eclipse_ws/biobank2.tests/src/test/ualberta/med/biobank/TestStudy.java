package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;

public class TestStudy extends TestDatabase {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testGettersAndSetters() throws BiobankCheckException, Exception {
		// StudyWrapper site = addStudy("testGettersAndSetters");
		// testGettersAndSetters(site);
		// }
		//
		// private StudyWrapper addStudy(SiteWrapper site, String name)
		// throws Exception {
		// StudyWrapper study = new StudyWrapper(appService);
		// study.setName(name + "Random" + r.nextInt());
		// study.setSite(site);
		// study.persist();
		// return study;
		// }
		//
		// private int addStudies(SiteWrapper site, String name)
		// throws BiobankCheckException, Exception {
		// int studiesNber = r.nextInt(15);
		// for (int i = 0; i < studiesNber; i++) {
		// addStudy(site, name);
		// }
		// site.reload();
		// return studiesNber;
		// }
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
	public void testSetSiteSiteWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWrappedClass() {
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
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testReset() {
		fail("Not yet implemented");
	}

}
