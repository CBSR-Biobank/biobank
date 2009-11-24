package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.ualberta.med.biobank.internal.DbHelper;

@RunWith(Suite.class)
@SuiteClasses( { TestSite.class, TestPatient.class, TestPatientVisit.class,
    TestStudy.class, TestContainerLabelingScheme.class,
    TestShptSampleSource.class, TestSample.class, TestClinic.class,
    TestSampleStorage.class, TestSampleSource.class, TestSampleType.class,
    TestContainer.class, TestContainerType.class, TestShipment.class,
    TestContact.class })
public class AllTests {
    public static WritableApplicationService appService = null;

    @BeforeClass
    public static void setUp() throws Exception {
        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://"
                + System.getProperty("server", "localhost:8080") + "/biobank2",
                "testuser", "test");

        DbHelper.setAppService(appService);
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }

}
