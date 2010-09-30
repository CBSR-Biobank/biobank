package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SessionState extends AbstractSourceProvider {
    public final static String LOGIN_STATE_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.loginState";
    public final static String IS_WEB_ADMIN_SOURCE_NAME = "edu.ualberta.med.biobank.sourceprovider.isWebAdmin";
    public final static String LOGGED_IN = "loggedIn";
    public final static String LOGGED_OUT = "loggedOut";
    boolean loggedIn;
    boolean isWebAdmin;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { LOGIN_STATE_SOURCE_NAME, IS_WEB_ADMIN_SOURCE_NAME };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        currentStateMap.put(LOGIN_STATE_SOURCE_NAME, currentState);
        currentStateMap.put(IS_WEB_ADMIN_SOURCE_NAME,
            Boolean.toString((isWebAdmin)));
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
        fireSourceChanged(ISources.WORKBENCH, LOGIN_STATE_SOURCE_NAME, currentState);
    }

    public void setWebAdmin(boolean isWebAdmin) {
        if (this.isWebAdmin == isWebAdmin) {
            return;
        }

        this.isWebAdmin = isWebAdmin;
        // note: must use a boolean object for the sourceValue, NOT a String
        // with value "true" or "false"
        fireSourceChanged(ISources.WORKBENCH, IS_WEB_ADMIN_SOURCE_NAME,
            isWebAdmin);
    }
}