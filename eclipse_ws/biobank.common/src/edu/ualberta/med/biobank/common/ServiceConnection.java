package edu.ualberta.med.biobank.common;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.net.URL;

public class ServiceConnection {

    private static WritableApplicationService appService;

    public static WritableApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        if (appService == null) {
            if (serverUrl.startsWith("https")) {
                String name = serverUrl.substring("https://".length());
                name = name.substring(0, name.indexOf(":"));
                URL url = ServiceConnection.class.getResource("cert/" + name
                    + ".keystore");
                System.setProperty("javax.net.ssl.trustStore", url.getFile());
            }
            if (userName == null) {
                appService = (WritableApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serverUrl);
            } else {
                appService = (WritableApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serverUrl, userName, password);
            }
        }
        return appService;
    }

    public static WritableApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }
}
