package edu.ualberta.med.biobank.treeview.admin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SiteAdapter extends AdapterBase {

    private int nodeIdOffset = 100;
    public static final int STUDIES_BASE_NODE_ID = 0;
    public static final int CLINICS_BASE_ID = 1;
    public static final int CONTAINER_TYPES_BASE_NODE_ID = 2;
    public static final int CONTAINERS_BASE_NODE_ID = 3;

    public SiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site, false);

        if (site != null && site.getId() != null) {
            nodeIdOffset *= site.getId();
        }

        createNodes();
    }

    public SiteWrapper getWrapper() {
        return (SiteWrapper) modelObject;
    }

    public ContainerTypeGroup getContainerTypesGroupNode() {
        AdapterBase adapter = getChild(nodeIdOffset
            + CONTAINER_TYPES_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return (ContainerTypeGroup) adapter;
    }

    public ContainerGroup getContainersGroupNode() {
        AdapterBase adapter = getChild(nodeIdOffset + CONTAINERS_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return (ContainerGroup) adapter;
    }

    @Override
    protected String getLabelInternal() {
        SiteWrapper site = getWrapper();
        Assert.isNotNull(site, "site is null"); //$NON-NLS-1$
        return site.getNameShort();
    }

    @Override
    public String getTooltipText() {
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
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        if (searchedObject instanceof SiteWrapper)
            return Arrays.asList((AdapterBase) this);
        return searchChildren(searchedObject);
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
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren() {
        return null;
    }

    @Override
    protected int getWrapperChildCount() {
        return 0;
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

}