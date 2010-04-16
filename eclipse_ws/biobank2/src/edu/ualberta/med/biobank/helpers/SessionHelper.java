package edu.ualberta.med.biobank.helpers;

import java.util.Collection;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionHelper implements Runnable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SessionHelper.class.getName());

    private String serverUrl;

    private String userName;

    private String password;

    private WritableApplicationService appService;

    private Collection<SiteWrapper> siteWrappers;

    public SessionHelper(String server, boolean secureConnection,
        String userName, String password) {
        if (secureConnection) {
            this.serverUrl = "https://";
        } else {
            this.serverUrl = "http://";
        }
        this.serverUrl += server + "/biobank2";
        this.userName = userName;
        this.password = password;

        appService = null;
        siteWrappers = null;

    }

    public void run() {
        try {
            if (userName.length() == 0) {
                if (BioBankPlugin.getDefault().isDebugging()) {
                    userName = "testuser";
                    appService = ServiceConnection.getAppService(serverUrl,
                        userName, "test");
                } else {
                    appService = ServiceConnection.getAppService(serverUrl);
                }
            } else {
                appService = ServiceConnection.getAppService(serverUrl,
                    userName, password);
            }
            siteWrappers = SiteWrapper.getSites(appService);
            SessionManager.failedLoginAttempts = 0;
        } catch (ApplicationException exp) {
            logger.error("Error while logging to application", exp);
            if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                SessionManager.failedLoginAttempts++;
                if (SessionManager.failedLoginAttempts > 2)
                    BioBankPlugin
                        .openAsyncError("Login Failed",
                            "Too many failed connection attempts. Login disabled for 30 min.");
                else
                    BioBankPlugin.openAsyncError("Login Failed",
                        "Error authenticating user " + userName + ". "
                            + (3 - SessionManager.failedLoginAttempts)
                            + " attempt(s) remaining.");
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

    public String getUserName() {
        return userName;
    }
}
