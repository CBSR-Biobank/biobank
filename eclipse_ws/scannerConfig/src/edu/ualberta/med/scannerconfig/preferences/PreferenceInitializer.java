package edu.ualberta.med.scannerconfig.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    // private static final I18n i18n = I18nFactory.getI18n(PreferenceInitializer.class);

    @SuppressWarnings("nls")
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ScannerConfigPlugin.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.SCANNER_PALLET_PROFILES, "");
        store.setDefault(PreferenceConstants.SCANNER_BRIGHTNESS, 0);
        store.setDefault(PreferenceConstants.SCANNER_CONTRAST, 0);
        store.setDefault(PreferenceConstants.SCANNER_DRV_TYPE,
            PreferenceConstants.SCANNER_DRV_TYPE_NONE);

        store.setDefault(PreferenceConstants.LIBDMTX_MIN_EDGE_FACTOR, 0.2);
        store.setDefault(PreferenceConstants.LIBDMTX_MAX_EDGE_FACTOR, 0.4);
        store.setDefault(PreferenceConstants.LIBDMTX_SCAN_GAP_FACTOR, 0.15);

        store.setDefault(PreferenceConstants.LIBDMTX_EDGE_THRESH, 5);
        store.setDefault(PreferenceConstants.LIBDMTX_SQUARE_DEV, 15);
        store.setDefault(PreferenceConstants.LIBDMTX_CORRECTIONS, 10);

        for (String preference : PreferenceConstants.SCANNER_PALLET_ENABLED) {
            store.setDefault(preference, false);
        }
    }
}
