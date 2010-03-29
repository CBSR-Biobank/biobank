package edu.ualberta.med.biobank.common;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {

    /**
     * if not null, called to resolved the address
     */
    private static ResourceResolver resourceResolver;

    public static WritableApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        if (serverUrl.startsWith("https")) {
            // URL url =
            // ServiceConnection.class.getResource("cert/all.keystore");
            // if (url != null) {
            // if (resourceResolver != null) {
            // url = resourceResolver.resolveURL(url);
            // }
            // System.setProperty("javax.net.ssl.trustStore", url.getFile());
            // }
        }
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

    public static void setResourceResolver(ResourceResolver resourceResolver) {
        ServiceConnection.resourceResolver = resourceResolver;
    }
}
