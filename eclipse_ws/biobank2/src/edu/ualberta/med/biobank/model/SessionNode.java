package edu.ualberta.med.biobank.model;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import org.eclipse.core.runtime.ListenerList;

public class SessionNode extends WsObject {
	private ArrayList<SiteNode> siteNodes = null;
	
	private ListenerList listeners;
	
	private WritableApplicationService appService;
	
	public SessionNode(WritableApplicationService appService, String name) {
		this.appService = appService;
		setName(name);
	}
	
	public void addSite(Site site) {
		if (siteNodes == null) {
			siteNodes = new ArrayList<SiteNode>();
		}
		
		// is site has already been added, get rid of old one
		if (!siteNodes.isEmpty())
			removeSite(site);
		
		SiteNode siteNode = new SiteNode(site);
		siteNode.setParent(this);
		siteNodes.add(siteNode);
		fireChildrenChanged(null);
	}

	public void removeSite(Site site) {
		if (siteNodes == null) return;

		for (SiteNode node : siteNodes) {
			if (node.getSite().getId().equals(site.getId())) 
				siteNodes.remove(node);
		}
		
		if (siteNodes.isEmpty())
			siteNodes = null;
	}
	
	public boolean containsSite(Site site) {
		if (siteNodes == null) return false;
		
		for (SiteNode node : siteNodes) {
			if (node.getSite().getId().equals(site.getId())) return true;
		}
		return false;
	}
	
	public SiteNode[] getSites() {
		if (siteNodes == null) {
			return new SiteNode[0];
		}
		return (SiteNode[]) siteNodes.toArray(new SiteNode[siteNodes.size()]);
	}
	
	public WritableApplicationService getAppService() {
		return appService;
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
