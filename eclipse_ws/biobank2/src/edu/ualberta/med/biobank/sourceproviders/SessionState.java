package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;

public class SessionState extends AbstractSourceProvider {
    public final static String SESSION_STATE_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.loginState"; //$NON-NLS-1$

    public final static String IS_SUPER_ADMIN_MODE_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.isSuperAdminMode"; //$NON-NLS-1$
    public final static String HAS_WORKING_CENTER_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.hasWorkingCenter"; //$NON-NLS-1$
    public final static String HAS_CLINIC_SHIPMENT_RIGHTS = "edu.ualberta.med.biobank.sourceprovider.clinicShipmentRights"; //$NON-NLS-1$
    public final static String HAS_DISPATCH_RIGHTS = "edu.ualberta.med.biobank.sourceprovider.dispatchRights"; //$NON-NLS-1$
    public final static String IS_CURRENT_CENTER_ADMIN_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.isCurrentCenterAdmin"; //$NON-NLS-1$
    public final static String HAS_PRINTER_LABELS_RIGHTS = "edu.ualberta.med.biobank.sourceprovider.hasPrinterLabelsRights"; //$NON-NLS-1$
    public final static String CURRENT_CENTER_TYPE = "edu.ualberta.med.biobank.sourceprovider.currentCenterType"; //$NON-NLS-1$

    private boolean isSuperAdminMode;
    private boolean hasWorkingCenter;
    private boolean hasClinicShipmentRights;
    private boolean hasDispatchRights;
    private boolean isCurrentCenterAdmin;
    private boolean hasPrinterLabelsRights;
    private String currentCenterType = ""; //$NON-NLS-1$

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SESSION_STATE_SOURCE_NAME,
            IS_SUPER_ADMIN_MODE_SOURCE_NAME, HAS_WORKING_CENTER_SOURCE_NAME,
            HAS_CLINIC_SHIPMENT_RIGHTS, HAS_DISPATCH_RIGHTS,
            IS_CURRENT_CENTER_ADMIN_SOURCE_NAME, HAS_PRINTER_LABELS_RIGHTS,
            CURRENT_CENTER_TYPE };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        currentStateMap.put(IS_SUPER_ADMIN_MODE_SOURCE_NAME,
            Boolean.toString((isSuperAdminMode)));
        currentStateMap.put(HAS_WORKING_CENTER_SOURCE_NAME,
            Boolean.toString(hasWorkingCenter));
        currentStateMap.put(HAS_CLINIC_SHIPMENT_RIGHTS,
            Boolean.toString(hasClinicShipmentRights));
        currentStateMap.put(HAS_DISPATCH_RIGHTS,
            Boolean.toString(hasDispatchRights));
        currentStateMap.put(IS_CURRENT_CENTER_ADMIN_SOURCE_NAME,
            Boolean.toString(isCurrentCenterAdmin));
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

    private void setIsCurrentCenterAdmin(boolean isCurrentCenterAdmin) {
        if (this.isCurrentCenterAdmin == isCurrentCenterAdmin)
            return; // no change
        this.isCurrentCenterAdmin = isCurrentCenterAdmin;
        fireSourceChanged(ISources.WORKBENCH,
            IS_CURRENT_CENTER_ADMIN_SOURCE_NAME, isCurrentCenterAdmin);
    }

    private void setHasClinicShipmentRights(boolean hasClinicShipmentRights) {
        if (this.hasClinicShipmentRights == hasClinicShipmentRights)
            return; // no change
        this.hasClinicShipmentRights = hasClinicShipmentRights;
        fireSourceChanged(ISources.WORKBENCH, HAS_CLINIC_SHIPMENT_RIGHTS,
            hasClinicShipmentRights);
    }

    private void setDispatchRights(boolean hasDispatchRights) {
        if (this.hasDispatchRights == hasDispatchRights)
            return; // no change
        this.hasDispatchRights = hasDispatchRights;
        fireSourceChanged(ISources.WORKBENCH, HAS_DISPATCH_RIGHTS,
            hasDispatchRights);
    }

    private void setHasPrinterLabelsRights(boolean hasPrinterLabelsRights) {
        if (this.hasPrinterLabelsRights == hasPrinterLabelsRights)
            return; // no change
        this.hasPrinterLabelsRights = hasPrinterLabelsRights;
        fireSourceChanged(ISources.WORKBENCH, HAS_PRINTER_LABELS_RIGHTS,
            hasPrinterLabelsRights);
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

    public void setUser(User user) {
        setSuperAdminMode(user != null && user.isInSuperAdminMode());
        setHasWorkingCenter(user != null
            && user.getCurrentWorkingCenter() != null);
        setCurrentCenterType((user == null) ? null : user
            .getCurrentWorkingCenter());
        setHasClinicShipmentRights(user != null
            && user.canPerformActions(SecurityFeature.CLINIC_SHIPMENT));
        setDispatchRights(user != null
            && user.canPerformActions(SecurityFeature.DISPATCH_REQUEST));
        setIsCurrentCenterAdmin(user != null
            && user.isAdministratorForCurrentCenter());
        setHasPrinterLabelsRights(user != null
            && user.canPerformActions(SecurityFeature.PRINTER_LABELS));
    }
}