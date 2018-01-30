package edu.ualberta.med.biobank.tools.maintenancemode;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.client.util.TrustStore;
import edu.ualberta.med.biobank.client.util.TrustStore.Cert;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService.MaintenanceMode;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.utils.HostUrl;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * A tool that can be used to change or query the Biobank Server's Maintenante Mode setting. This
 * tool can be run as a command from the project's maint Ant build file or standalone.
 *
 * @author loyola
 *
 */
public class MaintenanceModeTool {

    private static String USAGE =
        "Usage: maintenancemode [options]\n\n"
            + "Options\n"
            + "  -H, --hostname   hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n";

    private static final Logger log = LoggerFactory.getLogger(MaintenanceModeTool.class);

    private BiobankApplicationService appService;

    public static void main(String[] argv) {
        try {
            log.trace("args: " + StringUtils.join(argv, " "));
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.helpOption()) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new MaintenanceModeTool(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MaintenanceModeTool(GenericAppArgs appArgs) throws Exception {
        String hostUrl = HostUrl.getHostUrl(appArgs.hostOption(), appArgs.portOption());

        try {
            checkCertificates(hostUrl);

            appService = ServiceConnection.getAppService(hostUrl,
                                                         appArgs.userOption(),
                                                         appArgs.passwordOption());

            String[] remainingArgs = appArgs.getRemainingArgs();

            if (remainingArgs.length != 1) {
                System.out.println("ERROR: invalid argument(s): "
                    + StringUtils.join(remainingArgs, " "));
            } else if (remainingArgs[0].equals("query")) {
                maintenanceModeQuery();
            } else if (remainingArgs[0].equals("toggle")) {
                maintenanceModeQueryToggle();
            } else {
                System.out.println("ERROR: invalid argument(s): "
                    + StringUtils.join(remainingArgs, " "));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void maintenanceModeQuery() {
        MaintenanceMode mode = appService.maintenanceMode();
        System.out.print("Server maintenance mode is ");
        System.out.println(mode == MaintenanceMode.NONE ? "disabled" : "enabled");
    }

    private void maintenanceModeQueryToggle() {
        MaintenanceMode mode = appService.maintenanceMode();
        MaintenanceMode newMode;

        switch (mode) {
        case NONE:
            newMode = MaintenanceMode.PREVENT_USER_LOGIN;
            break;
        case PREVENT_USER_LOGIN:
            newMode = MaintenanceMode.NONE;
            break;
        default:
            throw new IllegalStateException("server's maintenance mode is invalid: " + mode);
        }

        try {
            appService.maintenanceMode(newMode);
            System.out.print("Server maintenance mode changed to ");
            System.out.println(newMode == MaintenanceMode.NONE ? "disabled" : "enabled");
        } catch (LocalizedException e) {
            System.out.println("ERROR: " + e.getLocalizedString());
        } catch (ApplicationException e) {
            System.out.println("ERROR: " + e.getLocalizedMessage());
        }

    }

    @SuppressWarnings("nls")
    private boolean checkCertificates(String serverUrl)
        throws KeyManagementException, NoSuchAlgorithmException,
        KeyStoreException, UnknownHostException, IOException,
        CertificateException {
        TrustStore ts = TrustStore.getInstance();
        List<Cert> untrustedCerts = ts.getUntrustedCerts(serverUrl);
        boolean restartPending = false;

        if (!untrustedCerts.isEmpty()) {
            for (Cert untrustedCert : untrustedCerts) {
                untrustedCert.trust();
            }

            System.out.println("SSL certificate updated. Run this tool again to connect to the specified server.");
            System.exit(-1);
        }

        return restartPending;
    }

}
