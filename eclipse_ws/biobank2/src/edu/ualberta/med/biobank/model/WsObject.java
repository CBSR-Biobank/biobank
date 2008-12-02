package edu.ualberta.med.biobank.model;

public class WsObject {
	private WsObject parent;
	private String name;
	
	public void setParent(WsObject parent) {
		this.parent = parent;
	}
	public WsObject getParent() {
		return parent;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
