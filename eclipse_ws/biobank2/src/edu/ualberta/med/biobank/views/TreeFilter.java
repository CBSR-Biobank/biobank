package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.ualberta.med.biobank.treeview.Node;

public class TreeFilter extends ViewerFilter {

	private Object selection;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (selection == null) {
			return true;
		}
		if (element instanceof Node) {
			Node node = (Node) element;
			return node.isSameCompositeObject(selection);
		}
		return false;
	}

	public void setSelection(Object selection) {
		this.selection = selection;
	}

	public void clear() {
		selection = null;
	}

}
