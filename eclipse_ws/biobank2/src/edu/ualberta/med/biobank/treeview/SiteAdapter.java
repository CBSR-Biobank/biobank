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

    private static final int CLINICS_NODE_ID = 0;
    private static final int STUDIES_NODE_ID = 1;
    private static final int STORAGE_TYPES_NODE_ID = 2;
    private static final int STORAGE_CONTAINERS_NODE_ID = 3;

    private int currentClinicNodeId;
    private int currentStudyNodeId;
    private int currentContainerTypeNodeId;
    private int currentContainerNodeId;

    public SiteAdapter(AdapterBase parent, SiteWrapper siteWrapper,
        boolean enableActions) {
        super(parent, siteWrapper, enableActions, false);

        currentClinicNodeId = CLINICS_NODE_ID + 100 * siteWrapper.getId();
        currentStudyNodeId = STUDIES_NODE_ID + 100 * siteWrapper.getId();
        currentContainerTypeNodeId = STORAGE_TYPES_NODE_ID + 100
            * siteWrapper.getId();
        currentContainerNodeId = STORAGE_CONTAINERS_NODE_ID + 100
            * siteWrapper.getId();

        addChild(new ClinicGroup(this, currentClinicNodeId));
        addChild(new StudyGroup(this, currentStudyNodeId));
        addChild(new ContainerTypeGroup(this, currentContainerTypeNodeId));
        addChild(new ContainerGroup(this, currentContainerNodeId));
    }

    public SiteWrapper getWrapper() {
        return (SiteWrapper) modelObject;
    }

    public SiteAdapter(AdapterBase parent, SiteWrapper siteWrapper) {
        this(parent, siteWrapper, true);
    }

    public AdapterBase getStudiesGroupNode() {
        return children.get(STUDIES_NODE_ID);
    }

    public AdapterBase getClinicGroupNode() {
        return children.get(CLINICS_NODE_ID);
    }

    public AdapterBase getContainerTypesGroupNode() {
        return children.get(STORAGE_TYPES_NODE_ID);
    }

    public AdapterBase getContainersGroupNode() {
        return children.get(STORAGE_CONTAINERS_NODE_ID);
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
    public String getEntryFormId() {
        return SiteEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return SiteViewForm.ID;
    }

}