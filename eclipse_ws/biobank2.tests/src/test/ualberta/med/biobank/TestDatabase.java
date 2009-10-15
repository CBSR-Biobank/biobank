package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;
import java.util.Random;

import org.junit.Before;

public class TestDatabase {
	protected static WritableApplicationService appService;

	@Before
	public void setUp() throws Exception {
		appService = AllTests.appService;
		if (appService == null) {
			AllTests.setUp();
			appService = AllTests.appService;
		}
	}

	public <T> T chooseRandomlyInList(List<T> list) {
		if (list.size() > 0) {
			Random r = new Random();
			int pos = r.nextInt(list.size());
			return list.get(pos);
		}
		return null;
	}
}
