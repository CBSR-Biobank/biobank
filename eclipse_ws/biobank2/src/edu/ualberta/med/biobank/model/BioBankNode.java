package edu.ualberta.med.biobank.model;

public class BioBankNode extends WsObject {
	private BioBank bioBank;
	
	private WsObject[] children;

	public BioBankNode(BioBank bioBank) {
		this.bioBank = bioBank;
		children = new WsObject[] { new StudiesNode(bioBank), new ClinicsNode(bioBank) };
		children[0].setParent(this);
		children[1].setParent(this);
	}

	public BioBank getBioBank() {
		return bioBank;
	}
	
	public WsObject[] getChildren() {
		return children;
	}
	
	public ClinicsNode getClinicsNode() {
		return (ClinicsNode) children[0];
	}
	
	public StudiesNode getStudiesNode() {
		return (StudiesNode) children[1];
	}

	protected void fireChildrenChanged() {
		SessionNode parent = (SessionNode) getParent();
		if (parent == null) return; 
		parent.fireChildrenChanged(this);
	}
}
