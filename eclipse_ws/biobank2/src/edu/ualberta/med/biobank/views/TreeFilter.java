package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;

public class TreeFilter extends PatternFilter {

	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		if (element instanceof Node
				&& !(element instanceof PatientVisitAdapter)) {
			// load node children, except for PatientVisit : don't want to load
			// all samples only for filtering
			((Node) element).loadChildren();
		}
		return super.isElementVisible(viewer, element);
	}
}
