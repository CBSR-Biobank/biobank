package edu.ualberta.med.biobank;

public class SessionCredentials {
	private String userName;
	private String server;
	private String password;
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getServer() {
		return server;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

}
