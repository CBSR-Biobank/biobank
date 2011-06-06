package edu.ualberta.med.biobank.treeview.processing;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SpecimenAssignAdapter extends AdapterBase {

    public SpecimenAssignAdapter(AdapterBase parent, int id, String name,
        boolean hasChildren, boolean loadChildrenInBackground) {
        super(parent, id, name, hasChildren, loadChildrenInBackground);
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipText() {
        return "Specimen Assign";
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

}
