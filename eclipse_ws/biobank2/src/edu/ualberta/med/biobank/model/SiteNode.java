package edu.ualberta.med.biobank.model;

public class SiteNode extends WsObject {
	private Site site;
	
	private WsObject[] children;

	public SiteNode(Site site) {
		this.site = site;
		children = new WsObject[] { new StudiesNode(site), new ClinicsNode(site) };
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
	
	public ClinicsNode getClinicsNode() {
		return (ClinicsNode) children[0];
	}
	
	public StudiesNode getStudiesNode() {
		return (StudiesNode) children[1];
	}

	protected void fireChildrenChanged() {
		SessionNode parent = (SessionNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged(this);
	}
}
