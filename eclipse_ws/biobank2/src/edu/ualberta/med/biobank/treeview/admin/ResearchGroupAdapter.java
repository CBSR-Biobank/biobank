package edu.ualberta.med.biobank.treeview.admin;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupDeleteAction;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupDeletePermission;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.forms.ResearchGroupEntryForm;
import edu.ualberta.med.biobank.forms.ResearchGroupViewForm;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

/**
 *
 * Code Changes -
 * 		1> Extend AdapterBase like SiteAdapter
 * 		2> Call the ResearchGroupDeleteAction for deleting a Research Group
 * 		3> Get the Research Group from the Model Object
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupAdapter.class);

    public ResearchGroupAdapter(AdapterBase parent, ResearchGroupWrapper rg) {
        super(parent, rg);
    }

    @Override
    public void init() {
        this.isDeletable = isAllowed(new ResearchGroupDeletePermission(getModelObject().getId()));
        this.isReadable = isAllowed(new ResearchGroupReadPermission(getModelObject().getId()));
        this.isEditable = isAllowed(new ResearchGroupUpdatePermission(getModelObject().getId()));
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {

        ResearchGroupWrapper rg = (ResearchGroupWrapper) getModelObject();
        Assert.isNotNull(rg, "ResearchGroup is null");
        return rg.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(ResearchGroup.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, ResearchGroup.NAME.singular().toString());
        addViewMenu(menu, ResearchGroup.NAME.singular().toString());
        addDeleteMenu(menu, ResearchGroup.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        return i18n.tr("Are you sure you want to delete this research group?");
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass, Integer objectId) {
        if (ResearchGroupWrapper.class.isAssignableFrom(searchedClass))
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
        return ResearchGroupEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ResearchGroupViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ResearchGroupAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        ResearchGroupDeleteAction delete = new ResearchGroupDeleteAction((ResearchGroup) getModelObject().getWrappedObject());
        SessionManager.getAppService().doAction(delete);
    }
}