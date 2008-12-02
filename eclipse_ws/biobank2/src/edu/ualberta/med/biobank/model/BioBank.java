package edu.ualberta.med.biobank.model;

import gov.nih.nci.system.applicationservice.ApplicationService;

public class BioBank extends WsObject {
	private ApplicationService appService;	
	
	private StudyGroup studyGroup;
	
	private ClinicGroup clinicGroup;
	
	public BioBank(ApplicationService appService, String name) {
		this.appService = appService;
		setName(name);
		studyGroup = new StudyGroup();
		clinicGroup = new ClinicGroup();
	}
	
	public void addStudy(Study study) {
		studyGroup.addStudy(study);
	}
	
	public void addClinic(Clinic clinic) {
		clinicGroup.addClinic(clinic);
	}

	protected void fireChildrenChanged() {
		BioBankGroup parent = (BioBankGroup) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged(null);
	}
	
	public WsObject[] getChildren() {
		WsObject[] result = new WsObject[2];
		result[0] = studyGroup;
		result[1] = clinicGroup;
		return result;
	}
}
