package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;

public class SiteAdapter extends AdapterBase {

    private final String DEL_CONFIRM_MSG = "Are you sure you want to delete this repository site?";

    public static final int CLINICS_BASE_NODE_ID = 0;
    public static final int STUDIES_BASE_NODE_ID = 1;
    public static final int STORAGE_BASE_TYPES_NODE_ID = 2;
    public static final int STORAGE_BASE_CONTAINERS_NODE_ID = 3;

    public SiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site, false);

        int nodeIdOffset = 100;
        if (site != null && site.getId() != null) {
            nodeIdOffset *= site.getId();
        }

        addChild(new ClinicGroup(this, nodeIdOffset + CLINICS_BASE_NODE_ID));
        addChild(new StudyGroup(this, nodeIdOffset + STUDIES_BASE_NODE_ID));
        addChild(new ContainerTypeGroup(this, nodeIdOffset
            + STORAGE_BASE_TYPES_NODE_ID));
        addChild(new ContainerGroup(this, nodeIdOffset
            + STORAGE_BASE_CONTAINERS_NODE_ID));
    }

    public SiteWrapper getWrapper() {
        return (SiteWrapper) modelObject;
    }

    public AdapterBase getStudiesGroupNode() {
        return children.get(STUDIES_BASE_NODE_ID);
    }

    public AdapterBase getClinicGroupNode() {
        return children.get(CLINICS_BASE_NODE_ID);
    }

    public AdapterBase getContainerTypesGroupNode() {
        return children.get(STORAGE_BASE_TYPES_NODE_ID);
    }

    public AdapterBase getContainersGroupNode() {
        return children.get(STORAGE_BASE_CONTAINERS_NODE_ID);
    }

    @Override
    protected String getLabelInternal() {
        SiteWrapper site = getWrapper();
        Assert.isNotNull(site, "site is null");
        return site.getNameShort();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Repository Site");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Site");
        addViewMenu(menu, "Site");
        addDeleteMenu(menu, "Site", DEL_CONFIRM_MSG);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return DEL_CONFIRM_MSG;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
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
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
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

}