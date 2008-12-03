package edu.ualberta.med.biobank.model;

import java.util.Collection;

public class ClinicsNode extends WsObject {
	private BioBank bioBank;
	
	public ClinicsNode(BioBank bioBank) {
		this.bioBank = bioBank;
		setName("Clinics");
	}
	
	public Clinic[] getClinics() {
		Collection<Clinic> collection = bioBank.getClinicCollection(); 
		return (Clinic[]) collection.toArray(new Clinic[collection.size()]);
	}

	protected void fireChildrenChanged() {
		BioBankNode parent = (BioBankNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
