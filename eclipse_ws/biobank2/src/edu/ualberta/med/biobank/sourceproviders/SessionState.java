package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class SessionState extends AbstractSourceProvider {

    public final static String SESSION_STATE_SOURCE_NAME =
        "edu.ualberta.med.biobank.sourceprovider.loginState"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger.getLogger(SessionState.class
        .getName());

    public final static String IS_SUPER_ADMIN_MODE_SOURCE_NAME =
        "edu.ualberta.med.biobank.sourceprovider.isSuperAdminMode"; //$NON-NLS-1$
    public final static String HAS_WORKING_CENTER_SOURCE_NAME =
        "edu.ualberta.med.biobank.sourceprovider.hasWorkingCenter"; //$NON-NLS-1$
    public final static String HAS_PRINTER_LABELS_RIGHTS =
        "edu.ualberta.med.biobank.sourceprovider.hasPrinterLabelsRights"; //$NON-NLS-1$
    public final static String HAS_USER_MANAGEMENT_RIGHTS =
        "edu.ualberta.med.biobank.sourceprovider.hasUserManagementRights"; //$NON-NLS-1$
    public final static String CURRENT_CENTER_TYPE =
        "edu.ualberta.med.biobank.sourceprovider.currentCenterType"; //$NON-NLS-1$

    private boolean isSuperAdminMode;
    private boolean hasWorkingCenter;
    private boolean hasPrinterLabelsRights;
    private boolean hasUserManagementRights;
    private String currentCenterType = ""; //$NON-NLS-1$

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SESSION_STATE_SOURCE_NAME,
            IS_SUPER_ADMIN_MODE_SOURCE_NAME, HAS_WORKING_CENTER_SOURCE_NAME,
            HAS_PRINTER_LABELS_RIGHTS, CURRENT_CENTER_TYPE };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        currentStateMap.put(IS_SUPER_ADMIN_MODE_SOURCE_NAME,
            Boolean.toString((isSuperAdminMode)));
        currentStateMap.put(HAS_WORKING_CENTER_SOURCE_NAME,
            Boolean.toString(hasWorkingCenter));
        currentStateMap.put(HAS_PRINTER_LABELS_RIGHTS,
            Boolean.toString(hasPrinterLabelsRights));
        currentStateMap.put(CURRENT_CENTER_TYPE, currentCenterType);
        return currentStateMap;
    }

    @Override
    public void dispose() {
    }

    private void setSuperAdminMode(boolean isSuperAdminMode) {
        if (this.isSuperAdminMode == isSuperAdminMode) {
            return;
        }

        this.isSuperAdminMode = isSuperAdminMode;
        // note: must use a boolean object for the sourceValue, NOT a String
        // with value "true" or "false"
        fireSourceChanged(ISources.WORKBENCH, IS_SUPER_ADMIN_MODE_SOURCE_NAME,
            isSuperAdminMode);
    }

    private void setHasWorkingCenter(boolean hasWorkingCenter) {
        if (this.hasWorkingCenter == hasWorkingCenter)
            return; // no change
        this.hasWorkingCenter = hasWorkingCenter;
        fireSourceChanged(ISources.WORKBENCH, HAS_WORKING_CENTER_SOURCE_NAME,
            hasWorkingCenter);
    }

    private void setHasPrinterLabelsRights(boolean hasPrinterLabelsRights) {
        if (this.hasPrinterLabelsRights == hasPrinterLabelsRights)
            return; // no change
        this.hasPrinterLabelsRights = hasPrinterLabelsRights;
        fireSourceChanged(ISources.WORKBENCH, HAS_PRINTER_LABELS_RIGHTS,
            hasPrinterLabelsRights);
    }

    private void setHasUserManagementRights(boolean hasUserManagementRights) {
        if (this.hasUserManagementRights == hasUserManagementRights)
            return; // no change
        this.hasUserManagementRights = hasUserManagementRights;
        fireSourceChanged(ISources.WORKBENCH, HAS_USER_MANAGEMENT_RIGHTS,
            hasUserManagementRights);
    }

    private void setCurrentCenterType(CenterWrapper<?> currentCenter) {
        String type = ""; //$NON-NLS-1$
        if (currentCenter != null) {
            type = currentCenter.getWrappedClass().getSimpleName();
        }
        if (currentCenterType.equals(type))
            return; // no change
        currentCenterType = type;
        fireSourceChanged(ISources.WORKBENCH, CURRENT_CENTER_TYPE,
            currentCenterType);
    }

    public void setUser(UserWrapper user) {
        try {
            setSuperAdminMode(user != null && user.isInSuperAdminMode());
            setHasWorkingCenter(user != null
                && user.getCurrentWorkingCenter() != null);
            setCurrentCenterType((user == null) ? null : user
                .getCurrentWorkingCenter());
            // setHasPrinterLabelsRights(user != null
            // && SessionManager.getAppService().isAllowed(
            // new PrinterLabelPermission()));
            // FIXME need this permission implemented
            setHasUserManagementRights(user != null
                && SessionManager.getAppService().isAllowed(
                    new UserManagerPermission()));
        } catch (Exception e) {
            logger.error("Error setting session state", e); //$NON-NLS-1$
        }
    }
}