package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.junit.Before;

public class TestDatabase {
	protected WritableApplicationService appService;

	@Before
	public void setUp() throws Exception {
		appService = (WritableApplicationService) ApplicationServiceProvider
				.getApplicationServiceFromUrl("http://localhost:8080/biobank2",
						"testuser", "test");
	}

}
