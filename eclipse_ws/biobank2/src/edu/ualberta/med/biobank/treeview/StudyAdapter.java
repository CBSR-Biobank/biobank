package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.model.Study;

public class StudyAdapter extends Node {
	private Study study;
	
	public StudyAdapter(Node parent, Study study) {
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
		Assert.isNotNull(study, "study is null");
		Object o = (Object) study.getId();
		if (o == null) return 0;
		return study.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(study, "study is null");
		Object o = (Object) study.getNameShort();
		if (o == null) return null;
		return study.getNameShort();
	}
}
