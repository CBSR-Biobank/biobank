package edu.ualberta.med.biobank.client.util;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

public class ServiceConnection {
    public static BiobankApplicationService getAppService(String serverUrl,
        String userName, String password) throws Exception {
        BiobankApplicationService appService = null;
        if (userName == null) {
            appService = (BiobankApplicationService) ApplicationServiceProvider
                .getApplicationServiceFromUrl(serverUrl);
        } else {
            appService = (BiobankApplicationService)
                ApplicationServiceProvider.getApplicationServiceFromUrl(
                    serverUrl, userName, password);
        }
        Log logMessage = new Log();
        logMessage.setAction("login"); //$NON-NLS-1$
        appService.logActivity(logMessage);
        return appService;
    }

    public static BiobankApplicationService getAppService(String serverUrl)
        throws Exception {
        return getAppService(serverUrl, null, null);
    }

    public static void logout(WritableApplicationService appService)
        throws Exception {
        if (appService != null) {
            Log logMessage = new Log();
            logMessage.setAction("logout"); //$NON-NLS-1$
            ((BiobankApplicationService) appService).logActivity(logMessage);
        }
    }
}
