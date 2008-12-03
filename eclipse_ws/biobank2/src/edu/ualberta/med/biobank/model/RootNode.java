package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class RootNode extends WsObject {
	private ArrayList<SessionNode> sessions;
	
	public RootNode() {
		sessions = new ArrayList<SessionNode>();
	}
	
	public void addSessionNode(SessionNode sessionNode) {
		sessions.add(sessionNode);
	}	
	
	public SessionNode[] getSessions() {
		if (sessions == null) {
			return new SessionNode[0];
		}
		return (SessionNode[]) sessions.toArray(new SessionNode[sessions.size()]);
	}
}
