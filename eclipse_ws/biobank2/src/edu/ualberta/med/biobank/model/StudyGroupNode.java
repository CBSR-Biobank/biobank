package edu.ualberta.med.biobank.model;

import java.util.ArrayList;

public class StudyGroupNode extends WsObject {
	private ArrayList<StudyNode> studies;
	
	public StudyGroupNode(SiteNode parent) {
		super(parent);
		setName("Studies");
	}
	
	public StudyNode[] getStudieNodes() {
		return (StudyNode[]) studies.toArray(new StudyNode[studies.size()]);
	}

	protected void fireChildrenChanged() {
		SiteNode parent = (SiteNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}

	@Override
	public int getId() {
		return 0;
	}
}
