package test.ualberta.med.biobank;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

// FIXME to be implemented by Delphine
public class TestSite extends TestDatabase {

	private List<SiteWrapper> sites;

	private SiteWrapper oneSite;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		sites = SiteWrapper.getAllSites(appService);
		if (sites.size() > 0) {
			Random r = new Random();
			int pos = r.nextInt(sites.size());
			oneSite = sites.get(pos);
		}
	}

	@Test
	public void testGetStudyCollection() {
		List<StudyWrapper> studies = oneSite.getStudyCollection();
		int sizeFound = studies.size();

		int expected = oneSite.getWrappedObject().getStudyCollection().size();

		Assert.assertEquals(expected, sizeFound);
	}

	// @Test
	// public void testGetStudyCollectionBoolean() {
	// oneSite.getStudyCollection(true);
	// }
	//
	// @Test
	// public void testGetClinicCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetClinicCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetContainerTypeCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetContainerTypeCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void
	// testSetContainerTypeCollectionCollectionOfContainerTypeBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetContainerTypeCollectionListOfContainerTypeWrapper() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetContainerCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetContainerCollectionCollectionOfContainerBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetContainerCollectionListOfContainerWrapper() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetTopContainerCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetTopContainerCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testReload() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSiteWrapperWritableApplicationServiceSite() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSiteWrapperWritableApplicationService() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetName() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetName() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetActivityStatus() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetActivityStatus() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetComment() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetComment() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetAddress() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetStreet1() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetStreet1() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetStreet2() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetStreet2() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetCity() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetCity() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetProvince() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetProvince() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetPostalCode() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetPostalCode() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetWrappedClass() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testClearTopContainerCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetSampleTypeCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetSampleTypeCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetSampleTypeCollectionCollectionOfSampleTypeBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetSampleTypeCollectionListOfSampleTypeWrapper() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetPvInfoPossibleCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetPvInfoPossibleCollection() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void
	// testSetPvInfoPossibleCollectionCollectionOfPvInfoPossibleBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetPvInfoPossibleCollectionListOfPvInfoPossibleWrapper()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetPvInfoPossibleLabels() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetPvInfoPossible() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetPvInfoPossible() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCompareTo() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetAllSites() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetSites() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testPersist() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testDelete() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testReset() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCheckIntegrity() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testEqualsObject() {
	// fail("Not yet implemented");
	// }

}
