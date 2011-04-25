package edu.ualberta.med.biobank.treeview.processing;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class AssignAdapter extends AdapterBase {

    public AssignAdapter(AdapterBase parent, int id, String name,
        boolean hasChildren, boolean loadChildrenInBackground) {
        super(parent, id, name, hasChildren, loadChildrenInBackground);
    }

    @Override
    protected String getLabelInternal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTooltipText() {
        return "Scan Assign";
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AdapterBase createChildNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getViewFormId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntryFormId() {
        // TODO Auto-generated method stub
        return null;
    }

}
