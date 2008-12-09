package edu.ualberta.med.biobank.model;

import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.ArrayList;
import org.eclipse.core.runtime.ListenerList;

public class SessionNode extends WsObject {
	private ArrayList<SiteNode> siteNodes;
	
	private ListenerList listeners;
	
	private ApplicationService appService;
	
	public SessionNode(ApplicationService appService, String name) {
		this.appService = appService;
		setName(name);
	}
	
	public void addSite(Site site) {
		if (siteNodes == null) {
			siteNodes = new ArrayList<SiteNode>();
		}
		SiteNode siteNode = new SiteNode(site);
		siteNode.setParent(this);
		siteNodes.add(siteNode);
		fireChildrenChanged(null);
	}

	public void removeSite(Site site) {
		if (siteNodes != null) {
			siteNodes.remove(site);
			if (siteNodes.isEmpty())
				siteNodes = null;
		}
	}	
	
	public SiteNode[] getSites() {
		if (siteNodes == null) {
			return new SiteNode[0];
		}
		return (SiteNode[]) siteNodes.toArray(new SiteNode[siteNodes.size()]);
	}


	public void addListener(ISessionNodeListener listener) {
		if (listeners == null)
			listeners = new ListenerList();
		listeners.add(listener);
	}

	public void removeListener(ISessionNodeListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireChildrenChanged(SiteNode siteNode) {
		if (listeners == null) return;
		
		for (Object l : listeners.getListeners()) {
			((ISessionNodeListener) l).sessionChanged(this, siteNode);
		}
	}
}
