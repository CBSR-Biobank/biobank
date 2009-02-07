package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.model.Clinic;


public class ClinicAdapter extends Node {
	private Clinic clinic;
	
	public ClinicAdapter(Node parent, Clinic clinic) {
		super(parent);
		this.clinic = clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Clinic getClinic() {
		return clinic;
	}
	
	public void addChild(Node child) {
		Assert.isTrue(false, "Cannot add children to this adapter");
	}

	@Override
	public int getId() {
		Assert.isNotNull(clinic, "Clinic is null");
		Object o = (Object) clinic.getId();
		if (o == null) return 0;
		return clinic.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(clinic, "Clinic is null");
		Object o = (Object) clinic.getId();
		if (o == null) return null;
		return clinic.getName();
	}
}
