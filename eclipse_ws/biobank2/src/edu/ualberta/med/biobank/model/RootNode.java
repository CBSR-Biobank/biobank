package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import java.util.Iterator;

public class RootNode extends WsObject {
	private ArrayList<SessionNode> sessions;
	
	public RootNode() {
		super(null);
		sessions = new ArrayList<SessionNode>();
	}
	
	public void addSessionNode(SessionNode sessionNode) {
		sessions.add(sessionNode);
	}	
	
	public void deleteSessionNode(String name) throws Exception {
		SessionNode nodeToDelete = null;
		
		for (Iterator<SessionNode> it = sessions.iterator(); it.hasNext(); ) {
			SessionNode node = it.next();
			if (node.getName().equals(name)) {
				nodeToDelete = node;
			}
		}
		if (nodeToDelete == null) {
			// no session with name found
			throw new RuntimeException();
		}
		
		sessions.remove(nodeToDelete);
	}
	
	public SessionNode[] getSessions() {
		if (sessions == null) {
			return new SessionNode[0];
		}
		return (SessionNode[]) sessions.toArray(new SessionNode[sessions.size()]);
	}
	
	public int getChildCount() {
		return sessions.size();
	}

	@Override
	public int getId() {
		return 0;
	}
}
