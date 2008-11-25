package edu.ualberta.med.biobank.webservice;

import java.util.EventObject;

public class LoginEvent extends EventObject {
	private String url;
	private String userName;
	private String password;

	/**
	 * 
	 */
	private static final long serialVersionUID = -93493076305102318L;

	public LoginEvent(Object source) {
		super(source);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUserName(String login) {
		this.userName = login;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
}
