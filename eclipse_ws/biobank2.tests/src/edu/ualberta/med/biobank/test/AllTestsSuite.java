package edu.ualberta.med.biobank.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.action.ActionSuite;
import edu.ualberta.med.biobank.test.model.ModelSuite;

@SuppressWarnings("nls")
@RunWith(Suite.class)
@SuiteClasses({ ActionSuite.class, ModelSuite.class })
public class AllTestsSuite {
    public static BiobankApplicationService appService = null;
    public static final String userLogin = "testuser";
    public static final String userPwd = "test";

    @Deprecated
    public static BiobankApplicationService connect(String user, String password)
        throws Exception {
        return ServiceConnection.getAppService(
            System.getProperty("server", "http://localhost:8080") + "/biobank",
            user, password);
    }

}
