package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;

@RunWith(Suite.class)
@SuiteClasses( { TestContainerType.class, TestContainer.class,
    TestPatient.class, TestPatientVisit.class, TestSite.class, TestStudy.class })
public class AllTests {
    public static WritableApplicationService appService = null;

    public static Random r = new Random();

    @BeforeClass
    public static void setUp() throws Exception {
        appService = (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl("http://"
                + System.getProperty("server", "localhost:8080") + "/biobank2",
                "testuser", "test");

        ClinicHelper.setAppService(appService);
        ContainerHelper.setAppService(appService);
        ContainerTypeHelper.setAppService(appService);
        PatientHelper.setAppService(appService);
        PatientVisitHelper.setAppService(appService);
        SampleHelper.setAppService(appService);
        SiteHelper.setAppService(appService);
        StudyHelper.setAppService(appService);
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }

}
