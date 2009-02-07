package edu.ualberta.med.biobank.model;

import org.eclipse.core.runtime.Assert;


public class ClinicNode extends WsObject {
	private Clinic clinic;
	
	public ClinicNode(GroupNode<ClinicNode> parent, Clinic clinic) {
		super(parent);
		this.clinic = clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Clinic getClinic() {
		return clinic;
	}
	
	public String getName() {
		Assert.isNotNull(clinic, "Clinic is null");
		return clinic.getName();
	}

	@Override
	public int getId() {
		Object o = (Object) clinic.getId();
		if (o == null) return 0;
		return clinic.getId();
	}
}
