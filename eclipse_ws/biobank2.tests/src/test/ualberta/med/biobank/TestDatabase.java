package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.junit.Before;
import org.springframework.remoting.RemoteAccessException;

public class TestDatabase {
    protected WritableApplicationService appService;

    @Before
    public void setUp() throws Exception {
        try {
            appService = (WritableApplicationService) ApplicationServiceProvider
                .getApplicationServiceFromUrl("http://localhost:8080/biobank2",
                    "testuser", "test");
        } catch (ApplicationException exp) {
            System.out.println("Login failed - error authenticating user");
        } catch (RemoteAccessException exp) {
            System.out.println("Login Failed - Remote Access Exception:");
        }
    }

}
