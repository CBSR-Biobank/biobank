package edu.ualberta.med.biobank.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.action.TestActivityStatus;
import edu.ualberta.med.biobank.test.action.TestClinic;
import edu.ualberta.med.biobank.test.action.TestCollectionEvent;
import edu.ualberta.med.biobank.test.action.TestContainerType;
import edu.ualberta.med.biobank.test.action.TestDispatch;
import edu.ualberta.med.biobank.test.action.TestPatient;
import edu.ualberta.med.biobank.test.action.TestPermissionEnum;
import edu.ualberta.med.biobank.test.action.TestProcessingEvent;
import edu.ualberta.med.biobank.test.action.TestRequest;
import edu.ualberta.med.biobank.test.action.TestResearchGroup;
import edu.ualberta.med.biobank.test.action.TestRole;
import edu.ualberta.med.biobank.test.action.TestShipment;
import edu.ualberta.med.biobank.test.action.TestSite;
import edu.ualberta.med.biobank.test.action.TestStudy;

@RunWith(Suite.class)
@SuiteClasses({ TestClinic.class, TestCollectionEvent.class,
    TestContainerType.class, TestDispatch.class,
    TestPatient.class, TestProcessingEvent.class, TestRequest.class,
    TestResearchGroup.class, TestRole.class,
    TestShipment.class, TestSite.class, TestStudy.class,
    TestActivityStatus.class, TestPermissionEnum.class })
public class AllTests {
    public static BiobankApplicationService appService = null;
    public static final String userLogin = "testuser";
    public static final String userPwd = "test";

    @BeforeClass
    public static void setUp() throws Exception {
        // appService = connect(userLogin, userPwd);
        // DbHelper.setAppService(appService);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    public static BiobankApplicationService connect(String user, String password)
        throws Exception {
        return ServiceConnection.getAppService(
            System.getProperty("server", "http://localhost:8080") + "/biobank",
            user, password);
    }

}
