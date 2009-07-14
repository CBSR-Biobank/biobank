package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.model.SampleType;

public class SampleTypeAdapter extends Node {

	private SampleType sampleType;

	public SampleTypeAdapter(Node parent, SampleType sampleType) {
		super(parent);
		this.sampleType = sampleType;
		setHasChildren(true);
	}

	public SampleType getSampleType() {
		return sampleType;
	}

	public void setSampleType(SampleType sampleType) {
		this.sampleType = sampleType;
	}

	@Override
	public Integer getId() {
		Assert.isNotNull(sampleType, "patient is null");
		return sampleType.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(sampleType, "storage type is null");
		return sampleType.getNameShort();
	}

	@Override
	public void performDoubleClick() {
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

	}

	@Override
	public void loadChildren(boolean updateNode) {
	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return visitor.visit(this);
	}

}
