package edu.ualberta.med.biobank.widgets.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;

public class TreeFilter extends PatternFilter {

    @Override
    public boolean isElementVisible(Viewer viewer, Object element) {
        if (element instanceof AbstractAdapterBase
            && !(element instanceof CollectionEventAdapter)) {
            // load node children, except for PatientVisit : don't want to load
            // all samples only for filtering
            ((AbstractAdapterBase) element).loadChildren(false);
        }
        return super.isElementVisible(viewer, element);
    }
}
