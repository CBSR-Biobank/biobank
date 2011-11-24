package edu.ualberta.med.biobank.common.action.scanprocess;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.common.action.scanprocess.messages"; //$NON-NLS-1$

	private Messages() {
	}

	public static String getString(String key, Locale locale) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME,
					locale);
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
