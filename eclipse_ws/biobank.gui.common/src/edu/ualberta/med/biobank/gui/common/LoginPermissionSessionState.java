package edu.ualberta.med.biobank.gui.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.common.action.security.UserPermissionsGetAction.UserCreatePermissions;

public class LoginPermissionSessionState extends AbstractSourceProvider {

    @SuppressWarnings("nls")
    public final static String LOGIN_STATE_SOURCE_NAME =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.loginState";

    @SuppressWarnings("nls")
    public static final String LABEL_PRINTING_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.labelPrintingPermission";

    @SuppressWarnings("nls")
    public final static String LOGGED_IN = "loggedIn";

    @SuppressWarnings("nls")
    public final static String LOGGED_OUT = "loggedOut";

    private boolean loggedIn;

    private UserCreatePermissions userCreatePermissions;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { LOGIN_STATE_SOURCE_NAME,
            LABEL_PRINTING_PERMISSION };
    }

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        currentStateMap.put(LOGIN_STATE_SOURCE_NAME, currentState);
        return currentStateMap;
    }

    public void setLoggedInState(boolean loggedIn,
        UserCreatePermissions userCreatePermissions) {
        if (this.loggedIn == loggedIn)
            return; // no change
        this.loggedIn = loggedIn;
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;

        this.userCreatePermissions = userCreatePermissions;

        fireSourceChanged(ISources.WORKBENCH, LOGIN_STATE_SOURCE_NAME,
            currentState);
    }

}
