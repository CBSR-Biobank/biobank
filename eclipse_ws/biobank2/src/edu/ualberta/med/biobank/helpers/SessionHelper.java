package edu.ualberta.med.biobank.helpers;

import java.util.Collection;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.client.util.ServiceConnection;
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

    @Override
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
        } catch (ApplicationException exp) {
            logger.error("Error while logging to application", exp);
            if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BioBankPlugin.openAsyncError("Login Failed", exp.getCause()
                    .getMessage());
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
