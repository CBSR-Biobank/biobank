package edu.ualberta.med.biobank.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {

    private static String TRUST_STORE_PROPERTY_NAME = "javax.net.ssl.trustStore";

    private static String KEYSTORE_FILE_PATH = "cert/all.keystore";

    public static BiobankApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        if (serverUrl.startsWith("https")
            && System.getProperty(TRUST_STORE_PROPERTY_NAME) == null) {
            setTrustStore();
        }
        BiobankApplicationService appService = null;
        if (userName == null) {
            appService = (BiobankApplicationService) ApplicationServiceProvider
                .getApplicationServiceFromUrl(serverUrl);
        } else {
            appService = (BiobankApplicationService) ApplicationServiceProvider
                .getApplicationServiceFromUrl(serverUrl, userName, password);
        }
        appService.logActivity("login", null, null, null, null, null);
        return appService;
    }

    private static void setTrustStore() throws Exception {
        // export the keystore into a temporary file (can't use it from inside a
        // jar)
        File tf = File.createTempFile("biobank2", ".keystore");
        tf.deleteOnExit();
        byte buffer[] = new byte[0x1000];
        InputStream is = ServiceConnection.class
            .getResourceAsStream(KEYSTORE_FILE_PATH);
        FileOutputStream out = new FileOutputStream(tf);
        int cnt;
        while ((cnt = is.read(buffer)) != -1)
            out.write(buffer, 0, cnt);
        is.close();
        out.close();
        System.setProperty(TRUST_STORE_PROPERTY_NAME, tf.getAbsolutePath());
    }

    public static WritableApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }

    public static void logout(WritableApplicationService appService) {
        ((BiobankApplicationService) appService).logActivity("logout", null,
            null, null, null, null);
    }
}
