package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetAllAction;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 *
 * Code Changes -
 * 		1> Extend AdapterBase like SiteGroup
 * 		2> Call the ResearchGroupGetAllAction to get the wrapper collection for all existing Research Groups
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupMasterGroup extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupMasterGroup.class);

    private Boolean createAllowed;

    @SuppressWarnings("nls")
    public ResearchGroupMasterGroup(SessionAdapter parent, int id) {
        super(parent, id, i18n.tr("All Research Groups"), true);
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new ResearchGroupCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }

    }

    @SuppressWarnings("nls")
    @Override
    public void openViewForm() {
        Assert.isTrue(false, "should not be called");
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add Research Group"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addResearchGroup();
                }
            });
        }
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass, Integer objectId) {
        return findChildFromClass(searchedClass, objectId, ResearchGroupWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ResearchGroupAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof ResearchGroupWrapper);
        return new ResearchGroupAdapter(this, (ResearchGroupWrapper) child);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren() throws Exception {
        List<ResearchGroup> researchGroups = SessionManager.getAppService().doAction(new ResearchGroupGetAllAction()).getList();
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),researchGroups, ResearchGroupWrapper.class);
    }


    public void addResearchGroup() {
	ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(SessionManager.getAppService());
	ResearchGroupAdapter adapter = new ResearchGroupAdapter(this, researchGroup);
        adapter.openEntryForm();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
