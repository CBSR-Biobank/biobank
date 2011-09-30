package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public abstract class AbstractClinicGroup extends AdapterBase {

    public AbstractClinicGroup(AdapterBase parent, int id, String name) {
        super(parent, id, name, true, true);
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        //
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ClinicWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ClinicAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof ClinicWrapper);
        return new ClinicAdapter(this, (ClinicWrapper) child);
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

}
