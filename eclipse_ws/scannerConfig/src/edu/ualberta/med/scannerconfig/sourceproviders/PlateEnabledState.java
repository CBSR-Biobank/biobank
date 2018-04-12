package edu.ualberta.med.scannerconfig.sourceproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class PlateEnabledState extends AbstractSourceProvider {

    public final static String PLATES_ENABLED = "edu.ualberta.med.scannerconfig.sourceprovider.platesEnabled"; //$NON-NLS-1$

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentStateMap = new HashMap<String, Object>(1);
        currentStateMap.put(PLATES_ENABLED, ScannerConfigPlugin.getPlatesEnabledCount() > 0);
        return currentStateMap;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { PLATES_ENABLED };
    }

    public void setPlateEnabled() {
        fireSourceChanged(ISources.WORKBENCH, PLATES_ENABLED,
            ScannerConfigPlugin.getPlatesEnabledCount() > 0);
    }

}
