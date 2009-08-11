package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SessionState extends AbstractSourceProvider {
    public final static String SESSION_STATE = "edu.ualberta.med.biobank.sourceprovider.loginState";
    private final static String LOGGED_IN = "loggedIn";
    private final static String LOGGED_OUT = "loggedOut";
    boolean loggedIn;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SESSION_STATE };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        currentStateMap.put(SESSION_STATE, currentState);
        return currentStateMap;
    }

    @Override
    public void dispose() {
    }

    public void setLoggedIn(boolean loggedIn) {
        if (this.loggedIn == loggedIn)
            return; // no change
        this.loggedIn = loggedIn;
        String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
        fireSourceChanged(ISources.WORKBENCH, SESSION_STATE, currentState);
    }
}