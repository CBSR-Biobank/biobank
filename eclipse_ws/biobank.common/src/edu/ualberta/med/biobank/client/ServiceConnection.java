package edu.ualberta.med.biobank.client;

import java.net.URL;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {

    /**
     * if not null, called to resolved the address
     */
    private static ResourceResolver resourceResolver;

    private static String TRUST_STORE_PROPERTY_NAME = "javax.net.ssl.trustStore";

    private static String KEYSTORE_FILE_PATH = "cert/all.keystore";

    public static BiobankApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        return getAppService(serverUrl, ServiceConnection.class
            .getResource(KEYSTORE_FILE_PATH), userName, password);
    }

    public static BiobankApplicationService getAppService(String serverUrl,
        URL trustStoreUrl, String userName, String password) throws Exception {
        if (serverUrl.startsWith("https")
            && System.getProperty(TRUST_STORE_PROPERTY_NAME) == null) {
            setTrustStore(trustStoreUrl);
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

    public static void setTrustStore() throws Exception {
        setTrustStore(ServiceConnection.class.getResource(KEYSTORE_FILE_PATH));
    }

    public static void setTrustStore(URL url) throws Exception {
        if (url == null)
            return;
        if (resourceResolver != null) {
            url = resourceResolver.resolveURL(url);
        }
        System.setProperty(TRUST_STORE_PROPERTY_NAME, url.getFile());
    }

    public static WritableApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }

    public static void setResourceResolver(ResourceResolver resourceResolver) {
        ServiceConnection.resourceResolver = resourceResolver;
    }

    public static ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public static void logout(WritableApplicationService appService) {
        ((BiobankApplicationService) appService).logActivity("logout", null,
            null, null, null, null);
    }
}
