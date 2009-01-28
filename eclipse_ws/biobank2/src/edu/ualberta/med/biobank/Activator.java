package edu.ualberta.med.biobank;

import java.util.EventObject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ualberta.med.biobank.session.SessionsView;
import edu.ualberta.med.biobank.webservice.Session;
import edu.ualberta.med.biobank.webservice.ISessionListener;
import edu.ualberta.med.biobank.webservice.LoginResultEvent;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements ISessionListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "biobank2";

	// The shared instance
	private static Activator plugin;
	
	private Session wsSession;
	
	private SessionCredentials sessionCredentials;
	
	private SessionsView sessionView;
	
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
		wsSession.addListener(this);
		wsSession.start();
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
	
	public void setSessionCredentials(SessionCredentials sc) {
		sessionCredentials = sc;
	}
	
	public SessionCredentials getSessionCredentials() {
		return sessionCredentials;
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
	
	public void createSession() {
		sessionView.createSession(getSessionCredentials());
	}
	
	public void addSession(final WritableApplicationService appService, final String name) {
		getLog().log(new Status(IStatus.WARNING,getBundle().getSymbolicName(),0,
				"session opened: " + name, null));
		try {
			sessionView.addSession(appService, name);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void addSessionFailed(final SessionCredentials sc) {
		getLog().log(new Status(IStatus.WARNING,getBundle().getSymbolicName(),0,
				"session failed: " + sc.getServer(), null));
		sessionView.loginFailed(sc);
	}
	
	public void setSessionView(SessionsView sessionView) {
		this.sessionView = sessionView;
	}
	
	public int getSessionCount() {
		return sessionView.getSessionCount(); 
	}

	public String[] getSessionNames() {
		return sessionView.getSessionNames();
	}
	
	public void createObject(final String sessionName, final Object o) throws Exception {
		sessionView.createObject(sessionName, o);
	}
}
