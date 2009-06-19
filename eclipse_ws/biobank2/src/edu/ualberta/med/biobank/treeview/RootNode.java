package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

public class RootNode extends Node {

	private static Node instance;

	private RootNode() {
		super(null, 1, "root");
	}

	public static Node getRootNode() {
		if (instance == null) {
			instance = new RootNode();
		}
		return instance;
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
		return null;
	}

}
