package edu.ualberta.med.biobank;

import java.net.URL;
import java.util.EventObject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SessionsView;
import edu.ualberta.med.biobank.webservice.Session;
import edu.ualberta.med.biobank.webservice.ISessionListener;
import edu.ualberta.med.biobank.webservice.LoginResultEvent;

/**
 * The activator class controls the plug-in life cycle
 */
public class BioBankPlugin extends AbstractUIPlugin implements ISessionListener {
	public static final String IMG_FORM_BG = "formBg";

	// The plug-in ID
	public static final String PLUGIN_ID = "biobank2";

	// The shared instance
	private static BioBankPlugin plugin;
	
	private Session wsSession;
	
	private SessionCredentials sessionCredentials;
	
	private SessionsView sessionView;
	
	/**
	 * The constructor
	 */
	public BioBankPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		wsSession = new Session();
		wsSession.addListener(this);
		wsSession.start();
	}
	
	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_FORM_BG, "form_banner.bmp");
	}

	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
		try {
			IPath path = new Path("icons/" + fileName);
			URL url = FileLocator.find(getBundle(), path, null);
			if (url!=null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);
				registry.put(key, desc);
			}
		} catch (Exception e) {
		}
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
	public static BioBankPlugin getDefault() {
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
	
	public void setSessionCredentials(SessionCredentials sc) {
		sessionCredentials = sc;
	}
	
	public SessionCredentials getSessionCredentials() {
		return sessionCredentials;
	}
 	
	public void createSession() {
		sessionView.createSession(getSessionCredentials());
	}

	public void eventHappened(EventObject event) {
		if (event instanceof LoginResultEvent) {
			LoginResultEvent loginResult = (LoginResultEvent)event;
			if (loginResult.getResult()) {		
				System.out.println("login successfull");
			}
			else {	
				System.out.println("login unsuccessfull");
			}
		}
	}
	
	public void setSessionView(SessionsView sessionView) {
		this.sessionView = sessionView;
	}
	
	public int getSessionCount() {
		return sessionView.getSessionCount(); 
	}
	
	public SessionAdapter getSessionNode(String sessionName) {
		return sessionView.getSessionNode(sessionName);
	}
	
	public SessionAdapter getSessionNode(int count) {
		return sessionView.getSessionNode(count);
	}


	public String[] getSessionNames() {
		return sessionView.getSessionNames();
	}
	
	public void createObject(final String sessionName, final Object o) throws Exception {
		sessionView.createObject(sessionName, o);
	}
	
	public void updateObject(final String sessionName, final Object o) throws Exception {
		sessionView.updateObject(sessionName, o);
	}
}
