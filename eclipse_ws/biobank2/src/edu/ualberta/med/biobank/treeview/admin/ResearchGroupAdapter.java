package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.forms.ResearchGroupEntryForm;
import edu.ualberta.med.biobank.forms.ResearchGroupViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ResearchGroupAdapter extends AdapterBase {

    public ResearchGroupAdapter(AdapterBase parent,
        ResearchGroupWrapper researchGroupWrapper) {
        super(parent, researchGroupWrapper);
    }

    public ResearchGroupWrapper getWrapper() {
        return (ResearchGroupWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        ResearchGroupWrapper wrapper = getWrapper();
        Assert.isNotNull(wrapper, "client is null");
        return wrapper.getNameShort();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Research Group");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Research Group");
        addViewMenu(menu, "Research Group");
        addDeleteMenu(menu, "Research Group");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this researchGroup?";
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
        return ResearchGroupEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ResearchGroupViewForm.ID;
    }

}
