package edu.ualberta.med.biobank.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.wrappers.TestActivityStatus;
import edu.ualberta.med.biobank.test.wrappers.TestAliquotedSpecimen;
import edu.ualberta.med.biobank.test.wrappers.TestCaCore;
import edu.ualberta.med.biobank.test.wrappers.TestCenter;
import edu.ualberta.med.biobank.test.wrappers.TestClinic;
import edu.ualberta.med.biobank.test.wrappers.TestCollectionEvent;
import edu.ualberta.med.biobank.test.wrappers.TestContact;
import edu.ualberta.med.biobank.test.wrappers.TestContainer;
import edu.ualberta.med.biobank.test.wrappers.TestContainerLabelingScheme;
import edu.ualberta.med.biobank.test.wrappers.TestContainerPath;
import edu.ualberta.med.biobank.test.wrappers.TestContainerType;
import edu.ualberta.med.biobank.test.wrappers.TestDispatch;
import edu.ualberta.med.biobank.test.wrappers.TestModelWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestOriginInfo;
import edu.ualberta.med.biobank.test.wrappers.TestPatient;
import edu.ualberta.med.biobank.test.wrappers.TestProcessingEvent;
import edu.ualberta.med.biobank.test.wrappers.TestShipmentInfo;
import edu.ualberta.med.biobank.test.wrappers.TestShippingMethod;
import edu.ualberta.med.biobank.test.wrappers.TestSite;
import edu.ualberta.med.biobank.test.wrappers.TestSourceSpecimen;
import edu.ualberta.med.biobank.test.wrappers.TestSpecimen;
import edu.ualberta.med.biobank.test.wrappers.TestSpecimenType;
import edu.ualberta.med.biobank.test.wrappers.TestStudy;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@RunWith(Suite.class)
@SuiteClasses({ TestActivityStatus.class, TestAliquotedSpecimen.class,
    TestCaCore.class, TestCenter.class, TestClinic.class,
    TestCollectionEvent.class, TestContact.class, TestContainer.class,
    TestContainerLabelingScheme.class, TestContainerPath.class,
    TestContainerType.class, TestDispatch.class, TestModelWrapper.class,
    TestOriginInfo.class, TestPatient.class, TestProcessingEvent.class,
    TestSpecimenType.class, TestShipmentInfo.class, TestShippingMethod.class,
    TestSite.class, TestSourceSpecimen.class, TestSpecimen.class,
    TestSpecimenType.class, TestStudy.class })
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
