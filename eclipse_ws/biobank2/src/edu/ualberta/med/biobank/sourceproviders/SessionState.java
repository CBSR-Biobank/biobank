package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SessionState extends AbstractSourceProvider {
    public final static String LOGIN_STATE_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.loginState";
    public final static String IS_SUPER_ADMIN_MODE_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.isSuperAdminMode";
    public final static String HAS_WORKING_CENTER_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.hasWorkingCenter";
    public final static String LOGGED_IN = "loggedIn";
    public final static String LOGGED_OUT = "loggedOut";
    boolean loggedIn;
    boolean isSuperAdminMode;
    private boolean hasWorkingCenter;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { LOGIN_STATE_SOURCE_NAME,
            IS_SUPER_ADMIN_MODE_SOURCE_NAME, HAS_WORKING_CENTER_SOURCE_NAME };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        currentStateMap.put(LOGIN_STATE_SOURCE_NAME, currentState);
        currentStateMap.put(IS_SUPER_ADMIN_MODE_SOURCE_NAME,
            Boolean.toString((isSuperAdminMode)));
        currentStateMap.put(HAS_WORKING_CENTER_SOURCE_NAME,
            Boolean.toString(hasWorkingCenter));
        return currentStateMap;
    }

    @Override
    public void dispose() {
    }

    public void setLoggedInState(boolean loggedIn) {
        if (this.loggedIn == loggedIn)
            return; // no change
        this.loggedIn = loggedIn;
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        fireSourceChanged(ISources.WORKBENCH, LOGIN_STATE_SOURCE_NAME,
            currentState);
    }

    public void setSuperAdminMode(boolean isSuperAdminMode) {
        if (this.isSuperAdminMode == isSuperAdminMode) {
            return;
        }

        this.isSuperAdminMode = isSuperAdminMode;
        // note: must use a boolean object for the sourceValue, NOT a String
        // with value "true" or "false"
        fireSourceChanged(ISources.WORKBENCH, IS_SUPER_ADMIN_MODE_SOURCE_NAME,
            isSuperAdminMode);
    }

    public void setHasWorkingCenter(boolean hasWorkingCenter) {
        if (this.hasWorkingCenter == hasWorkingCenter)
            return; // no change
        this.hasWorkingCenter = hasWorkingCenter;
        fireSourceChanged(ISources.WORKBENCH, HAS_WORKING_CENTER_SOURCE_NAME,
            hasWorkingCenter);
    }

}