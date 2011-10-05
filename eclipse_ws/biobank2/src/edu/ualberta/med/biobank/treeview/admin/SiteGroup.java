package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class SiteGroup extends AdapterBase {

    public SiteGroup(SessionAdapter parent, int id) {
        super(parent, id, Messages.SiteGroup_sites_node_label, true, false);
    }

    @Override
    public void openViewForm() {
        Assert.isTrue(false, "should not be called"); //$NON-NLS-1$
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(SiteWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.SiteGroup_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addSite();
                }
            });
        }
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, SiteWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new SiteAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof SiteWrapper);
        return new SiteAdapter(this, (SiteWrapper) child);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        if (SessionManager.isSuperAdminMode()) {
            return SiteQuery.getSites(SessionManager.getAppService());
        }
        return Collections.emptyList();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren().size();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    public void addSite() {
        SiteWrapper site = new SiteWrapper(getAppService());
        SiteAdapter adapter = new SiteAdapter(this, site);
        adapter.openEntryForm();
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
