package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class ClinicGroupNode extends WsObject {	
	private ArrayList<ClinicNode> clinicNodess;
	
	public ClinicGroupNode(SiteNode siteNode) {
		super(siteNode);
		setName("Clinics");
		clinicNodess = new ArrayList<ClinicNode>();
	}
	
	public void addClinic(Clinic clinic) {
		// is site has already been added, get rid of old one
		if (!clinicNodess.isEmpty())
			removeClinic(clinic);
		
		ClinicNode node = new ClinicNode(this, clinic);
		addChild(node);
	}
	
	public void addChild(ClinicNode clinicNode) {
		clinicNodess.add(clinicNode);
		fireChildrenChanged();
	}
	
	public ClinicNode[] getClinicNodes() {
		return (ClinicNode[]) clinicNodess.toArray(new ClinicNode[clinicNodess.size()]);
	}

	protected void fireChildrenChanged() {
		SiteNode parent = (SiteNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
	
	public void removeClinic(Clinic clinic) {
		if (clinicNodess == null) return;
		
		ClinicNode nodeToRemove = null;

		for (ClinicNode node : clinicNodess) {
			if (node.getClinic().getId().equals(clinic.getId()) 
					|| node.getClinic().getName().equals(clinic.getName()))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			clinicNodess.remove(nodeToRemove);
	}

	@Override
	public int getId() {
		return 0;
	}
}
