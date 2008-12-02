package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class StudyGroup extends WsObject {
	
	private ArrayList<Study> studies;
	
	public StudyGroup() {
		setName("Studies");
	}
	
	public void addStudy(Study study) {
		if (studies == null) {
			studies = new ArrayList<Study>();
		}
		studies.add(study);
		fireChildrenChanged();
	}

	public void removeStudy(Study study) {
		if (studies != null) {
			studies.remove(study);
			if (studies.isEmpty())
				studies = null;
		}
		fireChildrenChanged();
	}
	
	public Study[] getStudies() {
		if (studies == null) {
			return new Study[0];
		}
		return (Study[]) studies.toArray(new Study[studies.size()]);
	}

	protected void fireChildrenChanged() {
		BioBank parent = (BioBank) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
