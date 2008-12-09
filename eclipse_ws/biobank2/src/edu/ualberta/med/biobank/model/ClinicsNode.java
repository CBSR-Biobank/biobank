package edu.ualberta.med.biobank.model;

import java.util.Collection;

public class ClinicsNode extends WsObject {
	private Site site;
	
	public ClinicsNode(Site site) {
		this.site = site;
		setName("Clinics");
	}
	
	public Clinic[] getClinics() {
		Collection<Clinic> collection = site.getClinicCollection(); 
		return (Clinic[]) collection.toArray(new Clinic[collection.size()]);
	}

	protected void fireChildrenChanged() {
		SiteNode parent = (SiteNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
