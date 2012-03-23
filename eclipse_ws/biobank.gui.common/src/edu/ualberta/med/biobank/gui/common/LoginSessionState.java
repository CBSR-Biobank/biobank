package edu.ualberta.med.biobank.gui.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class LoginSessionState extends AbstractSourceProvider {

    public final static String LOGIN_STATE_SOURCE_NAME = "edu.ualberta.med.biobank.gui.common.sourceprovider.loginState"; //$NON-NLS-1$

    public final static String LOGGED_IN = "loggedIn"; //$NON-NLS-1$

    public final static String LOGGED_OUT = "loggedOut"; //$NON-NLS-1$

    private boolean loggedIn;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { LOGIN_STATE_SOURCE_NAME };
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

    public void setLoggedInState(boolean loggedIn) {
        if (this.loggedIn == loggedIn)
            return; // no change
        this.loggedIn = loggedIn;
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        fireSourceChanged(ISources.WORKBENCH, LOGIN_STATE_SOURCE_NAME,
            currentState);
    }

}
