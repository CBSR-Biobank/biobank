package edu.ualberta.med.biobank.tools;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import org.apache.commons.cli.ParseException;

import edu.ualberta.med.biobank.client.util.TrustStore;
import edu.ualberta.med.biobank.client.util.TrustStore.Cert;

public abstract class Application {

    private static Boolean firstConnection = true;

    protected Application(String appName, String header, GenericAppArgs appArgs) {
        try {
            if (appArgs.error) {
                System.out.println("Error: " + appArgs.errorMsg + "\n");
                appArgs.printHelp(appName);
                System.exit(1);
            }

            if (appArgs.helpOption()) {
                appArgs.printUsage(appName, header);
                System.exit(0);
            }

            start(appArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Application(String appName, String usage, String[] argv) throws ParseException {
        this(appName, usage, new GenericAppArgs(argv));

    }

    protected abstract void start(GenericAppArgs args) throws Exception;

    protected boolean checkCertificates(String serverUrl)
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

            if (!firstConnection) {
                // restart because the trustStore has been read from once.
                System.out.println(
                    "Restart Required\n\n"
                        + "The application must be restarted to connect to the entered server.");
                System.exit(-1);
            }
        }

        return restartPending;
    }
}
