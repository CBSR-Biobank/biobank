package edu.ualberta.med.biobank.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class ConfirmState extends AbstractSourceProvider {
    public final static String SESSION_STATE = "edu.ualberta.med.biobank.sourceprovider.confirmState"; //$NON-NLS-1$
    private final static String CONFIRM_ENABLED = "confirmEnabled"; //$NON-NLS-1$
    private final static String CONFIRM_DISABLED = "confirmDisabled"; //$NON-NLS-1$

    // true for debug, false for non-debug
    boolean state;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SESSION_STATE };
    }

    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> currentStateMap = new HashMap<String, String>(1);
        String currentState = state ? CONFIRM_ENABLED : CONFIRM_DISABLED;
        currentStateMap.put(SESSION_STATE, currentState);
        return currentStateMap;
    }

    @Override
    public void dispose() {
    }

    public void setState(boolean state) {
        if (this.state == state)
            return; // no change
        this.state = state;
        String currentState = state ? CONFIRM_ENABLED : CONFIRM_DISABLED;
        fireSourceChanged(ISources.WORKBENCH, SESSION_STATE, currentState);
    }

}
