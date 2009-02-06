package edu.ualberta.med.biobank.model;

import org.springframework.util.Assert;

public class SiteNode extends WsObject {
	private Site site;
	
	private WsObject[] children;

	public SiteNode(SessionNode parent, Site site) {
		super(parent);
		this.site = site;
		children = new WsObject[] { new StudyGroupNode(this), new ClinicGroupNode(this) };
		children[0].setParent(this);
		children[1].setParent(this);
	}

	public Site getSite() {
		return site;
	}
	
	public String getName() {
		return site.getName();
	}
	
	public WsObject[] getChildren() {
		return children;
	}
	
	public StudyGroupNode getStudieGroupNode() {
		return (StudyGroupNode) children[0];
	}
	
	public ClinicGroupNode getClinicGroupNode() {
		return (ClinicGroupNode) children[1];
	}

	protected void fireChildrenChanged() {
		SessionNode parent = (SessionNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged(this);
	}

	@Override
	public int getId() {
		Assert.notNull(site, "Site is null");
		Object o = (Object) site.getId();
		if (o == null) return 0;
		return ((Integer) o).intValue();
	}
}
