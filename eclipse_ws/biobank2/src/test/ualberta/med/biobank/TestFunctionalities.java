package test.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestFunctionalities {

	private WritableApplicationService appService;

	private Random r;

	@Before
	public void setUp() throws Exception {
		appService = (WritableApplicationService) ApplicationServiceProvider
			.getApplicationServiceFromUrl("http://localhost:8080/biobank2");

		r = new Random();
	}

	/**
	 * Test equals method on object get from database
	 */
	@Test
	public void equalsSite() throws Exception {
		int id = getSite().getId();

		Site site = new Site();

		site.setId(id);

		Site search1 = (Site) appService.search(Site.class, site).get(0);

		Site search2 = (Site) appService.search(Site.class, site).get(0);

		Assert.assertEquals(search1.getId(), search2.getId());

		Assert.assertEquals(search1, search2);
	}

	/**
	 * Add a Sample Position
	 * 
	 * @throws Exception
	 */
	@Test
	public void addSamplePosition() throws Exception {
		int abstractPositionSize = appService.search(AbstractPosition.class,
			new AbstractPosition()).size();
		int samplePositionSize = appService.search(SamplePosition.class,
			new SamplePosition()).size();

		StorageContainer sc = getStorageContainer();

		// 1st test = sample not used in another sampleposition
		Sample sample = findNotUsedSampleInSamplePosition();

		trySamplePositionInsert(sample, sc);

		int abstractPositionSizeAfterTest1 = appService.search(
			AbstractPosition.class, new AbstractPosition()).size();
		int samplePositionSizeAfterTest1 = appService.search(
			SamplePosition.class, new SamplePosition()).size();

		// insertion should be ok
		Assert.assertEquals(abstractPositionSize + 1,
			abstractPositionSizeAfterTest1);
		Assert.assertEquals(samplePositionSize + 1,
			samplePositionSizeAfterTest1);

		// 2nd test = sample already used - can't work
		sample = findUsedSampleInSamplePosition();

		trySamplePositionInsert(sample, sc);

		int abstractPositionSizeAfterTest2 = appService.search(
			AbstractPosition.class, new AbstractPosition()).size();
		int samplePositionSizeAfterTest2 = appService.search(
			SamplePosition.class, new SamplePosition()).size();
		// insertion should not be done
		Assert.assertEquals(abstractPositionSizeAfterTest1,
			abstractPositionSizeAfterTest2);
		Assert.assertEquals(samplePositionSizeAfterTest1,
			samplePositionSizeAfterTest2);
	}

	/**
	 * Insert a new SamplePosition
	 */
	private void trySamplePositionInsert(Sample sample, StorageContainer sc) {
		try {
			SamplePosition samplePosition = new SamplePosition();
			samplePosition.setPositionDimensionOne(3);
			samplePosition.setPositionDimensionTwo(3);
			samplePosition.setSample(sample);
			samplePosition.setStorageContainer(sc);
			SDKQueryResult res = appService
				.executeQuery(new InsertExampleQuery(samplePosition));
			samplePosition = (SamplePosition) res.getObjectResult();
		} catch (Exception e) {
			System.err.println("trySamplePositionInsert="
					+ e.getCause().getMessage());
		}
	}

	/**
	 * Insert 2 samplePosition with the executeBatchQuery method
	 * 
	 * @throws Exception
	 */
	@Test
	public void batchQueriesSamplePosition() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		int samplePositionSize = appService.search(SamplePosition.class,
			new SamplePosition()).size();

		StorageContainer sc = getStorageContainer();

		Sample sample = findNotUsedSampleInSamplePosition();
		System.out.println("Not used sample = " + sample.getId());

		SamplePosition samplePosition = new SamplePosition();
		samplePosition.setPositionDimensionOne(1);
		samplePosition.setPositionDimensionTwo(1);
		samplePosition.setSample(sample);
		samplePosition.setStorageContainer(sc);
		queries.add(new InsertExampleQuery(samplePosition));

		samplePosition = new SamplePosition();
		samplePosition.setSample(sample);
		samplePosition.setPositionDimensionOne(2);
		samplePosition.setPositionDimensionTwo(2);
		samplePosition.setStorageContainer(sc);
		// will failed because sample can't be link to 2 samplePosition
		// roll back should be launched
		queries.add(new InsertExampleQuery(samplePosition));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			System.err.println("batchQueriesSamplePosition: "
					+ ae.getCause().getMessage());
		} finally {
			int samplePositionSizeAfter = appService.search(
				SamplePosition.class, new SamplePosition()).size();
			Assert.assertEquals(samplePositionSize, samplePositionSizeAfter);
		}
	}

	/**
	 * Find a sample that is not used in a samplePosition
	 */
	private Sample findNotUsedSampleInSamplePosition() throws Exception {
		HQLCriteria c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.Sample as sample "
					+ " where sample not in "
					+ " (select p.sample from edu.ualberta.med.biobank.model.SamplePosition as p)");
		List<Sample> samples = appService.query(c);
		if (samples.size() > 0) {
			return samples.get(0);
		}
		throw new Exception("No samples to test");
	}

	/**
	 * Find a sample that is used in a samplePosition
	 */
	private Sample findUsedSampleInSamplePosition() throws Exception {
		HQLCriteria c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.Sample as sample "
					+ " where sample in "
					+ " (select p.sample from edu.ualberta.med.biobank.model.SamplePosition as p)");
		List<Sample> samples = appService.query(c);
		if (samples.size() > 0) {
			return samples.get(0);
		}
		throw new Exception("No samples to test");
	}

	/**
	 * Get a site from the database
	 */
	private Site getSite() throws Exception {
		Site site = new Site();

		List<Site> sites = appService.search(Site.class, site);
		if (sites.size() > 0) {
			return sites.get(0);
		}
		throw new Exception("One site should be added");
	}

	/**
	 * insert 2 site with the executeBatchQuery method
	 */
	@Test
	public void batchQueriesSite() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		Address address = new Address();
		address.setCity("Vesoul" + r.nextInt());
		address = (Address) appService.executeQuery(
			new InsertExampleQuery(address)).getObjectResult();

		int sitesSize = appService.search(Site.class, new Site()).size();

		Site site = new Site();
		site.setName("TestBatch1" + r.nextInt());
		site.setAddress(address);
		queries.add(new InsertExampleQuery(site));

		site = new Site();
		site.setName("TestBatch2" + r.nextInt());
		// Insert will failed because address is missing
		// Roll back should be launched
		// site.setAddress(address);
		queries.add(new InsertExampleQuery(site));

		try {
			appService.executeBatchQuery(queries);
		} catch (Exception ae) {
			System.err.println("batchQueriesSite : "
					+ ae.getCause().getMessage());
		} finally {
			int sitesSizeAfter = appService.search(Site.class, new Site())
				.size();
			Assert.assertEquals(sitesSize, sitesSizeAfter);
		}
	}

	/**
	 * Insert 2 sample with the executeBatchQuery method
	 */
	@Test
	public void batchQueriesSample() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		int sampleSize = appService.search(Sample.class, new Sample()).size();

		SampleType st = getSampleType();

		PatientVisit pv = getPatientVisit();

		Sample sample = new Sample();
		sample.setInventoryId("Test1_" + r.nextInt());
		sample.setSampleType(st);
		sample.setPatientVisit(pv);
		queries.add(new InsertExampleQuery(sample));

		sample = new Sample();
		sample.setInventoryId("Test2_" + r.nextInt());
		// Insert will failed because of sampleType missing
		// Roll back should be launched
		queries.add(new InsertExampleQuery(sample));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			System.out.println("batchQueriesSample : "
					+ ae.getCause().getMessage());
		} finally {
			int sampleSizeAfter = appService.search(Sample.class, new Sample())
				.size();
			Assert.assertEquals(sampleSize, sampleSizeAfter);
		}
	}

	/**
	 * get a patient visit from the database
	 */
	private PatientVisit getPatientVisit() throws Exception {
		List<PatientVisit> visits = appService.search(PatientVisit.class,
			new PatientVisit());
		if (visits.size() > 0) {
			return visits.get(0);
		}
		throw new Exception("One patient visit type should be added");
	}

	/**
	 * get a sample type from the database
	 */
	private SampleType getSampleType() throws Exception {
		List<SampleType> types = appService.search(SampleType.class,
			new SampleType());
		if (types.size() > 0) {
			return types.get(0);
		}
		throw new Exception("One sample type should be added");
	}

	/**
	 * Insert 2 storageType with executeBatchQuery method
	 */
	@Test
	public void batchQueriesStorageType() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		int storageTypeSize = appService.search(StorageType.class,
			new StorageType()).size();

		Capacity capacity = getNewCapacity();
		Site site = getSite();

		StorageType st = new StorageType();
		st.setCapacity(capacity);
		st.setSite(site);
		st.setDefaultTemperature(r.nextDouble());
		st.setName("TestBatch_" + r.nextInt());
		queries.add(new InsertExampleQuery(st));

		st = new StorageType();
		queries.add(new InsertExampleQuery(st));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			System.out.println("batchQueriesStorageType : "
					+ ae.getCause().getMessage());
		} finally {
			int storageTypeSizeAfter = appService.search(StorageType.class,
				new StorageType()).size();
			Assert.assertEquals(storageTypeSize, storageTypeSizeAfter);
		}

	}

	/**
	 * Create a new capacity
	 */
	private Capacity getNewCapacity() throws ApplicationException {
		Capacity capacity = new Capacity();
		capacity.setDimensionOneCapacity(r.nextInt(30));
		capacity.setDimensionTwoCapacity(r.nextInt(30));
		return (Capacity) appService.executeQuery(
			new InsertExampleQuery(capacity)).getObjectResult();
	}

	/**
	 * insert 2 container position with executeBatchQuery method
	 */
	@Test
	public void batchQueriesContainerPosition() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		int containerPositionSize = appService.search(ContainerPosition.class,
			new ContainerPosition()).size();

		StorageContainer sc = getStorageContainer();
		StorageContainer scParent = getAnotherStorageContainer(sc.getId());

		ContainerPosition containerPosition = new ContainerPosition();
		containerPosition.setPositionDimensionOne(8);
		containerPosition.setPositionDimensionTwo(8);
		containerPosition.setOccupiedContainer(sc);
		containerPosition.setParentContainer(scParent);
		queries.add(new InsertExampleQuery(containerPosition));

		containerPosition = new ContainerPosition();
		containerPosition.setOccupiedContainer(sc);
		containerPosition.setParentContainer(scParent);
		containerPosition.setPositionDimensionOne(9);
		containerPosition.setPositionDimensionTwo(9);
		queries.add(new InsertExampleQuery(containerPosition));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			System.out.println("batchQueriesContainerPosition : "
					+ ae.getCause().getMessage());
		} finally {
			int containerPositionSizeAfter = appService.search(
				ContainerPosition.class, new ContainerPosition()).size();

			Assert.assertEquals(containerPositionSize,
				containerPositionSizeAfter);
		}
	}

	/**
	 * Find a StorageContainer in the database
	 */
	private StorageContainer getStorageContainer() throws Exception {
		return getAnotherStorageContainer(null);
	}

	/**
	 * Find a StorageContainer with id different from param id (if null, take
	 * whichever StorageContainer)
	 */
	private StorageContainer getAnotherStorageContainer(Integer id)
			throws Exception {
		List<StorageContainer> scs;
		if (id == null) {
			scs = appService.search(StorageContainer.class,
				new StorageContainer());
		} else {
			HQLCriteria c = new HQLCriteria(
				"from edu.ualberta.med.biobank.model.StorageContainer where id <> "
						+ id);
			scs = appService.query(c);
		}
		if (scs.size() == 0) {
			Capacity capacity = new Capacity();
			capacity.setDimensionOneCapacity(8);
			capacity.setDimensionTwoCapacity(12);
			SDKQueryResult result = appService
				.executeQuery(new InsertExampleQuery(capacity));
			capacity = (Capacity) result.getObjectResult();

			Site site = getSite();

			StorageType st = new StorageType();
			st.setCapacity(capacity);
			st.setDefaultTemperature(40.0);
			st.setSite(site);
			result = appService.executeQuery(new InsertExampleQuery(st));
			st = (StorageType) result.getObjectResult();

			StorageContainer sc = new StorageContainer();
			sc.setName("scTest");
			sc.setCapacity(capacity);
			sc.setSite(site);
			sc.setStorageType(st);
			result = appService.executeQuery(new InsertExampleQuery(sc));
			sc = (StorageContainer) result.getObjectResult();
			return sc;
		}
		return scs.get(0);
	}

	/**
	 * insert 2 storage containers using executeBatchQuery method
	 */
	@Test
	public void batchQueriesStorageContainer() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		int storageContainerSize = appService.search(StorageContainer.class,
			new StorageContainer()).size();

		Site site = getSite();

		Capacity capacity = getNewCapacity();
		System.out.println("capacity=" + capacity.getId());

		System.out.println("site=" + site.getId());

		StorageType st = getStorageType();
		System.out.println("st=" + st.getId());

		StorageContainer sc = new StorageContainer();
		sc.setName("TestBatch-" + r.nextInt());
		sc.setCapacity(capacity);
		sc.setSite(site);
		sc.setStorageType(st);
		queries.add(new InsertExampleQuery(sc));

		sc = new StorageContainer();
		sc.setName("TestBatch-" + r.nextInt());
		//
		queries.add(new InsertExampleQuery(sc));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			System.out.println("batchQueriesStorageContainer:"
					+ ae.getCause().getMessage());
		} finally {
			int storageContainerSizeAfter = appService.search(
				StorageContainer.class, new StorageContainer()).size();

			Assert
				.assertEquals(storageContainerSize, storageContainerSizeAfter);
		}
	}

	/**
	 * return a storage type from the database
	 */
	private StorageType getStorageType() throws Exception {
		List<StorageType> types = appService.search(StorageType.class,
			new StorageType());
		if (types.size() > 0) {
			return types.get(0);
		}
		throw new Exception("One StorageType should be added");
	}

	@Test
	public void addStudy() throws Exception {
		Study study = new Study();
		study.setName("Toto");
		study.setSite(getSite());
		study = (Study) appService.executeQuery(new InsertExampleQuery(study))
			.getObjectResult();
	}

	@Test
	public void test() throws Exception {
		StorageType type = getStorageType();

		Capacity capacity = getNewCapacity();
		Site site = getSite();

		StorageType st = new StorageType();
		st.setCapacity(capacity);
		st.setSite(site);
		st.setDefaultTemperature(r.nextDouble());
		st.setName("enfantType" + r.nextInt());
		st = (StorageType) appService.executeQuery(new InsertExampleQuery(st))
			.getObjectResult();

		Collection<StorageType> children = type.getChildStorageTypeCollection();
		if (children == null || children.size() == 0) {
			children = new HashSet<StorageType>();
		}
		children.add(st);
		appService.executeQuery(new UpdateExampleQuery(type));

	}
}
