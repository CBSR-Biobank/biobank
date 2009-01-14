package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.PlatformObject;

public class NavigatorRoot extends PlatformObject {
	private ArrayList<SessionNode> sessions;
	
	public NavigatorRoot() {
		sessions = new ArrayList<SessionNode>();
	}
	
	public void addSessionNode(SessionNode sessionNode) {
		sessions.add(sessionNode);
	}	
	
	public void deleteSessionNode(String name) throws Exception {
		for (Iterator<SessionNode> it = sessions.iterator(); it.hasNext(); ) {
			SessionNode node = it.next();
			if (node.getName().equals(name)) {
				sessions.remove(node);
			}
		}
		// no session with name found
		throw new Exception();
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
}
