package edu.ualberta.med.biobank.treeview.request;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public abstract class AbstractRequestGroup extends AdapterBase {

    protected CenterWrapper<?> center;

    public AbstractRequestGroup(AdapterBase parent, int id, String name,
        CenterWrapper<?> center) {
        super(parent, id, name, true, true);
        this.center = center;
    }

    @Override
    public void openViewForm() {
        Assert.isTrue(false, "should not be called"); //$NON-NLS-1$
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
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren() == null ? 0 : getWrapperChildren().size();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
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
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new RequestAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof RequestWrapper);
        return new RequestAdapter(this, (RequestWrapper) child);
    }

}
