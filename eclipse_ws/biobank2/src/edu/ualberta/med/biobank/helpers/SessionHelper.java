package edu.ualberta.med.biobank.helpers;

import java.awt.Desktop;
import java.net.URI;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.eclipse.core.runtime.Platform;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SessionHelper implements Runnable {

    private static BgcLogger logger = BgcLogger.getLogger(SessionHelper.class
        .getName());

    private String serverUrl;

    private String userName;

    private String password;

    private BiobankApplicationService appService;

    private UserWrapper user;

    private static final String DOWNLOAD_URL = "http://aicml-med.cs.ualberta.ca/CBSR/latest.html"; 

    private static final String DEFAULT_TEST_USER = "testuser"; 

    private static final String DEFAULT_TEST_USER_PWD = "test"; 

    private static final String SECURE_CONNECTION_URI = "https://"; 

    private static final String NON_SECURE_CONNECTION_URI = "http://"; 

    private static final String BIOBANK_URL = "/biobank"; 

    public SessionHelper(String server, boolean secureConnection,
        String userName, String password) {
        if (secureConnection) {
            this.serverUrl = SECURE_CONNECTION_URI;
        } else {
            this.serverUrl = NON_SECURE_CONNECTION_URI;
        }
        this.serverUrl += server + BIOBANK_URL;
        this.userName = userName;
        this.password = password;

        appService = null;
    }

    @Override
    public void run() {
        try {
            if (userName.length() == 0) {
                if (BiobankPlugin.getDefault().isDebugging()) {
                    userName = DEFAULT_TEST_USER;
                    appService = ServiceConnection.getAppService(serverUrl,
                        userName, DEFAULT_TEST_USER_PWD);
                } else {
                    appService = ServiceConnection.getAppService(serverUrl);
                }
            } else {
                appService = ServiceConnection.getAppService(serverUrl,
                    userName, password);
            }
            String clientVersion = Platform.getProduct().getDefiningBundle()
                .getVersion().toString();
            logger.debug("Check client version:"
                + clientVersion);
            appService.checkVersion(clientVersion);
            user = UserWrapper.getUser(appService, userName);
        } catch (ApplicationException exp) {
            if (exp instanceof ServerVersionInvalidException) {
                BgcPlugin.openError(
                    "Server Version Error",
                    "The server you are connecting to does not have a version. Cannot authenticate.", exp);
            } else if (exp instanceof ServerVersionNewerException) {
                if (BgcPlugin.openConfirm(
                    "Server Version Error",
                    exp.getMessage()
                        + "Would you like to download the latest version?")) {
                    try {
                        Desktop.getDesktop().browse(new URI(DOWNLOAD_URL));
                    } catch (Exception e1) {
                        // ignore
                    }
                    logger.error(exp.getMessage(), exp);
                }
            } else if (exp instanceof ServerVersionOlderException) {
                BgcPlugin.openError(
                    "Server Version Error",
                    exp.getMessage(), exp);
            } else if (exp instanceof ClientVersionInvalidException) {
                BgcPlugin.openError("Client Version Error",
                    "Cannot connect to this server because the Java Client version is invalid.", exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BgcPlugin.openAsyncError(
                    "Login Failed",
                    "Bad credentials. Warning: You will be locked out after 3 failed login attempts.", exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAccessException) {
                BgcPlugin.openAsyncError(
                    "Login Failed",
                    "Error contacting server.", exp);
            }
        } catch (Exception exp) {
            BgcPlugin.openAsyncError("Login Failed",
                exp);
        }
    }

    public BiobankApplicationService getAppService() {
        return appService;
    }

    public UserWrapper getUser() {
        return user;
    }
}
