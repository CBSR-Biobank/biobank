package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestSite extends TestDatabase {

	private List<SiteWrapper> createdSites;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createdSites = new ArrayList<SiteWrapper>();
	}

	@Test
	public void testGettersAndSetters() throws Exception {
		SiteWrapper site = addSite("testGettersAndSetters");
		createdSites.add(site);
		testGettersAndSetters(site);
	}

	@Test
	public void testGetStudyCollection() throws Exception {
		SiteWrapper site = addSite("testGetStudyCollection");
		createdSites.add(site);
		int studiesNber = addStudies(site, "testGetStudyCollection");

		List<StudyWrapper> studies = site.getStudyCollection();
		int sizeFound = studies.size();

		Assert.assertEquals(studiesNber, sizeFound);
	}

	@Test
	public void testGetStudyCollectionBoolean() throws Exception {
		SiteWrapper site = addSite("testGetStudyCollectionBoolean");
		addStudies(site, "testGetStudyCollectionBoolean");

		List<StudyWrapper> studiesSorted = site.getStudyCollection(true);
		if (studiesSorted.size() > 1) {
			for (int i = 0; i < studiesSorted.size() - 1; i++) {
				StudyWrapper study1 = studiesSorted.get(i);
				StudyWrapper study2 = studiesSorted.get(i + 1);
				Assert.assertTrue(study1.compareTo(study2) <= 0);
			}
		}
	}

	@Test
	public void testAddInStudyCollection() throws Exception {
		SiteWrapper site = addSite("testAddInStudyCollection");
		int studiesNber = addStudies(site, "testAddInStudyCollection");

		List<StudyWrapper> studies = site.getStudyCollection();
		StudyWrapper study = new StudyWrapper(appService);
		study.setName("testAddInStudyCollection" + r.nextInt());
		study.setSite(site);
		studies.add(study);
		site.setStudyCollection(studies);
		site.persist();

		site.reload();
		// one study added
		Assert.assertEquals(studiesNber + 1, site.getStudyCollection().size());
	}

	@Test
	public void testRemoveInStudyCollection() throws Exception {
		SiteWrapper site = addSite("testRemoveInStudyCollection");
		int studiesNber = addStudies(site, "testRemoveInStudyCollection");

		List<StudyWrapper> studies = site.getStudyCollection();
		StudyWrapper study = chooseRandomlyInList(studies);
		int idStudy = study.getId();
		studies.remove(study);
		site.setStudyCollection(studies);
		study.delete();
		site.persist();

		site.reload();
		// one study removed
		Assert.assertEquals(studiesNber - 1, site.getStudyCollection().size());

		// study should not be anymore in the study collection (removed the
		// good one)
		for (StudyWrapper s : site.getStudyCollection()) {
			Assert.assertFalse(s.getId().equals(idStudy));
		}
	}

	@Test
	public void testGetClinicCollection() throws Exception {
		SiteWrapper site = addSite("testGetClinicCollection");
		int clinicsNber = addClinics(site, "testGetClinicCollection");

		List<ClinicWrapper> clinics = site.getClinicCollection();
		int sizeFound = clinics.size();

		Assert.assertEquals(clinicsNber, sizeFound);
	}

	@Test
	public void testGetClinicCollectionBoolean() throws Exception {
		SiteWrapper site = addSite("testGetClinicCollectionBoolean");
		addClinics(site, "testGetClinicCollectionBoolean");

		List<ClinicWrapper> clinics = site.getClinicCollection(true);
		if (clinics.size() > 1) {
			for (int i = 0; i < clinics.size() - 1; i++) {
				ClinicWrapper clinic1 = clinics.get(i);
				ClinicWrapper clinic2 = clinics.get(i + 1);
				Assert.assertTrue(clinic1.compareTo(clinic2) <= 0);
			}
		}
	}

	@Test
	public void testAddInClinicCollection() throws Exception {
		SiteWrapper site = addSite("testAddInClinicCollection");
		int nber = addClinics(site, "testAddInClinicCollection");

		List<ClinicWrapper> clinics = site.getClinicCollection();
		ClinicWrapper clinic = new ClinicWrapper(appService);
		clinic.setName("testAddInClinicCollection" + r.nextInt());
		clinic.setCity("");
		clinic.setSite(site);
		clinics.add(clinic);
		site.setClinicCollection(clinics);
		site.persist();

		site.reload();
		// one clinic added
		Assert.assertEquals(nber + 1, site.getClinicCollection().size());
	}

	@Test
	public void testRemoveInClinicCollection() throws Exception {
		SiteWrapper site = addSite("testRemoveInClinicCollection");
		int nber = addClinics(site, "testRemoveInClinicCollection");

		List<ClinicWrapper> clinics = site.getClinicCollection();
		ClinicWrapper clinic = chooseRandomlyInList(clinics);
		int idClinic = clinic.getId();
		clinics.remove(clinic);
		site.setClinicCollection(clinics);
		clinic.delete();
		site.persist();

		site.reload();
		// one clinic removed
		Assert.assertEquals(nber - 1, site.getClinicCollection().size());

		// clinic should not be anymore in the clinic collection (removed
		// the good one)
		for (ClinicWrapper c : site.getClinicCollection()) {
			Assert.assertFalse(c.getId().equals(idClinic));
		}
	}

	@Test
	public void testGetContainerTypeCollection() throws Exception {
		SiteWrapper site = addSite("testGetContainerTypeCollection");
		int nber = addContainerTypes(site, "testGetContainerTypeCollection");

		List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
		int sizeFound = types.size();

		Assert.assertEquals(nber, sizeFound);
	}

	@Test
	public void testGetContainerTypeCollectionBoolean() throws Exception {
		SiteWrapper site = addSite("testGetContainerTypeCollectionBoolean");
		addContainerTypes(site, "testGetContainerTypeCollectionBoolean");

		List<ContainerTypeWrapper> types = site
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
	public void testAddInContainerTypeCollection() throws Exception {
		SiteWrapper site = addSite("testAddInContainerTypeCollection");
		int nber = addContainerTypes(site, "testAddInContainerTypeCollection");

		List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
		ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
		type.setSite(site);
		type.setName("testAddInContainerTypeCollection" + r.nextInt());
		type.setRowCapacity(5);
		type.setColCapacity(4);
		types.add(type);
		site.setContainerTypeCollection(types);
		site.persist();

		site.reload();
		// one type added
		Assert.assertEquals(nber + 1, site.getContainerTypeCollection().size());
	}

	@Test
	public void testRemoveInContainerTypeCollection() throws Exception {
		SiteWrapper site = addSite("testRemoveInContainerTypeCollection");
		int nber = addContainerTypes(site,
				"testRemoveInContainerTypeCollection");

		List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
		ContainerTypeWrapper type = chooseRandomlyInList(types);
		int idType = type.getId();
		types.remove(type);
		site.setContainerTypeCollection(types);
		type.delete();
		site.persist();

		site.reload();
		// one type removed
		Assert.assertEquals(nber - 1, site.getContainerTypeCollection().size());

		// type should not be anymore in the type collection (removed
		// the good one)
		for (ContainerTypeWrapper t : site.getContainerTypeCollection()) {
			Assert.assertFalse(t.getId().equals(idType));
		}
	}

	private ContainerTypeWrapper addContainerType(SiteWrapper site, String name)
			throws Exception {
		ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
		type.setSite(site);
		type.setName(name + "Random" + r.nextInt());
		type.setRowCapacity(5);
		type.setColCapacity(4);
		type.setTopLevel(r.nextBoolean());
		type.persist();
		return type;
	}

	private int addContainerTypes(SiteWrapper site, String name)
			throws Exception {
		int nber = r.nextInt(15);
		for (int i = 0; i < nber; i++) {
			addContainerType(site, name);
		}
		site.reload();
		return nber;
	}

	@Test
	public void testGetContainerCollection() throws Exception {
		SiteWrapper site = addSite("testGetContainerCollection");
		int nber = addContainers(site, "testGetContainerCollection");

		List<ContainerWrapper> containers = site.getContainerCollection();
		int sizeFound = containers.size();

		Assert.assertEquals(nber, sizeFound);
	}

	@Test
	public void testAddInContainerCollection() throws Exception {
		SiteWrapper site = addSite("testAddInContainerCollection");
		int nber = addContainers(site, "testAddInContainerCollection");

		List<ContainerWrapper> containers = site.getContainerCollection();
		ContainerWrapper container = new ContainerWrapper(appService);
		container.setLabel("testAddInContainerCollection" + r.nextInt());
		ContainerTypeWrapper type = addContainerType(site,
				"testAddInContainerCollection");
		container.setContainerType(type);
		container.setSite(site);
		containers.add(container);
		site.setContainerCollection(containers);
		site.persist();

		site.reload();
		// one container added
		Assert.assertEquals(nber + 1, site.getContainerCollection().size());
	}

	private ContainerWrapper addContainer(SiteWrapper site, String name)
			throws Exception {
		ContainerWrapper container = new ContainerWrapper(appService);
		container.setLabel(name + "Random" + r.nextInt());
		ContainerTypeWrapper type = addContainerType(site, name);
		container.setContainerType(type);
		container.setSite(site);
		container.persist();
		return container;
	}

	private int addContainers(SiteWrapper site, String name) throws Exception {
		int nber = r.nextInt(15);
		for (int i = 0; i < nber; i++) {
			addContainer(site, name);
		}
		site.reload();
		return nber;
	}

	@Test
	public void testRemoveInContainerCollection() throws Exception {
		SiteWrapper site = addSite("testRemoveInContainerCollection");
		int nber = addContainers(site, "testRemoveInContainerCollection");

		List<ContainerWrapper> containers = site.getContainerCollection();
		ContainerWrapper container = chooseRandomlyInList(containers);
		int idContainer = container.getId();
		containers.remove(container);
		site.setContainerCollection(containers);
		container.delete();
		site.persist();

		site.reload();
		// one container removed
		Assert.assertEquals(nber - 1, site.getContainerCollection().size());

		// container should not be anymore in the container collection
		// (removed the good one)
		for (ContainerWrapper c : site.getContainerCollection()) {
			Assert.assertFalse(c.getId().equals(idContainer));
		}
	}

	@Test
	public void testGetSampleTypeCollection() throws Exception {
		SiteWrapper site = addSite("testGetSampleTypeCollection");
		int nber = addSampleTypes(site, "testGetSampleTypeCollection");

		List<SampleTypeWrapper> types = site.getSampleTypeCollection();
		int sizeFound = types.size();

		Assert.assertEquals(nber, sizeFound);
	}

	@Test
	public void testGetSampleTypeCollectionBoolean() throws Exception {
		SiteWrapper site = addSite("testGetSampleTypeCollectionBoolean");
		addSampleTypes(site, "testGetSampleTypeCollectionBoolean");

		List<SampleTypeWrapper> types = site.getSampleTypeCollection(true);
		if (types.size() > 1) {
			for (int i = 0; i < types.size() - 1; i++) {
				SampleTypeWrapper type1 = types.get(i);
				SampleTypeWrapper type2 = types.get(i + 1);
				Assert.assertTrue(type1.compareTo(type2) <= 0);
			}
		}
	}

	@Test
	public void testAddInSampleTypeCollection() throws Exception {
		SiteWrapper site = addSite("testAddInSampleTypeCollection");
		int nber = addSampleTypes(site, "testAddInSampleTypeCollection");

		List<SampleTypeWrapper> types = site.getSampleTypeCollection();
		SampleTypeWrapper type = new SampleTypeWrapper(appService);
		type.setName("testAddInSampleTypeCollection" + r.nextInt());
		type.setSite(site);
		types.add(type);
		site.setSampleTypeCollection(types);
		site.persist();

		site.reload();
		// one container added
		Assert.assertEquals(nber + 1, site.getSampleTypeCollection().size());
	}

	private SampleTypeWrapper addSampleType(SiteWrapper site, String name)
			throws Exception {
		SampleTypeWrapper type = new SampleTypeWrapper(appService);
		type.setName(name + "Random" + r.nextInt());
		type.setSite(site);
		type.persist();
		return type;
	}

	private int addSampleTypes(SiteWrapper site, String name) throws Exception {
		int nber = r.nextInt(15);
		for (int i = 0; i < nber; i++) {
			addSampleType(site, name);
		}
		site.reload();
		return nber;
	}

	@Test
	public void testRemoveInSampleTypeCollection() throws Exception {
		SiteWrapper site = addSite("testRemoveInSampleTypeCollection");
		int nber = addSampleTypes(site, "testRemoveInSampleTypeCollection");

		List<SampleTypeWrapper> types = site.getSampleTypeCollection();
		SampleTypeWrapper type = chooseRandomlyInList(types);
		int idContainer = type.getId();
		types.remove(type);
		site.setSampleTypeCollection(types);
		type.delete();
		site.persist();

		site.reload();
		// one type removed
		Assert.assertEquals(nber - 1, site.getSampleTypeCollection().size());

		// type should not be anymore in the type collection
		// (removed the good one)
		for (SampleTypeWrapper t : site.getSampleTypeCollection()) {
			Assert.assertFalse(t.getId().equals(idContainer));
		}
	}

	@Test
	public void testAddSite() throws Exception {
		int oldTotal = SiteWrapper.getAllSites(appService).size();
		addSite("testPersist");
		int newTotal = SiteWrapper.getAllSites(appService).size();
		Assert.assertEquals(oldTotal + 1, newTotal);
	}

	@Test
	public void testDelete() throws Exception {
		SiteWrapper site = super.addSite("testDelete");
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
		SiteWrapper site = addSite("testResetAlreadyInDatabase");
		site.reload();
		String oldName = site.getName();
		site.setName("toto");
		site.reset();
		Assert.assertEquals(oldName, site.getName());
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
		SiteWrapper site = addSite("testSetPvInfoPossible");
		site.reload();

		String[] types = site.getPvInfoTypes();
		if (types.length == 0) {
			Assert.fail("Can't test without pvinfotypes");
		}
		String labelGlobal = "labelGlobal" + r.nextInt();
		String type = types[r.nextInt(types.length)];
		site.setPvInfoPossible(labelGlobal, type, true);
		site.persist();

		site.reload();
		boolean labelExists = findLabel(site, labelGlobal);
		Assert.assertTrue(labelExists);

		Assert.assertEquals(type, site.getPvInfoType(labelGlobal));

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

		Assert.assertEquals(type, site2.getPvInfoType(labelGlobal));

		labelExists = findLabel(site, labelSite);
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

	@Test
	public void testGetTopContainerCollection() throws Exception {
		SiteWrapper site = addSite("testGetTopContainerCollection");
		addContainers(site, "testGetTopContainerCollection");

		List<ContainerWrapper> containers = site.getTopContainerCollection();
		int sizeFound = containers.size();

		int expected = 0;
		for (ContainerWrapper container : site.getContainerCollection()) {
			if (Boolean.TRUE.equals(container.getContainerType().getTopLevel())) {
				expected++;
			}
		}

		Assert.assertEquals(expected, sizeFound);
	}

	@Test
	public void testGetTopContainerCollectionBoolean() throws Exception {
		SiteWrapper site = addSite("testGetTopContainerCollection");
		addContainers(site, "testGetTopContainerCollection");

		List<ContainerWrapper> containers = site
				.getTopContainerCollection(true);
		if (containers.size() > 1) {
			for (int i = 0; i < containers.size() - 1; i++) {
				ContainerWrapper container1 = containers.get(i);
				ContainerWrapper containter2 = containers.get(i + 1);
				Assert.assertTrue(container1.compareTo(containter2) <= 0);
			}
		}
	}

	@Test
	public void testGetSites() throws Exception {
		int nber = addSites("testGetSites");

		List<SiteWrapper> siteWrappers = SiteWrapper.getSites(appService, null);
		Assert.assertEquals(nber, siteWrappers.size());

		SiteWrapper site = chooseRandomlyInList(siteWrappers);
		siteWrappers = SiteWrapper.getSites(appService, site.getId());
		Assert.assertEquals(1, siteWrappers.size());

		HQLCriteria criteria = new HQLCriteria("select max(id) from "
				+ Site.class.getName());
		List<Integer> max = appService.query(criteria);
		siteWrappers = SiteWrapper.getSites(appService, max.get(0) + 1000);
		Assert.assertEquals(0, siteWrappers.size());
	}

	@Override
	protected SiteWrapper addSite(String name) throws Exception {
		SiteWrapper site = super.addSite(name);
		createdSites.add(site);
		return site;
	}

	@After
	public void tearDown() {
		try {
			for (SiteWrapper site : createdSites) {
				removeFromList(site.getContainerCollection());
				removeFromList(site.getStudyCollection());
				removeFromList(site.getClinicCollection());
				removeFromList(site.getContainerTypeCollection());
				site.reload();
				site.delete();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Assert.fail();
		}

	}

	private void removeFromList(List<? extends ModelWrapper<?>> list)
			throws Exception {
		if (list != null) {
			for (ModelWrapper<?> object : list) {
				object.reload();
				object.delete();
			}
		}
	}

}
