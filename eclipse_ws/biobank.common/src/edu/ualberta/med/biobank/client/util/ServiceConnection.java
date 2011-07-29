package edu.ualberta.med.biobank.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {

    private static String TRUST_STORE_PROPERTY_NAME = "javax.net.ssl.trustStore"; //$NON-NLS-1$

    private static String KEYSTORE_FILE_PATH = "cert/all.keystore"; //$NON-NLS-1$

    public static BiobankApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        if (serverUrl.startsWith("https") //$NON-NLS-1$
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
        Log logMessage = new Log();
        logMessage.action = "login"; //$NON-NLS-1$
        appService.logActivity(logMessage);
        return appService;
    }

    private static void setTrustStore() throws Exception {
        // export the keystore into a temporary file (can't use it from inside a
        // jar)
        File tf = File.createTempFile("biobank", ".keystore"); //$NON-NLS-1$ //$NON-NLS-2$
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

    public static BiobankApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }

    public static void logout(WritableApplicationService appService)
        throws Exception {
        if (appService != null) {
            Log logMessage = new Log();
            logMessage.action = "logout"; //$NON-NLS-1$
            ((BiobankApplicationService) appService).logActivity(logMessage);
        }
    }
}
