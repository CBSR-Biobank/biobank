package edu.ualberta.med.biobank.model;

public interface IStudiesListener {
	public void studiesChanged(Site studyGroup, Study study);
}
