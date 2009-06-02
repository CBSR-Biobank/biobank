package test.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestFunctionalities {

	private WritableApplicationService appService;

	@Before
	public void setUp() throws Exception {
		appService = (WritableApplicationService) ApplicationServiceProvider
			.getApplicationServiceFromUrl("http://localhost:8080/biobank2");
	}

	@Test
	public void equalsSite() throws ApplicationException {
		Site site = new Site();
		site.setId(1);

		Site search1 = (Site) appService.search(Site.class, site).get(0);

		Site search2 = (Site) appService.search(Site.class, site).get(0);

		Assert.assertEquals(search1.getId(), search2.getId());

		Assert.assertEquals(search1, search2);

	}

	@Test
	public void batchQueries() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		StorageContainer sc = getStorageContainer();

		Sample sample = findNotUsedSample();
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
		queries.add(new InsertExampleQuery(samplePosition));

		try {
			appService.executeBatchQuery(queries);
		} finally {
			HQLCriteria c = new HQLCriteria(
				"from edu.ualberta.med.biobank.model.SamplePosition as samplePosition "
						+ " where sample.id = '" + sample.getId() + "'");
			List<SamplePosition> samplePositions = appService.query(c);
			Assert.assertFalse(samplePositions.size() > 0);
		}

	}

	private Sample findNotUsedSample() throws Exception {
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

	private StorageContainer getStorageContainer() throws ApplicationException {
		List<StorageContainer> scs = appService.search(StorageContainer.class,
			new StorageContainer());
		if (scs.size() == 0) {
			Capacity capacity = new Capacity();
			capacity.setDimensionOneCapacity(8);
			capacity.setDimensionTwoCapacity(12);
			SDKQueryResult result = appService
				.executeQuery(new InsertExampleQuery(capacity));
			capacity = (Capacity) result.getObjectResult();

			Site site = new Site();
			site.setId(1);
			site = (Site) appService.search(Site.class, site).get(0);

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

	@Test
	public void batchQueriesSite() throws ApplicationException {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();

		Address address = new Address();
		Random r = new Random();
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
		queries.add(new InsertExampleQuery(site));

		try {
			appService.executeBatchQuery(queries);
		} catch (ApplicationException ae) {
			ae.printStackTrace();
		} finally {
			int sitesSizeAfter = appService.search(Site.class, new Site())
				.size();
			Assert.assertEquals(sitesSize, sitesSizeAfter);
		}
	}
}
