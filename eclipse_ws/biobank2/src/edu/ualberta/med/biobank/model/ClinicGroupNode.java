package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class ClinicGroupNode extends WsObject {	
	private ArrayList<ClinicNode> clinicNodes;
	
	public ClinicGroupNode(SiteNode siteNode) {
		super(siteNode);
		setName("Clinics");
		clinicNodes = new ArrayList<ClinicNode>();
	}
	
	public void addClinic(Clinic clinic) {		
		if (containsClinic(clinic.getId())) {
			// don't add - assume our model is up to date 
			return;
		}
		
		ClinicNode clinicNode = getNamedClinic(clinic.getName());
		if (clinicNode != null) {
			// may have inserted a new site into database
			clinicNode.setClinic(clinic);
			return;
		}
		
		clinicNodes.add(new ClinicNode(this, clinic));
	}
	
	public void addChild(ClinicNode clinicNode) {
		clinicNodes.add(clinicNode);
	}
	
	public ClinicNode[] getClinicNodes() {
		return (ClinicNode[]) clinicNodes.toArray(new ClinicNode[clinicNodes.size()]);
	}
	
	public void removeClinic(Clinic clinic) {		
		ClinicNode nodeToRemove = null;

		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getId().equals(clinic.getId()) 
					|| node.getClinic().getName().equals(clinic.getName()))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			clinicNodes.remove(nodeToRemove);
	}

	public void removeClinicByName(String name) {	
		ClinicNode nodeToRemove = null;

		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getName().equals(name))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			clinicNodes.remove(nodeToRemove);
	}
	
	public boolean containsClinic(int id) {		
		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getId().equals(id)) return true;
		}
		return false;
	}
	
	public ClinicNode getNamedClinic(String name) {		
		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getName().equals(name)) return node;
		}
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}
}
