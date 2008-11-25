package edu.ualberta.med.biobank.webservice;

import java.util.EventObject;

public class LoginResultEvent extends EventObject {
	private boolean result;

	/**
	 * 
	 */
	private static final long serialVersionUID = -93493076305102318L;

	public LoginResultEvent(Object source) {
		super(source);
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean getResult() {
		return result;
	}
}
