package edu.ualberta.med.biobank.model;

public class StudyNode extends WsObject {
	private Study study;
	
	public StudyNode(ClinicGroupNode parent, Study study) {
		super(parent);
		this.setStudy(study);
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public Study getStudy() {
		return study;
	}
}
