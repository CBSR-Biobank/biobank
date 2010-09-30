package edu.ualberta.med.biobank.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.wrappers.TestActivityStatus;
import edu.ualberta.med.biobank.test.wrappers.TestAliquot;
import edu.ualberta.med.biobank.test.wrappers.TestClinic;
import edu.ualberta.med.biobank.test.wrappers.TestClinicShipment;
import edu.ualberta.med.biobank.test.wrappers.TestContact;
import edu.ualberta.med.biobank.test.wrappers.TestContainer;
import edu.ualberta.med.biobank.test.wrappers.TestContainerLabelingScheme;
import edu.ualberta.med.biobank.test.wrappers.TestContainerType;
import edu.ualberta.med.biobank.test.wrappers.TestPatient;
import edu.ualberta.med.biobank.test.wrappers.TestPatientVisit;
import edu.ualberta.med.biobank.test.wrappers.TestPvSourceVessel;
import edu.ualberta.med.biobank.test.wrappers.TestSampleStorage;
import edu.ualberta.med.biobank.test.wrappers.TestSampleType;
import edu.ualberta.med.biobank.test.wrappers.TestShippingMethod;
import edu.ualberta.med.biobank.test.wrappers.TestSite;
import edu.ualberta.med.biobank.test.wrappers.TestSourceVessel;
import edu.ualberta.med.biobank.test.wrappers.TestStudy;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@RunWith(Suite.class)
@SuiteClasses({ TestSite.class, TestPatient.class, TestPatientVisit.class,
    TestStudy.class, TestContainerLabelingScheme.class,
    TestPvSourceVessel.class, TestActivityStatus.class, TestAliquot.class,
    TestClinic.class, TestSampleStorage.class, TestSourceVessel.class,
    TestSampleType.class, TestContainer.class, TestContainerType.class,
    TestClinicShipment.class, TestContact.class, TestShippingMethod.class,
    TestContainerLabelingScheme.class })
public class AllTests {
    public static WritableApplicationService appService = null;

    @BeforeClass
    public static void setUp() throws Exception {
        appService = ServiceConnection
            .getAppService(
                System.getProperty("server", "http://localhost:8080")
                    + "/biobank2", "testuser", "test");
        DbHelper.setAppService(appService);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

}
