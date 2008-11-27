package edu.ualberta.med.biobank.model;

public interface IStudiesListener {
	public void studiesChanged(StudyGroup studyGroup, Study study);
}
