package edu.ualberta.med.biobank.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.BioBankPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = BioBankPlugin.getDefault()
			.getPreferenceStore();
		store.setDefault(PreferenceConstants.SCANNER_CONFIRM, "CONFIRM");
		store.setDefault(PreferenceConstants.SCANNER_CANCEL, "CANCEL");
		for (int i = 1; i <= PreferenceConstants.SCANNER_PLATE_NUMBER; i++) {
			store
				.setDefault(PreferenceConstants.SCANNER_PLATE + i, "PLATE" + i);
		}
	}
}
