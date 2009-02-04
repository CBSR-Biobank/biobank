package edu.ualberta.med.biobank.model;

public class ClinicNode extends WsObject {
	private Clinic clinic;
	
	public ClinicNode(ClinicGroupNode parent, Clinic clinic) {
		super(parent);
		this.clinic = clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Clinic getClinic() {
		return clinic;
	}
}
