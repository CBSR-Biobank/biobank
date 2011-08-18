package edu.ualberta.med.biobank.treeview.processing;

import java.util.ArrayList;
import java.util.Collection;
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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ProcessingEventGroup extends AdapterBase {

    public ProcessingEventGroup(AdapterBase parent, int id, String name) {
        super(parent, id, name, true, true);
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.getInstance().isConnected()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ProcessingEventGroup_pevent_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    ProcessingEventWrapper pEvent = new ProcessingEventWrapper(
                        SessionManager.getAppService());
                    pEvent.setCenter(SessionManager.getUser()
                        .getCurrentWorkingCenter());
                    ProcessingEventAdapter adapter = new ProcessingEventAdapter(
                        ProcessingEventGroup.this, pEvent);
                    adapter.openEntryForm();
                }
            });
        }
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
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ClinicWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ProcessingEventAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ProcessingEventWrapper);
        return new ProcessingEventAdapter(this, (ProcessingEventWrapper) child);
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

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<AdapterBase> children = getChildren();
        List<ModelWrapper<?>> wrappers = new ArrayList<ModelWrapper<?>>();
        for (AdapterBase child : children)
            wrappers.add(child.getModelObject());
        return wrappers;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren().size();
    }

}
