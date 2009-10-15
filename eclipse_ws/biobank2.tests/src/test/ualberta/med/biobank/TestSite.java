package test.ualberta.med.biobank;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
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
		oneSite = chooseRandomlyInList(sites);
	}

	@Test
	public void testGetStudyCollection() {
		List<StudyWrapper> studies = oneSite.getStudyCollection();
		int sizeFound = studies.size();

		int expected = oneSite.getWrappedObject().getStudyCollection().size();

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testGetStudyCollectionBoolean() throws Exception {
		List<StudyWrapper> studiesSorted = oneSite.getStudyCollection(true);
		if (studiesSorted.size() > 1) {
			for (int i = 0; i < studiesSorted.size() - 1; i++) {
				StudyWrapper study1 = studiesSorted.get(i);
				StudyWrapper study2 = studiesSorted.get(i + 1);
				Assert.assertTrue(study1.compareTo(study2) <= 0);
			}
		}
	}

	@Test
	public void testAddInStudyCollection() throws BiobankCheckException,
			Exception {
		List<StudyWrapper> studies = oneSite.getStudyCollection();

		StudyWrapper study = createStudy(oneSite, "AddInStudyCollection");
		studies.add(study);
		oneSite.setStudyCollection(studies);
		int expectedSize = studies.size();
		oneSite.persist();

		oneSite.reload();
		Assert.assertEquals(expectedSize, oneSite.getStudyCollection().size());
	}

	@Test
	public void testRemoveInStudyCollection() throws BiobankCheckException,
			Exception {
		List<StudyWrapper> studies = oneSite.getStudyCollection();
		StudyWrapper studyNoPatients = null;
		for (StudyWrapper study : studies) {
			if (study.getPatientCollection().size() == 0) {
				studyNoPatients = study;
				break;
			}
		}
		if (studyNoPatients == null) {
			Assert.fail("Need a study without patients to test that");
		} else {
			studies.remove(studyNoPatients);
			oneSite.setStudyCollection(studies);
			int expectedSize = studies.size();
			oneSite.persist();

			oneSite.reload();
			Assert.assertEquals(expectedSize, oneSite.getStudyCollection()
					.size());
		}

	}

	private StudyWrapper createStudy(SiteWrapper site, String name) {
		StudyWrapper study = new StudyWrapper(appService);
		study.setName(name + new Random().nextInt());
		study.setSite(site);
		return study;
	}

	@Test
	public void testGetClinicCollection() {
		List<ClinicWrapper> clinics = oneSite.getClinicCollection();
		int sizeFound = clinics.size();

		int expected = oneSite.getWrappedObject().getClinicCollection().size();

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testGetClinicCollectionBoolean() {
		List<ClinicWrapper> clinics = oneSite.getClinicCollection(true);
		if (clinics.size() > 1) {
			for (int i = 0; i < clinics.size() - 1; i++) {
				ClinicWrapper clinic1 = clinics.get(i);
				ClinicWrapper clinic2 = clinics.get(i + 1);
				Assert.assertTrue(clinic1.compareTo(clinic2) <= 0);
			}
		}
	}

	@Test
	public void testGetContainerTypeCollection() {
		List<ContainerTypeWrapper> types = oneSite.getContainerTypeCollection();
		int sizeFound = types.size();

		int expected = oneSite.getWrappedObject().getContainerTypeCollection()
				.size();

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testGetContainerTypeCollectionBoolean() {
		List<ContainerTypeWrapper> types = oneSite
				.getContainerTypeCollection(true);
		if (types.size() > 1) {
			for (int i = 0; i < types.size() - 1; i++) {
				ContainerTypeWrapper type1 = types.get(i);
				ContainerTypeWrapper type2 = types.get(i + 1);
				Assert.assertTrue(type1.compareTo(type2) <= 0);
			}
		}
	}

	@Test
	public void testGetContainerCollection() {
		List<ContainerWrapper> containers = oneSite.getContainerCollection();
		int sizeFound = containers.size();

		int expected = oneSite.getWrappedObject().getContainerCollection()
				.size();

		Assert.assertEquals(expected, sizeFound);
	}

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
	// public void testGetTopContainerCollectionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetTopContainerCollection() {
	// fail("Not yet implemented");
	// }
	//

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
