package edu.ualberta.med.biobank.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import edu.ualberta.med.biobank.test.action.TestClinic;
import edu.ualberta.med.biobank.test.action.TestCollectionEvent;
import edu.ualberta.med.biobank.test.action.TestDispatch;
import edu.ualberta.med.biobank.test.action.TestPatient;
import edu.ualberta.med.biobank.test.action.TestProcessingEvent;
import edu.ualberta.med.biobank.test.action.TestRequest;
import edu.ualberta.med.biobank.test.action.TestResearchGroup;
import edu.ualberta.med.biobank.test.action.TestShipment;
import edu.ualberta.med.biobank.test.action.TestSite;
import edu.ualberta.med.biobank.test.action.TestStudy;
import edu.ualberta.med.biobank.test.internal.DbHelper;

@RunWith(Suite.class)
@SuiteClasses({ TestClinic.class, TestCollectionEvent.class,
    TestDispatch.class,
    TestPatient.class, TestProcessingEvent.class, TestRequest.class,
    TestResearchGroup.class,
    TestShipment.class, TestSite.class, TestStudy.class })
public class AllTests {
    public static IActionExecutor appService = null;
    public static final String userLogin = "testuser";
    public static final String userPwd = "test";

    @BeforeClass
    public static void setUp() throws Exception {
        appService = connect(userLogin, userPwd);
        DbHelper.setAppService(appService);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    public static IActionExecutor connect(String user, String password)
        throws Exception {
        return ServiceConnection.getAppService(
            System.getProperty("server", "http://localhost:8080") + "/biobank",
            user, password);
    }

}
