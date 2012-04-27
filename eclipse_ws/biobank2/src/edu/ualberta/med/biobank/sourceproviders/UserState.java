package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.LoginPermissionSessionState;

public class UserState extends AbstractSourceProvider {

    private static BgcLogger logger = BgcLogger.getLogger(UserState.class
        .getName());

    public final static String HAS_WORKING_CENTER_SOURCE_NAME =
        "edu.ualberta.med.biobank.sourceprovider.hasWorkingCenter"; //$NON-NLS-1$

    private boolean hasWorkingCenter;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { HAS_WORKING_CENTER_SOURCE_NAME };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        currentStateMap.put(HAS_WORKING_CENTER_SOURCE_NAME,
            Boolean.toString(hasWorkingCenter));
        return currentStateMap;
    }

    @Override
    public void dispose() {
    }

    private void setHasWorkingCenter(boolean hasWorkingCenter) {
        if (this.hasWorkingCenter == hasWorkingCenter)
            return; // no change
        this.hasWorkingCenter = hasWorkingCenter;
        fireSourceChanged(ISources.WORKBENCH, HAS_WORKING_CENTER_SOURCE_NAME,
            hasWorkingCenter);
    }

    public void setUser(UserWrapper user) {
        try {
            setHasWorkingCenter(user != null
                && user.getCurrentWorkingCenter() != null);
        } catch (Exception e) {
            logger.error("Error setting session state", e); //$NON-NLS-1$
        }
    }

    public static AbstractSourceProvider getUserStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        return (LoginPermissionSessionState) service
            .getSourceProvider(UserState.HAS_WORKING_CENTER_SOURCE_NAME);
    }

}