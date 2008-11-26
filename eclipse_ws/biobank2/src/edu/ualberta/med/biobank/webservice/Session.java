package edu.ualberta.med.biobank.webservice;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;
import org.eclipse.core.runtime.ListenerList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;
import edu.ualberta.med.biobank.SessionCredentials;

/**
 * Thread that invokes API to caCORE generated application. 
 * 
 *
 */
public class Session extends Thread {	
	private ApplicationService appService;
	private ListenerList listenerList;
	private LinkedBlockingQueue<EventObject> eventQ;
	
	public Session() {
		eventQ = new LinkedBlockingQueue<EventObject>();
		listenerList = new ListenerList();
	}
	
	public void addListener(EventListener l) {
		listenerList.add(l);		
	}
	
	public void login(SessionCredentials sc) {
		LoginEvent event = new LoginEvent(this);
		event.setUrl(sc.getServer());
		event.setUserName(sc.getUserName());
		event.setPassword(sc.getPassword());

		try {		
			eventQ.put(event);
		}
		catch (InterruptedException e) {
			System.out.println("Web Service Controller Interrupted.");
		}
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			EventObject event;
			try {
				event = eventQ.take();
				
				if (event instanceof LoginEvent) {
					LoginEvent login = (LoginEvent) event;
					doLogin(login.getUrl(), login.getUserName(), login.getPassword());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doLogin(String url, String userName, String password) {
		LoginResultEvent event = new LoginResultEvent(this);
		
		try {
			appService = ApplicationServiceProvider.getApplicationServiceFromUrl(
					url, userName,password);
			event.setResult(true);
		}
		catch (Exception e) {
			event.setResult(false);	
		}
		
		informListeners(event);
	}
	
	private void informListeners(EventObject event) {
		Object[] listeners = listenerList.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ISessionListener) listeners[i]).eventHappened(event);
		}

	}
}
