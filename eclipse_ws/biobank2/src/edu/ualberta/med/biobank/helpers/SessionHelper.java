package edu.ualberta.med.biobank.helpers;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.List;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.springframework.remoting.RemoteAccessException;

public class SessionHelper implements Runnable {

    private String serverUrl;

    private String userName;

    private String password;

    private WritableApplicationService appService;

    private List<Site> sites;

    public SessionHelper(String server, String userName, String password) {
        this.serverUrl = "http://" + server + "/biobank2";
        this.userName = userName;
        this.password = password;

        appService = null;
        sites = null;
    }

    public void run() {
        try {
            if (userName.length() == 0) {
                if (BioBankPlugin.getDefault().isDebugging()) {
                    appService = (WritableApplicationService) ApplicationServiceProvider
                        .getApplicationServiceFromUrl(serverUrl, "testuser",
                            "test");
                } else {
                    appService = (WritableApplicationService) ApplicationServiceProvider
                        .getApplicationServiceFromUrl(serverUrl);
                }
            } else {
                appService = (WritableApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serverUrl, userName, password);
            }

            Site site = new Site();
            sites = appService.search(Site.class, site);
        } catch (ApplicationException exp) {
            SessionManager.getLogger().error(
                "Error while logging to application", exp);
            if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BioBankPlugin.openAsyncError("Login Failed",
                    "Error authenticating user " + userName);
                return;
            }
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (RemoteAccessException exp) {
            SessionManager.getLogger().error(
                "Error while logging to application", exp);
            BioBankPlugin.openAsyncError(
                "Login Failed - Remote Access Exception", exp.getMessage());
        } catch (Exception exp) {
            SessionManager.getLogger().error(
                "Error while logging to application", exp);
            BioBankPlugin.openAsyncError("Login Failed", exp.getMessage());
        }
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public List<Site> getSites() {
        return sites;
    }
}
