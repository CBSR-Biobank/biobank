package edu.ualberta.med.biobank.treeview.admin;

import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupDeletePermission;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupUpdatePermission;
import edu.ualberta.med.biobank.forms.ResearchGroupEntryForm;
import edu.ualberta.med.biobank.forms.ResearchGroupViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ResearchGroupAdapter extends AbstractNewAdapterBase {

    ResearchGroupAdapterInfo rg;

    public ResearchGroupAdapter(AbstractNewAdapterBase parent,
        ResearchGroupAdapterInfo rg) {
        super(parent, rg.id, rg.nameShort, null, false);
        this.rg = rg;
        if (rg.id == null) init();
    }

    @Override
    public void setValue(Object value) {
        this.rg = (ResearchGroupAdapterInfo) value;
        setId(rg.id);
        if (rg.id != null) init();
    }

    @Override
    public void init() {
        try {
            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new ResearchGroupDeletePermission(rg.id));
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new ResearchGroupReadPermission(rg.id));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new ResearchGroupUpdatePermission(rg.id));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    protected String getLabelInternal() {
        return rg.nameShort;
    }

    @Override
    public Integer getId() {
        return rg.id;
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.ResearchGroupAdapter_tooltip);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.ResearchGroupAdapter_menu_label);
        addViewMenu(menu, Messages.ResearchGroupAdapter_menu_label);
        addDeleteMenu(menu, Messages.ResearchGroupAdapter_menu_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.ResearchGroupAdapter_delete_confirm_msg;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AbstractNewAdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return ResearchGroupEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ResearchGroupViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ResearchGroupAdapter)
            return rg.id.compareTo(
                ((ResearchGroupAdapter) o).rg.id);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        // TODO: implement delete
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return null;
    }
}
