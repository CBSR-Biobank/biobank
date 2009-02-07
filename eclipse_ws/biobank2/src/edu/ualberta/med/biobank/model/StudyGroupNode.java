package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class StudyGroupNode extends WsObject {
	private ArrayList<StudyNode> studies;
	
	public StudyGroupNode(SiteNode parent) {
		super(parent);
		setName("Studies");
		studies = new ArrayList<StudyNode>();
	}
	
	public StudyNode[] getStudieNodes() {
		return (StudyNode[]) studies.toArray(new StudyNode[studies.size()]);
	}
	
	@Override
	public int getId() {
		return 0;
	}
}
