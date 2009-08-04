package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;

public class TreeFilter extends PatternFilter {

	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		if (element instanceof AdaptorBase
				&& !(element instanceof PatientVisitAdapter)) {
			// load node children, except for PatientVisit : don't want to load
			// all samples only for filtering
			((AdaptorBase) element).loadChildren(false);
		}
		return super.isElementVisible(viewer, element);
	}
}
