package edu.ualberta.med.biobank.webservice;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;
import org.eclipse.core.runtime.ListenerList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;

public class Controller implements Runnable {	
	private ApplicationService appService;
	private ListenerList listenerList;
	private LinkedBlockingQueue<EventObject> eventQ;
	
	public Controller() {
		eventQ = new LinkedBlockingQueue<EventObject>();
	}
	
	public void addListener(EventListener l) {
		listenerList.add(l);		
	}
	
	public void login(String url, String userName, String password) {
		LoginEvent event = new LoginEvent(this);
		event.setUrl(url);
		event.setUserName(userName);
		event.setPassword(password);

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
			((IControllerListener) listeners[i]).eventHappened(event);
		}

	}
}
