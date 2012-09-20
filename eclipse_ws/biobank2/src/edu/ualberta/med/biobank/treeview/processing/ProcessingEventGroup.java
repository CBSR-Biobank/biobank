package edu.ualberta.med.biobank.treeview.processing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ProcessingEventGroup extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ProcessingEventGroup.class);

    private boolean createAllowed;

    public ProcessingEventGroup(AdapterBase parent, int id, String name) {
        super(parent, id, name, true);

        this.createAllowed = false;

        CenterWrapper<?> center =
            SessionManager.getUser().getCurrentWorkingCenter();
        if (center != null) {
            this.createAllowed = isAllowed(
                new ProcessingEventCreatePermission(center.getId()));
        }
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(i18n.tr("Add processing event"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    ProcessingEventWrapper pEvent = new ProcessingEventWrapper(
                        SessionManager.getAppService());
                    pEvent.setCenter(SessionManager.getUser()
                        .getCurrentWorkingCenter());
                    ProcessingEventAdapter adapter =
                        new ProcessingEventAdapter(
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
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
            ProcessingEventWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ProcessingEventAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
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
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<AbstractAdapterBase> children = getChildren();
        List<ModelWrapper<?>> wrappers = new ArrayList<ModelWrapper<?>>();
        for (AbstractAdapterBase child : children)
            wrappers.add(((AdapterBase) child).getModelObject());
        return wrappers;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
