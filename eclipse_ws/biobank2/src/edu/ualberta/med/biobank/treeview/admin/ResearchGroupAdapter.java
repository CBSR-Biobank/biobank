package edu.ualberta.med.biobank.treeview.admin;

import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.forms.ResearchGroupEntryForm;
import edu.ualberta.med.biobank.forms.ResearchGroupViewForm;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ResearchGroupAdapter extends AbstractNewAdapterBase {

    ResearchGroupAdapterInfo rg;
    
    public ResearchGroupAdapter(AbstractNewAdapterBase parent,
        ResearchGroupAdapterInfo rg) {
        super(parent, rg.id, rg.nameShort, null, false);
        this.rg=rg;
    }
    
    public void setValue(Object value) {
        
    }

    @Override
    protected String getLabelInternal() {
        return rg.nameShort;
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
    public boolean isDeletable() {
        return internalIsDeletable();
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
        //TODO: implement delete
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return null;
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return 0;
    }
}
