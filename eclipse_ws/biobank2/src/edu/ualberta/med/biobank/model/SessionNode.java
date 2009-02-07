package edu.ualberta.med.biobank.model;

import gov.nih.nci.system.applicationservice.WritableApplicationService;
import java.util.ArrayList;
import org.eclipse.core.runtime.Assert;

public class SessionNode extends WsObject {
	private ArrayList<SiteNode> siteNodes = null;
	
	private WritableApplicationService appService;
	
	public SessionNode(WritableApplicationService appService, String name) {
		super(null);
		this.appService = appService;
		setName(name);
		siteNodes = new ArrayList<SiteNode>();
	}
	
	public void addSite(Site site) {		
		if (containsSite(site.getId())) {
			// don't add - assume our model is up to date 
			return;
		}
		
		SiteNode siteNode = getNamedSite(site.getName());
		if (siteNode != null) {
			// may have inserted a new site into database
			siteNode.setSite(site);
			return;
		}
		
		siteNodes.add(new SiteNode(this, site));
	}

	public void removeSite(Site site) {
		SiteNode nodeToRemove = null;

		for (SiteNode node : siteNodes) {
			if (node.getSite().getId().equals(site.getId()))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			siteNodes.remove(nodeToRemove);
	}

	public void removeSiteByName(String name) {
		SiteNode nodeToRemove = null;

		for (SiteNode node : siteNodes) {
			if (node.getSite().getName().equals(name))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			siteNodes.remove(nodeToRemove);
	}
	
	/**
	 * Returns true if there is a site with the same ID.
	 * 
	 * @param site
	 * @return
	 */
	public boolean containsSite(int id) {		
		for (SiteNode node : siteNodes) {
			if (node.getSite().getId().equals(id)) return true;
		}
		return false;
	}
	
	public SiteNode getNamedSite(String name) {		
		for (SiteNode node : siteNodes) {
			if (node.getSite().getName().equals(name)) return node;
		}
		return null;
	}
	
	public SiteNode[] getSites() {
		if (siteNodes == null) {
			return new SiteNode[0];
		}
		return (SiteNode[]) siteNodes.toArray(new SiteNode[siteNodes.size()]);
	}
	
	public SiteNode getSite(int id) {		
		for (SiteNode node : siteNodes) {
			if (node.getSite().getId().equals(id)) return node;
		}
		Assert.isTrue(false, "node with id " + id + " not found");
		return null;
	}
	
	public WritableApplicationService getAppService() {
		return appService;
	}

	@Override
	public int getId() {
		return 0;
	}
}
