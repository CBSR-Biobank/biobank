package edu.ualberta.med.biobank.helpers;

import java.awt.Desktop;
import java.net.URI;

import org.acegisecurity.providers.rcp.RemoteAuthenticationException;
import org.eclipse.core.runtime.Platform;
import org.springframework.remoting.RemoteAccessException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
    private static final I18n i18n = I18nFactory.getI18n(SessionHelper.class);

    private static BgcLogger logger = BgcLogger.getLogger(SessionHelper.class
        .getName());

    private String serverUrl;

    private String userName;

    private final String password;

    private BiobankApplicationService appService;

    private UserWrapper user;

    @SuppressWarnings("nls")
    private static final String DOWNLOAD_URL =
        "http://aicml-med.cs.ualberta.ca/CBSR/latest.html";

    @SuppressWarnings("nls")
    private static final String DEFAULT_TEST_USER = "testuser";

    @SuppressWarnings("nls")
    private static final String DEFAULT_TEST_USER_PWD = "test";

    @SuppressWarnings("nls")
    private static final String SECURE_CONNECTION_URI = "https://";

    @SuppressWarnings("nls")
    private static final String NON_SECURE_CONNECTION_URI = "http://";

    @SuppressWarnings("nls")
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

    @SuppressWarnings("nls")
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
                BgcPlugin
                    .openError(
                        // dialog title.
                        i18n.tr("Server Version Error"),
                        // dialog message.
                        i18n.tr("The server you are connecting to does not have a version. Cannot authenticate."),
                        exp);
            } else if (exp instanceof ServerVersionNewerException) {
                if (BgcPlugin.openConfirm(
                    // dialog title.
                    i18n.tr("Server Version Error"),
                    // dialog message. {0} is an exception message.
                    i18n.tr(
                        "{0} Would you like to download the latest version?",
                        exp.getMessage()))) {
                    try {
                        Desktop.getDesktop().browse(new URI(DOWNLOAD_URL));
                    } catch (Exception e1) {
                        // ignore
                    }
                    logger.error(exp.getMessage(), exp);
                }
            } else if (exp instanceof ServerVersionOlderException) {
                BgcPlugin.openError(
                    // dialog title.
                    i18n.tr("Server Version Error"),
                    exp.getMessage(), exp);
            } else if (exp instanceof ClientVersionInvalidException) {
                BgcPlugin
                    .openError(
                        // dialog title.
                        i18n.tr("Client Version Error"),
                        // dialog message.
                        i18n.tr("Cannot connect to this server because the Java Client version is invalid."),
                        exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAuthenticationException) {
                BgcPlugin
                    .openAsyncError(
                        // dialog title.
                        i18n.tr("Login Failed"),
                        // dialog message.
                        i18n.tr("Bad credentials. Warning: You will be locked out after 3 failed login attempts."),
                        exp);
            } else if (exp.getCause() != null
                && exp.getCause() instanceof RemoteAccessException) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Login Failed"),
                    // dialog message.
                    i18n.tr("Error contacting server."), exp);
            }
        } catch (Exception exp) {
            BgcPlugin.openAsyncError(
                // dialog title.
                i18n.tr("Login Failed"),
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
