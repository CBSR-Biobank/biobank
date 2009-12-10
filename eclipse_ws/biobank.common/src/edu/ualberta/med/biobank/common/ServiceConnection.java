package edu.ualberta.med.biobank.common;

import java.net.URL;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {

    static {
        URL url = ServiceConnection.class
            .getResource("cert/localhost.keystore");
        System.setProperty("javax.net.ssl.trustStore", url.getFile());
    }

    public static WritableApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        if (userName == null) {
            return (WritableApplicationService) ApplicationServiceProvider
                .getApplicationServiceFromUrl(serverUrl);
        }
        return (WritableApplicationService) ApplicationServiceProvider
            .getApplicationServiceFromUrl(serverUrl, userName, password);
    }

    public static WritableApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }
}
