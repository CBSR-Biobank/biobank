package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class ClinicGroup extends WsObject {
	
	private ArrayList<Clinic> clinics;
	
	public ClinicGroup() {
		setName("Clinics");
	}
	
	public void addClinic(Clinic clinic) {
		if (clinics == null) {
			clinics = new ArrayList<Clinic>();
		}
		clinics.add(clinic);
		fireChildrenChanged();
	}

	public void removeClinic(Clinic clinic) {
		if (clinics != null) {
			clinics.remove(clinic);
			if (clinics.isEmpty())
				clinics = null;
		}
		fireChildrenChanged();
	}
	
	public Clinic[] getClinics() {
		if (clinics == null) {
			return new Clinic[0];
		}
		return (Clinic[]) clinics.toArray(new Clinic[clinics.size()]);
	}

	protected void fireChildrenChanged() {
		BioBank parent = (BioBank) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
