package edu.ualberta.med.biobank.model;

import org.springframework.util.Assert;

public class SiteNode extends WsObject {
	private Site site;
	
	private WsObject[] children;

	public SiteNode(SessionNode parent, Site site) {
		super(parent);
		this.site = site;
		children = new WsObject[] { 
				new GroupNode<StudyNode>(this, "Studies"), 
				new GroupNode<ClinicNode>(this, "Clinics"), 
				//new GroupNode<SiteNode, StorageTypeNode>(this, "Clinics")  
		};
		children[0].setParent(this);
		children[1].setParent(this);
	}

	public void setSite(Site site) {
		this.site = site;
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
	
	@SuppressWarnings("unchecked")
	public GroupNode<StudyNode> getStudiesGroupNode() {
		return (GroupNode<StudyNode>) children[0];
	}
	
	@SuppressWarnings("unchecked")
	public GroupNode<ClinicNode> getClinicGroupNode() {
		return (GroupNode<ClinicNode>) children[1];
	}

	@Override
	public int getId() {
		Assert.notNull(site, "Site is null");
		Object o = (Object) site.getId();
		if (o == null) return 0;
		return ((Integer) o).intValue();
	}
}
