package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class ClinicGroupNode extends WsObject {	
	private ArrayList<ClinicNode> clinics;
	
	public ClinicGroupNode(SiteNode siteNode) {
		super(siteNode);
		setName("Clinics");
		clinics = new ArrayList<ClinicNode>();
	}
	
	public ClinicNode[] getClinicNodes() {
		return (ClinicNode[]) clinics.toArray(new ClinicNode[clinics.size()]);
	}

	protected void fireChildrenChanged() {
		SiteNode parent = (SiteNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
