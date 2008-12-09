package edu.ualberta.med.biobank.model;

import java.util.Collection;

public class StudiesNode extends WsObject {
	private Site site;
	
	public StudiesNode(Site site) {
		this.site = site;
		setName("Studies");
	}
	
	public Study[] getStudies() {
		Collection<Study> collection = site.getStudyCollection(); 
		if (collection == null) {
			// call appService here to get
			return null;
		}
		return (Study[]) collection.toArray(new Study[collection.size()]);
	}

	protected void fireChildrenChanged() {
		SiteNode parent = (SiteNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
