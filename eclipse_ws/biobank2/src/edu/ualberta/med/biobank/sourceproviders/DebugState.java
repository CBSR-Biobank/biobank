package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class DebugState extends AbstractSourceProvider {
    public final static String SESSION_STATE = "edu.ualberta.med.biobank.sourceprovider.debugState";
    private final static String DEBUG = "debug";
    private final static String NON_DEBUG = "nonDebug";

    // true for debug, false for non-debug
    boolean state;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SESSION_STATE };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = state ? DEBUG : NON_DEBUG;
        currentStateMap.put(SESSION_STATE, currentState);
        return currentStateMap;
    }

    @Override
    public void dispose() {
        //
    }

    public void setState(boolean state) {
        if (this.state == state)
            return; // no change
        this.state = state;
        String currentState = state ? DEBUG : NON_DEBUG;
        fireSourceChanged(ISources.WORKBENCH, SESSION_STATE, currentState);
    }

}
