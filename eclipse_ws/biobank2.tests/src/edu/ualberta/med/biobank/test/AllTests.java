package edu.ualberta.med.biobank.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.action.ActionSuite;
import edu.ualberta.med.biobank.test.validation.ValidationSuite;

@RunWith(Suite.class)
@SuiteClasses({ ActionSuite.class, ValidationSuite.class })
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
