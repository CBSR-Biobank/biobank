package edu.ualberta.med.biobank.model;

public interface IStudiesListener {
	public void studiesChanged(BioBank studyGroup, Study study);
}
