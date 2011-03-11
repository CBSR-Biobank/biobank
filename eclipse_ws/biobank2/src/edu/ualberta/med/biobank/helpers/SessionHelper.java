package edu.ualberta.med.biobank.helpers;

import java.awt.Desktop;
import java.net.URI;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.eclipse.core.runtime.Platform;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SessionHelper implements Runnable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SessionHelper.class.getName());

    private String serverUrl;

    private String userName;

    private String password;

    private BiobankApplicationService appService;

    private User user;

    private static final String DOWNLOAD_URL = "http://aicml-med.cs.ualberta.ca/CBSR/latest.html";

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
    }

    @Override
    public void run() {
        try {
            if (userName.length() == 0) {
                if (BiobankPlugin.getDefault().isDebugging()) {
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
            String clientVersion = Platform.getProduct().getDefiningBundle()
                .getVersion().toString();
            appService.checkVersion(clientVersion);
            user = appService.getCurrentUser();
        } catch (ApplicationException exp) {
            if (exp instanceof ServerVersionInvalidException) {
                BiobankPlugin
                    .openInformation(
                        "Server Version Error",
                        "The server you are connecting to does not have a version. Cannot authenticate.");
                logger.error("Error while logging to application", exp);
            } else if (exp instanceof ServerVersionNewerException) {
                if (BiobankPlugin.openConfirm("Server Version Error",
                    "Cannot connect to this server because the Java Client version is too old.\n"
                        + "Would you like to download the latest version?")) {
                    try {
                        Desktop.getDesktop().browse(new URI(DOWNLOAD_URL));
                    } catch (Exception e1) {
                        // ignore
                    }
                    logger.error("Error while logging to application", exp);
                }
            } else if (exp instanceof ServerVersionOlderException) {
                BiobankPlugin
                    .openInformation("Server Version Error",
                        "Cannot connect to this server because the Java Client version is too new.");
                logger.error("Error while logging to application", exp);
            } else if (exp instanceof ClientVersionInvalidException) {
                BiobankPlugin
                    .openInformation("Client Version Error",
                        "Cannot connect to this server because the Java Client version is invalid.");
                logger.error("Error while logging to application", exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BiobankPlugin
                    .openAsyncError(
                        "Login Failed",
                        "Bad credentials. Warning: You will be locked out after 3 failed login attempts.");
                return;
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAccessException) {
                BiobankPlugin.openAsyncError("Login Failed",
                    "Error contacting server.");
            }
        } catch (Exception exp) {
            BiobankPlugin.openAsyncError("Login Failed", exp);
        }
    }

    public BiobankApplicationService getAppService() {
        return appService;
    }

    public User getUser() {
        return user;
    }
}
