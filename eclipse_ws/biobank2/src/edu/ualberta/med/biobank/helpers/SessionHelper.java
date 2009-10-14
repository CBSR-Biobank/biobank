package edu.ualberta.med.biobank.helpers;

import java.util.Collection;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class SessionHelper implements Runnable {

    private static Logger LOGGER = Logger.getLogger(SessionHelper.class
        .getName());

    private String serverUrl;

    private String userName;

    private String password;

    private WritableApplicationService appService;

    private Collection<SiteWrapper> siteWrappers;

    public SessionHelper(String server, String userName, String password) {
        this.serverUrl = "http://" + server + "/biobank2";
        this.userName = userName;
        this.password = password;

        appService = null;
        siteWrappers = null;
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

            siteWrappers = SiteWrapper.getSites(appService, null);
        } catch (ApplicationException exp) {
            LOGGER.error("Error while logging to application", exp);
            if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BioBankPlugin.openAsyncError("Login Failed",
                    "Error authenticating user " + userName);
                return;
            }
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (RemoteAccessException exp) {
            BioBankPlugin.openAsyncError(
                "Login Failed - Remote Access Exception", exp);
        } catch (Exception exp) {
            BioBankPlugin.openAsyncError("Login Failed", exp);
        }
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public Collection<SiteWrapper> getSites() {
        return siteWrappers;
    }
}
