package edu.ualberta.med.biobank.model;

import java.util.Collection;

public class StudiesNode extends WsObject {
	private BioBank bioBank;
	
	public StudiesNode(BioBank bioBank) {
		this.bioBank = bioBank;
		setName("Studies");
	}
	
	public Study[] getStudies() {
		Collection<Study> collection = bioBank.getStudyCollection(); 
		return (Study[]) collection.toArray(new Study[collection.size()]);
	}

	protected void fireChildrenChanged() {
		BioBankNode parent = (BioBankNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged();
	}
}
