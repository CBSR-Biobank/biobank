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
import edu.ualberta.med.biobank.forms.input.FormInput;

public class SiteAdapter extends AdapterBase {

    public static final int CLINICS_NODE_ID = 0;
    public static final int STUDIES_NODE_ID = 1;
    public static final int STORAGE_TYPES_NODE_ID = 2;
    public static final int STORAGE_CONTAINERS_NODE_ID = 3;

    public SiteAdapter(AdapterBase parent, SiteWrapper siteWrapper,
        boolean enableActions) {
        super(parent, siteWrapper, enableActions, false);

        if (enableActions) {
            addChild(new ClinicGroup(this, CLINICS_NODE_ID));
            addChild(new StudyGroup(this, STUDIES_NODE_ID));
            addChild(new ContainerTypeGroup(this, STORAGE_TYPES_NODE_ID));
            addChild(new ContainerGroup(this, STORAGE_CONTAINERS_NODE_ID));
        }
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
    public String getName() {
        SiteWrapper site = getWrapper();
        if (site == null) {
            return "loading...";
        }
        Assert.isNotNull(site, "site is null");
        return site.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Site");
    }

    @Override
    public void executeDoubleClick() {
        if (enableActions) {
            openForm(new FormInput(this), SiteViewForm.ID);
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Site", SiteEntryForm.ID);
        addViewMenu(menu, "Site", SiteViewForm.ID);
        addDeleteMenu(menu, "Site",
            "Are you sure you want to delete this site?");
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

}