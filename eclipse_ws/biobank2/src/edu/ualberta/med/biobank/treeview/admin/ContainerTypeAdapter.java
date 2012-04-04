package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeDeleteAction;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeDeletePermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.ContainerTypeViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeAdapter extends AdapterBase {

    public ContainerTypeAdapter(AdapterBase parent,
        ContainerTypeWrapper containerType) {
        super(parent, containerType);
    }

    @Override
    public void init() {
        try {
            ContainerTypeWrapper ctype =
                (ContainerTypeWrapper) getModelObject();
            Integer id = ctype.getId();
            if (id == null) return;

            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new ContainerTypeDeletePermission(id));
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new ContainerTypeReadPermission(ctype.getSite()
                        .getWrappedObject()));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new ContainerTypeUpdatePermission(id));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    protected String getLabelInternal() {
        ContainerTypeWrapper containerType =
            (ContainerTypeWrapper) getModelObject();
        Assert.isNotNull(containerType, "container type is null"); 
        return containerType.getName();
    }

    @Override
    public String getTooltipTextInternal() {
        ContainerTypeWrapper type = (ContainerTypeWrapper) getModelObject();
        if (type != null) {
            SiteWrapper site = type.getSite();
            if (site != null) {
                return site.getNameShort() + " - " 
                    + getTooltipText("Container Type");
            }
        }
        return getTooltipText("Container Type");

    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Container Type");
        addViewMenu(menu, "Container Type");
        addDeleteMenu(menu, "Container Type");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this container type?";
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
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return ContainerTypeEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ContainerTypeViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ContainerTypeAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        SessionManager.getAppService().doAction(
            new ContainerTypeDeleteAction((ContainerType) getModelObject()
                .getWrappedObject()));
        SessionManager.updateAllSimilarNodes(getParent(), true);
    }
}
