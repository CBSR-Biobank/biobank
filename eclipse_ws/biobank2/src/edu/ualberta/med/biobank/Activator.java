package edu.ualberta.med.biobank;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import edu.ualberta.med.biobank.webservice.Session;
import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "biobank2";

	// The shared instance
	private static Activator plugin;
	
	Session wsSession;
	
	public static final String DLG_SETTINGS_SECTION = "edu.ualberta.med.biobank";
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		wsSession = new Session();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public Session getWsSession() {
		return wsSession;
	}
	
	public void setWsUserData(String userName, String server) {
		IDialogSettings settings = getDialogSettings().getSection(DLG_SETTINGS_SECTION);
		if (settings == null) {
			settings = getDialogSettings().addNewSection(DLG_SETTINGS_SECTION);
		}
		settings.put("server", server);
	}
	
	public String getWsUserData() {
		IDialogSettings settings = getDialogSettings().getSection(DLG_SETTINGS_SECTION);
		if (settings == null) return null;
		return settings.get("server");
	}
}
