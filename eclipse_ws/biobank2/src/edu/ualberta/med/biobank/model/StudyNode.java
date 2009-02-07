package edu.ualberta.med.biobank.model;

public class StudyNode extends WsObject {
	private Study study;
	
	public StudyNode(GroupNode<ClinicNode> parent, Study study) {
		super(parent);
		this.setStudy(study);
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public Study getStudy() {
		return study;
	}

	@Override
	public int getId() {
		return study.getId();
	}
}
