package edu.ualberta.med.biobank.event;

public interface Event<H extends EventHandler> {
	public Object getSource();
}
