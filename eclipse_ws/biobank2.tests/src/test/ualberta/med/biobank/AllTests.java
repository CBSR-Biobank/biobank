package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { TestContainerType.class, TestContainer.class,
    TestPatient.class, TestPatientVisit.class, TestSite.class, TestStudy.class })
public class AllTests {
    public static WritableApplicationService appService = null;

    @BeforeClass
    public static void setUp() throws Exception {
        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://"
                + System.getProperty("server", "localhost:8080") + "/biobank2",
                "testuser", "test");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }

}
