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
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;

// FIXME to be implemented by Delphine
public class TestSite extends TestDatabase {

	private List<SiteWrapper> sites;

	private SiteWrapper oneSite;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		sites = SiteWrapper.getAllSites(appService);
		if (sites.size() == 0) {
			SiteWrapper newSite = addSite("NewSite");
			sites.add(newSite);
		}
		oneSite = chooseRandomlyInList(sites);
	}

	@Test
	public void testGettersAndSetters() throws BiobankCheckException, Exception {
		SiteWrapper site = new SiteWrapper(appService);
		site.setCity("");
		site.persist();
		testGettersAndSetters(site);
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
		int expectedSize = studies.size() + 1;
		addStudy("testAddInStudyCollection");

		oneSite.reload();
		// one study added
		Assert.assertEquals(expectedSize, oneSite.getStudyCollection().size());
	}

	@Test
	public void testRemoveInStudyCollection() throws BiobankCheckException,
			Exception {
		StudyWrapper study = addStudy("testRemoveInStudyCollection");
		oneSite.reload();

		List<StudyWrapper> studies = oneSite.getStudyCollection();
		int idStudy = study.getId();
		studies.remove(study);
		oneSite.setStudyCollection(studies);
		study.delete();
		int expectedSize = studies.size();
		oneSite.persist();

		oneSite.reload();
		// one study removed
		Assert.assertEquals(expectedSize, oneSite.getStudyCollection().size());

		// study should not be anymore in the study collection (removed the
		// good one)
		for (StudyWrapper s : oneSite.getStudyCollection()) {
			Assert.assertFalse(s.getId().equals(idStudy));
		}
	}

	private StudyWrapper addStudy(String name) throws BiobankCheckException,
			Exception {
		StudyWrapper study = new StudyWrapper(appService);
		study.setName(name + new Random().nextInt());
		study.setSite(oneSite);
		study.persist();
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
	public void testAddInClinicCollection() throws BiobankCheckException,
			Exception {
		List<ClinicWrapper> clinics = oneSite.getClinicCollection();
		int expectedSize = clinics.size() + 1;
		addClinic("testAddInClinicCollection");

		oneSite.reload();
		// one clinic added
		Assert.assertEquals(expectedSize, oneSite.getClinicCollection().size());
	}

	private ClinicWrapper addClinic(String name) throws BiobankCheckException,
			Exception {
		ClinicWrapper clinic = new ClinicWrapper(appService);
		clinic.setName(name + new Random().nextInt());
		clinic.setCity("");
		clinic.setSite(oneSite);
		clinic.persist();
		return clinic;
	}

	@Test
	public void testRemoveInClinicCollection() throws BiobankCheckException,
			Exception {
		ClinicWrapper clinic = addClinic("testRemoveInClinicCollection");
		oneSite.reload();

		List<ClinicWrapper> clinics = oneSite.getClinicCollection();
		int idClinic = clinic.getId();
		clinics.remove(clinic);
		oneSite.setClinicCollection(clinics);
		clinic.delete();
		int expectedSize = clinics.size();
		oneSite.persist();

		oneSite.reload();
		// one clinic removed
		Assert.assertEquals(expectedSize, oneSite.getClinicCollection().size());

		// clinic should not be anymore in the clinic collection (removed
		// the good one)
		for (ClinicWrapper c : oneSite.getClinicCollection()) {
			Assert.assertFalse(c.getId().equals(idClinic));
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
	public void testAddInContainerTypeCollection()
			throws BiobankCheckException, Exception {
		List<ContainerTypeWrapper> types = oneSite.getContainerTypeCollection();
		int expectedSize = types.size() + 1;
		addContainerType("testAddInContainerTypeCollection");
		oneSite.reload();
		// one type added
		Assert.assertEquals(expectedSize, oneSite.getContainerTypeCollection()
				.size());
	}

	@Test
	public void testRemoveInContainerTypeCollection()
			throws BiobankCheckException, Exception {
		ContainerTypeWrapper type = addContainerType("RemoveInContainerTypeCollection");
		oneSite.reload();
		List<ContainerTypeWrapper> types = oneSite.getContainerTypeCollection();
		int idType = type.getId();
		types.remove(type);
		oneSite.setContainerTypeCollection(types);
		type.delete();
		int expectedSize = types.size();
		oneSite.persist();

		oneSite.reload();
		// one type removed
		Assert.assertEquals(expectedSize, oneSite.getContainerTypeCollection()
				.size());

		// type should not be anymore in the type collection (removed
		// the good one)
		for (ContainerTypeWrapper t : oneSite.getContainerTypeCollection()) {
			Assert.assertFalse(t.getId().equals(idType));
		}
	}

	private ContainerTypeWrapper addContainerType(String name)
			throws BiobankCheckException, Exception {
		ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
		type.setSite(oneSite);
		type.setName(name + new Random().nextInt());
		type.setRowCapacity(5);
		type.setColCapacity(4);
		type.persist();
		return type;
	}

	@Test
	public void testGetContainerCollection() {
		List<ContainerWrapper> containers = oneSite.getContainerCollection();
		int sizeFound = containers.size();

		int expected = oneSite.getWrappedObject().getContainerCollection()
				.size();

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testAddInContainerCollection() throws BiobankCheckException,
			Exception {
		List<ContainerWrapper> containers = oneSite.getContainerCollection();
		int expectedSize = containers.size() + 1;
		addContainer("testAddInContainerCollection");

		oneSite.reload();
		// one container added
		Assert.assertEquals(expectedSize, oneSite.getContainerCollection()
				.size());
	}

	private ContainerWrapper addContainer(String name)
			throws BiobankCheckException, Exception {
		ContainerWrapper container = new ContainerWrapper(appService);
		container.setLabel(name + new Random().nextInt());
		ContainerTypeWrapper type = addContainerType(name);
		container.setContainerType(type);
		container.setSite(oneSite);
		container.persist();
		return container;
	}

	@Test
	public void testRemoveInContainerCollection() throws BiobankCheckException,
			Exception {
		ContainerWrapper container = addContainer("testRemoveInContainerCollection");
		oneSite.reload();
		List<ContainerWrapper> containers = oneSite.getContainerCollection();
		int idContainer = container.getId();
		containers.remove(container);
		oneSite.setContainerCollection(containers);
		container.delete();
		int expectedSize = containers.size();
		oneSite.persist();

		oneSite.reload();
		// one container removed
		Assert.assertEquals(expectedSize, oneSite.getContainerCollection()
				.size());

		// container should not be anymore in the container collection
		// (removed
		// the good one)
		for (ContainerWrapper c : oneSite.getContainerCollection()) {
			Assert.assertFalse(c.getId().equals(idContainer));
		}
	}

	@Test
	public void testGetSampleTypeCollectionBoolean() {
		List<SampleTypeWrapper> types = oneSite.getSampleTypeCollection();
		int sizeFound = types.size();

		int expected = oneSite.getWrappedObject().getSampleTypeCollection()
				.size();

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testGetSampleTypeCollection() {
		List<SampleTypeWrapper> types = oneSite.getSampleTypeCollection(true);
		if (types.size() > 1) {
			for (int i = 0; i < types.size() - 1; i++) {
				SampleTypeWrapper type1 = types.get(i);
				SampleTypeWrapper type2 = types.get(i + 1);
				Assert.assertTrue(type1.compareTo(type2) <= 0);
			}
		}
	}

	@Test
	public void testAddInSampleTypeCollection() throws BiobankCheckException,
			Exception {
		List<SampleTypeWrapper> types = oneSite.getSampleTypeCollection();
		int expectedSize = types.size() + 1;
		addSampleType("testAddInSampleTypeCollection");

		oneSite.reload();
		// one container added
		Assert.assertEquals(expectedSize, oneSite.getSampleTypeCollection()
				.size());
	}

	private SampleTypeWrapper addSampleType(String name)
			throws BiobankCheckException, Exception {
		SampleTypeWrapper type = new SampleTypeWrapper(appService);
		type.setName(name + new Random().nextInt());
		type.setSite(oneSite);
		type.persist();
		return type;
	}

	@Test
	public void testRemoveInSampleTypeCollection()
			throws BiobankCheckException, Exception {
		SampleTypeWrapper type = addSampleType("testRemoveInSampleTypeCollection");
		oneSite.reload();
		List<SampleTypeWrapper> types = oneSite.getSampleTypeCollection();
		int idContainer = type.getId();
		types.remove(type);
		oneSite.setSampleTypeCollection(types);
		type.delete();
		int expectedSize = types.size();
		oneSite.persist();

		oneSite.reload();
		// one type removed
		Assert.assertEquals(expectedSize, oneSite.getSampleTypeCollection()
				.size());

		// type should not be anymore in the type collection
		// (removed the good one)
		for (SampleTypeWrapper t : oneSite.getSampleTypeCollection()) {
			Assert.assertFalse(t.getId().equals(idContainer));
		}
	}

	@Test
	public void testPersist() throws Exception {
		int expected = sites.size() + 1;
		addSite("testPersist");
		int newTotal = SiteWrapper.getAllSites(appService).size();
		Assert.assertEquals(expected, newTotal);
	}

	private SiteWrapper addSite(String name) throws BiobankCheckException,
			Exception {
		SiteWrapper site = new SiteWrapper(appService);
		site.setName(name + new Random().nextInt());
		site.setCity("");
		site.persist();
		return site;
	}

	@Test
	public void testDelete() throws Exception {
		SiteWrapper site = addSite("testDelete");
		// object is in database
		Assert.assertNotNull(site);
		site.delete();
		Site siteInDB = ModelUtils.getObjectWithId(appService, Site.class, site
				.getId());
		// object is not anymore in database
		Assert.assertNull(siteInDB);
	}

	@Test
	public void testResetAlreadyInDatabase() throws Exception {
		String name = oneSite.getName();
		oneSite.setName("toto");
		oneSite.reset();
		Assert.assertEquals(name, oneSite.getName());
	}

	@Test
	public void testResetNew() throws Exception {
		SiteWrapper newSite = new SiteWrapper(appService);
		newSite.setName("titi");
		newSite.reset();
		Assert.assertEquals(null, newSite.getName());
	}

	@Test
	public void testSetPvInfoPossible() throws Exception {
		Random r = new Random();

		String[] types = oneSite.getPvInfoTypes();
		if (types.length == 0) {
			Assert.fail("Can't test without pvinfotypes");
		}
		String labelGlobal = "labelGlobal" + r.nextInt();
		String type = types[r.nextInt(types.length)];
		oneSite.setPvInfoPossible(labelGlobal, type, true);
		oneSite.persist();

		oneSite.reload();
		boolean labelExists = findLabel(oneSite, labelGlobal);
		Assert.assertTrue(labelExists);

		SiteWrapper site2 = addSite("SetPvInfoPossible");
		types = site2.getPvInfoTypes();
		if (types.length == 0) {
			Assert.fail("Can't test without pvinfotypes");
		}
		String labelSite = "labelSite" + r.nextInt();
		type = types[r.nextInt(types.length)];
		site2.setPvInfoPossible(labelSite, type, false);
		site2.persist();

		site2.reload();
		labelExists = findLabel(site2, labelSite);
		Assert.assertTrue(labelExists);

		labelExists = findLabel(oneSite, labelSite);
		Assert.assertFalse(labelExists);
	}

	private boolean findLabel(SiteWrapper site, String label)
			throws ApplicationException {
		String[] labels = site.getPvInfoPossibleLabels();
		for (String l : labels) {
			if (l.equals(label)) {
				return true;
			}
		}
		return false;
	}

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
	// public void testGetSites() {
	// fail("Not yet implemented");
	// }
	//

}
