package edu.ualberta.med.biobank.treeview.admin;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.site.SiteDeletePermission;
import edu.ualberta.med.biobank.common.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.common.permission.site.SiteUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteAdapter extends AdapterBase {

    private int nodeIdOffset = 100;
    public static final int CONTAINER_TYPES_BASE_NODE_ID = 0;
    public static final int CONTAINERS_BASE_NODE_ID = 1;

    public SiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site);
        if (site != null && site.getId() != null) {
            nodeIdOffset *= site.getId();
        }
        createNodes();
    }

    @Override
    public void init() {
        try {
            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new SiteDeletePermission(getModelObject().getId()));
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new SiteReadPermission(getModelObject().getId()));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new SiteUpdatePermission(getModelObject().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    public ContainerTypeGroup getContainerTypesGroupNode() {
        ContainerTypeGroup adapter = (ContainerTypeGroup) getChild(nodeIdOffset
            + CONTAINER_TYPES_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return adapter;
    }

    public ContainerGroup getContainersGroupNode() {
        ContainerGroup adapter = (ContainerGroup) getChild(nodeIdOffset
            + CONTAINERS_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return adapter;
    }

    @Override
    protected String getLabelInternal() {
        SiteWrapper site = (SiteWrapper) getModelObject();
        Assert.isNotNull(site, "site is null"); //$NON-NLS-1$
        return site.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.SiteAdapter_tooltip_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.SiteAdapter_site_label);
        addViewMenu(menu, Messages.SiteAdapter_site_label);
        if (!getModelObject().equals(
            SessionManager.getUser().getCurrentWorkingCenter()))
            addDeleteMenu(menu, Messages.SiteAdapter_site_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.SiteAdapter_delete_confirm_msg;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        if (SiteWrapper.class.isAssignableFrom(searchedClass))
            return Arrays.asList((AbstractAdapterBase) this);
        return searchChildren(searchedClass, objectId);
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
    protected List<? extends ModelWrapper<?>> getWrapperChildren() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return SiteEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return SiteViewForm.ID;
    }

    public void createNodes() {
        addChild(new ContainerTypeGroup(this, nodeIdOffset
            + CONTAINER_TYPES_BASE_NODE_ID));
        addChild(new ContainerGroup(this, nodeIdOffset
            + CONTAINERS_BASE_NODE_ID));
    }

    @Override
    public void rebuild() {
        removeAll();
        createNodes();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof SiteAdapter)
            return internalCompareTo(o);
        return 0;
    }

    // disable MVP for version 3.2.0
    // @Override
    // public IEditorPart openEntryForm(boolean hasPreviousForm) {
    // eventBus.fireEvent(new SiteEditEvent(site.getId()));
    // return null; // TODO: problem !?
    // }

    // disable MVP for version 3.2.0
    // @Override
    // public void openViewForm() {
    // eventBus.fireEvent(new SiteViewEvent(site.getId()));
    // }
}