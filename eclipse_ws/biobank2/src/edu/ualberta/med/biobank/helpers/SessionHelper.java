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

    private static final String DOWNLOAD_URL = "http://aicml-med.cs.ualberta.ca/CBSR/latest.html"; //$NON-NLS-1$

    private static final String DEFAULT_TEST_USER = "testuser"; //$NON-NLS-1$

    private static final String DEFAULT_TEST_USER_PWD = "test"; //$NON-NLS-1$

    private static final String SECURE_CONNECTION_URI = "https://"; //$NON-NLS-1$

    private static final String NON_SECURE_CONNECTION_URI = "http://"; //$NON-NLS-1$

    private static final String BIOBANK_URL = "/biobank"; //$NON-NLS-1$

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
            logger.debug(Messages.SessionHelper_clientVersion_debug_msg
                + clientVersion);
            appService.checkVersion(clientVersion);
            user = UserWrapper.getUser(appService, userName);
        } catch (ApplicationException exp) {
            if (exp instanceof ServerVersionInvalidException) {
                BgcPlugin.openError(
                    Messages.SessionHelper_server_version_error_title,
                    Messages.SessionHelper_server_noversion_error_msg, exp);
            } else if (exp instanceof ServerVersionNewerException) {
                if (BgcPlugin.openConfirm(
                    Messages.SessionHelper_server_version_error_title,
                    Messages.SessionHelper_server_oldversion_error_msg)) {
                    try {
                        Desktop.getDesktop().browse(new URI(DOWNLOAD_URL));
                    } catch (Exception e1) {
                        // ignore
                    }
                    logger.error(
                        Messages.SessionHelper_server_oldversion_log_msg, exp);
                }
            } else if (exp instanceof ServerVersionOlderException) {
                BgcPlugin.openError(
                    Messages.SessionHelper_server_version_error_title,
                    Messages.SessionHelper_server_newversion_error_msg, exp);
            } else if (exp instanceof ClientVersionInvalidException) {
                BgcPlugin.openError(Messages.SessionHelper_client_error_title,
                    Messages.SessionHelper_client_invalid_error_msg, exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BgcPlugin.openAsyncError(
                    Messages.SessionHelper_login_error_title,
                    Messages.SessionHelper_login_error_msg, exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAccessException) {
                BgcPlugin.openAsyncError(
                    Messages.SessionHelper_login_error_title,
                    Messages.SessionHelper_login_server_error_msg, exp);
            }
        } catch (Exception exp) {
            BgcPlugin.openAsyncError(Messages.SessionHelper_login_error_title,
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
