package edu.ualberta.med.biobank.webservice;

import java.util.EventObject;
import java.util.EventListener;

public interface ISessionListener extends EventListener {
	public void eventHappened(EventObject event);
}
