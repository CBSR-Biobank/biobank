package edu.ualberta.med.biobank.treeview.admin;

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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetContainerTypeInfoAction;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ContainerTypeGroup extends AdapterBase {

    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger.getLogger(ContainerGroup.class
        .getName());

    private List<SiteContainerTypeInfo> containerTypeInfos = null;

    private final Boolean createAllowed;

    public ContainerTypeGroup(SiteAdapter parent, int id) {
        super(parent, id, ContainerType.NAME.plural().toString(), true);

        this.createAllowed =
            isAllowed(new ContainerTypeCreatePermission(parent.getId()));
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
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Container Type");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addContainerType(ContainerTypeGroup.this
                        .getParentFromClass(SiteAdapter.class), false);
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
        return findChildFromClass(searchedClass, objectId,
            ContainerTypeWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ContainerTypeAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof ContainerTypeWrapper);
        return new ContainerTypeAdapter(this, (ContainerTypeWrapper) child);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter siteAdapter = (SiteAdapter) getParent();
        containerTypeInfos = SessionManager.getAppService().doAction(
            new SiteGetContainerTypeInfoAction(siteAdapter.getId())).getList();

        List<ContainerType> containerTypes = new ArrayList<ContainerType>();
        for (SiteContainerTypeInfo info : containerTypeInfos) {
            containerTypes.add(info.getContainerType());
        }

        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            containerTypes, ContainerTypeWrapper.class);
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    public void addContainerType(SiteAdapter siteAdapter,
        boolean hasPreviousForm) {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(
            SessionManager.getAppService());
        ct.setSite((SiteWrapper) siteAdapter.getModelObject());
        ContainerTypeAdapter adapter = new ContainerTypeAdapter(
            siteAdapter.getContainerTypesGroupNode(), ct);
        adapter.openEntryForm(hasPreviousForm);
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
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
