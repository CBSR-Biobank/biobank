package edu.ualberta.med.biobank.treeview.processing;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SpecimenAssignAdapter extends AdapterBase {

    public SpecimenAssignAdapter(AdapterBase parent, int id, String name,
        boolean hasChildren) {
        super(parent, id, name, hasChildren);
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipTextInternal() {
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
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }

}
